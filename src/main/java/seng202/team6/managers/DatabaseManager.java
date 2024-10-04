package seng202.team6.managers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidParameterException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.stream.Stream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team6.dao.AggregatedDAO;
import seng202.team6.dao.DAO;
import seng202.team6.dao.GeoLocationDAO;
import seng202.team6.dao.UserDAO;
import seng202.team6.dao.VineyardDAO;
import seng202.team6.dao.WineDAO;
import seng202.team6.dao.WineListDAO;
import seng202.team6.dao.WineNotesDAO;
import seng202.team6.dao.WineReviewDAO;
import seng202.team6.service.VineyardDefaultsService;
import seng202.team6.util.EncryptionUtil;
import seng202.team6.util.Timer;

/**
 * Manages the creation, initialization, and teardown of a database. Provides methods for setting up
 * an in-memory or persistent SQLite database connection and initializes DAOs (Data Access Objects)
 * for interacting with different database tables.
 */
public class DatabaseManager {

  private static final Logger log = LogManager.getLogger(DatabaseManager.class);
  private final Connection connection;
  private final UserDAO userDAO;
  private final WineDAO wineDAO;
  private final WineListDAO wineListDAO;
  private final VineyardDAO vineyardsDAO;
  private final WineNotesDAO wineNotesDAO;
  private final WineReviewDAO wineReviewDAO;
  private final GeoLocationDAO geoLocationDAO;
  private final AggregatedDAO aggregatedDAO;

  /**
   * Constructs a NewDatabaseManager with an in-memory SQLite database connection.
   *
   * @throws SQLException if a database access error occurs
   */
  public DatabaseManager() throws SQLException {
    this(setupInMemoryConnection());
  }

  /**
   * Constructs a NewDatabaseManager with a persistent SQLite database connection.
   *
   * @param directoryName the directory to store the database file
   * @param fileName      the name of the database file
   * @throws SQLException if a database access error occurs
   */
  public DatabaseManager(String directoryName, String fileName) throws SQLException {
    this(setupPersistentConnection(directoryName, fileName));
  }

  /**
   * Private constructor for NewDatabaseManager that initializes DAOs using the provided database
   * connection.
   *
   * @param connection the database connection to use
   */
  private DatabaseManager(Connection connection) throws SQLException {
    if (connection == null || connection.isClosed()) {
      throw new InvalidParameterException("The provided connection was invalid or closed");
    }
    this.connection = connection;
    log.info("Successfully opened a connection to the database");
    this.userDAO = new UserDAO(connection);
    this.wineDAO = new WineDAO(connection);
    this.wineListDAO = new WineListDAO(connection);
    this.vineyardsDAO = new VineyardDAO(connection);
    this.wineNotesDAO = new WineNotesDAO(connection);
    this.wineReviewDAO = new WineReviewDAO(connection);
    this.geoLocationDAO = new GeoLocationDAO(connection);
    this.aggregatedDAO = new AggregatedDAO(connection, wineNotesDAO, wineDAO);
    init();
  }

  /**
   * Sets up a database connection using the given JDBC URL.
   *
   * @param jdbcURL the JDBC URL to connect to
   * @return a Connection object to the database
   * @throws SQLException if a database access error occurs
   */
  private static Connection setupConnection(String jdbcURL) throws SQLException {
    Properties properties = new Properties();
    properties.setProperty("foreign_keys", "true");
    return DriverManager.getConnection(jdbcURL, properties);
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
   * statements are fetched from each DAO.
   *
   * @throws RuntimeException if any SQL execution fails
   */
  public void init() {
    List<String> sqlStatements = Stream.of(userDAO, wineDAO, wineListDAO, vineyardsDAO, wineNotesDAO,
            wineReviewDAO, geoLocationDAO)
        .filter(Objects::nonNull)  // Filter out null DAOs
        .map(DAO::getInitialiseStatements)
        .filter(Objects::nonNull)  // Filter out null statements
        .flatMap(Arrays::stream)
        .toList();

    String salt = EncryptionUtil.generateSalt();
    String hashedAdminPassword = EncryptionUtil.hashPassword("admin", salt);
    List<String> triggersAndDefaultStatements = List.of(
        "CREATE TRIGGER IF NOT EXISTS FAVOURITES_LIST" +
            "AFTER INSERT ON USER " +
            "FOR EACH ROW " +
            "BEGIN " +
            "INSERT INTO LIST_NAME (USERNAME, NAME) " +
            "VALUES (NEW.USERNAME, 'Favourites'); " +
            "INSERT INTO LIST_NAME (USERNAME, NAME) " +
            "VALUES (NEW.USERNAME, 'History'); " +
            "END",
        "INSERT INTO USER (USERNAME, PASSWORD, ROLE, SALT) " +
            "SELECT 'admin', '" + hashedAdminPassword + "', 'admin', '" + salt + "' " +
            "WHERE NOT EXISTS (" +
            "SELECT 1 FROM USER WHERE USERNAME = 'admin')"
    );

    try (Statement statement = connection.createStatement()) {
      statement.execute("DROP TABLE VINEYARD");      // todo - remove this, for debugging purposes only
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
    geoLocationDAO.addDefaultGeoLocations();

    VineyardDefaultsService vineyardDefaultsService = new VineyardDefaultsService(geoLocationDAO,
        vineyardsDAO);
    vineyardDefaultsService.init();
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

  public Set<String> getDistinctStringValues(String attribute, String tableName) {
    Set<String> values = new HashSet<>();
    Timer timer = new Timer();
    String sql = "SELECT DISTINCT " + attribute + " FROM " + tableName;
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      try (ResultSet resultSet = statement.executeQuery()) {
        while (resultSet.next()) {
          values.add(resultSet.getString(1));
        }
        log.error("Successfully retrieved {} distinct values from {} in table {}",
            values.size(), attribute, tableName);
      }
    } catch (SQLException error) {
      log.error("Failed to select distinct values from {} in table {}", attribute,
          tableName);
    }
    return values;
  }

  public UserDAO getUserDAO() {
    return userDAO;
  }

  public WineDAO getWineDAO() {
    return wineDAO;
  }

  public WineListDAO getWineListDAO() {
    return wineListDAO;
  }

  public VineyardDAO getVineyardsDAO() {
    return vineyardsDAO;
  }

  public WineNotesDAO getWineNotesDAO() {
    return wineNotesDAO;
  }

  public WineReviewDAO getWineReviewDAO() {
    return wineReviewDAO;
  }

  public GeoLocationDAO getGeoLocationDAO() {
    return geoLocationDAO;
  }

  public AggregatedDAO getAggregatedDAO() {
    return aggregatedDAO;
  }

  /**
   * Callback to set the attribute to update
   */
  public interface AttributeSetter {

    /**
     * Updates the prepared statement with the value to set
     * <p>
     * Attribute must be index 1 in prepared statement
     * </p>
     */
    void setAttribute(PreparedStatement statement) throws SQLException;
  }

}
