package seng202.team6.managers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidParameterException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Stream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team6.dao.AggregatedDao;
import seng202.team6.dao.Dao;
import seng202.team6.dao.GeoLocationDao;
import seng202.team6.dao.UserDao;
import seng202.team6.dao.VineyardDao;
import seng202.team6.dao.VineyardTourDao;
import seng202.team6.dao.WineDao;
import seng202.team6.dao.WineListDao;
import seng202.team6.dao.WineNotesDao;
import seng202.team6.dao.WineReviewDao;
import seng202.team6.service.VineyardDefaultsService;
import seng202.team6.service.WineDataStatService;
import seng202.team6.util.PasswordUtil;

/**
 * Manages the creation, initialization, and teardown of a database. Provides methods for setting up
 * an in-memory or persistent SQLite database connection and initializes Daos (Data Access Objects)
 * for interacting with different database tables.
 */
public class DatabaseManager {

  private static final Logger log = LogManager.getLogger(DatabaseManager.class);
  private final Connection connection;
  private final UserDao userDao;
  private final WineDao wineDao;
  private final WineListDao wineListDao;
  private final VineyardDao vineyardsDao;
  private final WineNotesDao wineNotesDao;
  private final WineReviewDao wineReviewDao;
  private final GeoLocationDao geoLocationDao;
  private final VineyardTourDao vineyardTourDao;
  private final AggregatedDao aggregatedDao;
  private final WineDataStatService wineDataStatService;

  /**
   * Constructs a NewDatabaseManager with an in-memory SQLite database connection.
   *
   * @throws SQLException if a database access error occurs
   */
  public DatabaseManager() throws SQLException {
    this(setupInMemoryConnection(), true);
  }

  /**
   * Constructs a NewDatabaseManager with a persistent SQLite database connection.
   *
   * @param directoryName the directory to store the database file
   * @param fileName      the name of the database file
   * @throws SQLException if a database access error occurs
   */
  public DatabaseManager(String directoryName, String fileName) throws SQLException {
    this(setupPersistentConnection(directoryName, fileName), false);
  }

  /**
   * Private constructor for NewDatabaseManager that initializes Daos using the provided database
   * connection.
   *
   * @param connection the database connection to use
   */
  private DatabaseManager(Connection connection, boolean inMemory) throws SQLException {
    if (connection == null || connection.isClosed()) {
      throw new InvalidParameterException("The provided connection was invalid or closed");
    }
    this.connection = connection;
    log.info("Successfully opened a connection to the database");
    this.wineDataStatService = new WineDataStatService();
    this.userDao = new UserDao(connection);
    this.wineDao = new WineDao(connection, wineDataStatService);
    this.wineListDao = new WineListDao(connection);
    this.vineyardsDao = new VineyardDao(connection);
    this.wineNotesDao = new WineNotesDao(connection);
    this.wineReviewDao = new WineReviewDao(connection);
    this.geoLocationDao = new GeoLocationDao(connection);
    this.vineyardTourDao = new VineyardTourDao(connection);
    this.aggregatedDao = new AggregatedDao(connection, wineReviewDao, wineNotesDao, wineDao);
    init();

    VineyardDefaultsService vineyardDefaultsService = new VineyardDefaultsService(geoLocationDao,
        vineyardsDao, !inMemory);
    vineyardDefaultsService.init();
  }

  /**
   * Sets up a database connection using the given JDBC URL.
   *
   * @param jdbcUrl the JDBC URL to connect to
   * @return a Connection object to the database
   * @throws SQLException if a database access error occurs
   */
  private static Connection setupConnection(String jdbcUrl) throws SQLException {
    Properties properties = new Properties();
    properties.setProperty("foreign_keys", "true");
    return DriverManager.getConnection(jdbcUrl, properties);
  }

  /**
   * Sets up an in-memory SQLite database connection.
   *
   * @return a Connection object to an in-memory SQLite database
   * @throws SQLException if a database access error occurs
   */
  private static Connection setupInMemoryConnection() throws SQLException {
    return setupConnection("jdbc:sqlite::memory:");
  }

  /**
   * Sets up a persistent SQLite database connection. If the directory does not exist, it is
   * created.
   *
   * @param directoryName the directory to store the database file
   * @param fileName      the name of the database file
   * @return a Connection object to the SQLite database
   * @throws SQLException     if a database access error occurs
   * @throws RuntimeException if the directory cannot be created
   */
  private static Connection setupPersistentConnection(String directoryName, String fileName)
      throws SQLException {
    Path directory = Path.of(directoryName);
    if (Files.notExists(directory)) {
      try {
        Files.createDirectories(directory);
      } catch (IOException error) {
        log.error("Failed to create the database directory", error);
        throw new RuntimeException(error);
      }
    }
    return setupConnection("jdbc:sqlite:" + directoryName + File.separator + fileName);
  }

  /**
   * Initializes the database by executing SQL statements required to set up the tables. The SQL
   * statements are fetched from each Dao.
   *
   * @throws RuntimeException if any SQL execution fails
   */
  public void init() {
    List<String> sqlStatements = Stream.of(userDao, wineDao, wineListDao, wineNotesDao,
            wineReviewDao, geoLocationDao, vineyardsDao, vineyardTourDao)
        .filter(Objects::nonNull)  // Filter out null Daos
        .map(Dao::getInitialiseStatements)
        .filter(Objects::nonNull)  // Filter out null statements
        .flatMap(Arrays::stream)
        .toList();

    String salt = PasswordUtil.generateSalt();
    String hashedAdminPassword = PasswordUtil.hashPassword("admin", salt);
    List<String> triggersAndDefaultStatements = List.of(
        "CREATE TRIGGER IF NOT EXISTS FAVOURITES_LIST"
            + "AFTER INSERT ON USER "
            + "FOR EACH ROW "
            + "BEGIN "
            + "INSERT INTO LIST_NAME (USERNAME, NAME) "
            + "VALUES (NEW.USERNAME, 'Favourites'); "
            + "END",
        "CREATE TRIGGER IF NOT EXISTS HISTORY_LIST"
            + "AFTER INSERT ON USER "
            + "FOR EACH ROW "
            + "BEGIN "
            + "INSERT INTO LIST_NAME (USERNAME, NAME) "
            + "VALUES (NEW.USERNAME, 'History'); "
            + "END",
        "INSERT INTO USER (USERNAME, PASSWORD, ROLE, SALT) "
            + "SELECT 'admin', '" + hashedAdminPassword + "', 'admin', '" + salt + "' "
            + "WHERE NOT EXISTS ("
            + "SELECT 1 FROM USER WHERE USERNAME = 'admin')"
    );

    try (Statement statement = connection.createStatement()) {
      for (String sql : sqlStatements) {
        statement.execute(sql);
      }
      for (String sql : triggersAndDefaultStatements) {
        statement.execute(sql);
      }
    } catch (SQLException e) {
      log.error("Failed to initialise a Data Access Object", e);
      throw new RuntimeException(e);
    }
    log.info("Successfully executed {} initialise statements", sqlStatements.size());
    geoLocationDao.addDefaultGeoLocations();
  }

  /**
   * Tears down the database by closing the connection. Logs an error if the connection fails to
   * close.
   */
  public void teardown() {
    try {
      connection.close();
      log.info("Successfully closed the database connection");
    } catch (SQLException error) {
      log.error("Failed to close the database connection", error);
    }
  }

  public UserDao getUserDao() {
    return userDao;
  }

  public WineDao getWineDao() {
    return wineDao;
  }

  public WineListDao getWineListDao() {
    return wineListDao;
  }

  public WineNotesDao getWineNotesDao() {
    return wineNotesDao;
  }

  public WineReviewDao getWineReviewDao() {
    return wineReviewDao;
  }

  public GeoLocationDao getGeoLocationDao() {
    return geoLocationDao;
  }

  public VineyardDao getVineyardsDao() {
    return vineyardsDao;
  }

  public VineyardTourDao getVineyardTourDao() {
    return vineyardTourDao;
  }

  public AggregatedDao getAggregatedDao() {
    return aggregatedDao;
  }

  /**
   * Callback to set the attribute to update.
   */
  public interface AttributeSetter {

    /**
     * Updates the prepared statement with the value to set. Attribute must be index 1 in prepared
     * statement.
     */
    void setAttribute(PreparedStatement statement) throws SQLException;
  }

}
