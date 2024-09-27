package seng202.team6.managers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Stream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team6.dao.DAO;
import seng202.team6.dao.GeoLocationDAO;
import seng202.team6.dao.UserDAO;
import seng202.team6.dao.WineDAO;
import seng202.team6.dao.WineListDAO;
import seng202.team6.dao.WineNotesDAO;
import seng202.team6.dao.WineReviewDAO;

public class NewDatabaseManager {
  private static final Logger log = LogManager.getLogger(NewDatabaseManager.class);
  private final Connection connection;
  private final UserDAO userDAO;
  private final WineDAO wineDAO;
  private final WineListDAO wineListDAO;
  private final WineNotesDAO wineNotesDAO;
  private final WineReviewDAO wineReviewDAO;
  private final GeoLocationDAO geoLocationDAO;

  public NewDatabaseManager() throws SQLException {
    this(setupInMemoryConnection());
  }

  public NewDatabaseManager(String directoryName, String fileName) throws SQLException {
    this(setupPersistentConnection(directoryName, fileName));
  }

  private NewDatabaseManager(Connection connection) {
    this.connection = connection;
    this.userDAO = new UserDAO(connection);
    this.wineDAO = new WineDAO(connection);
    this.wineListDAO = new WineListDAO(connection);
    this.wineNotesDAO = new WineNotesDAO(connection);
    this.wineReviewDAO = new WineReviewDAO(connection);
    this.geoLocationDAO = new GeoLocationDAO(connection);
  }

  public void init() {
    List<String> sqlStatements = Stream.of(userDAO, wineDAO, wineListDAO, wineNotesDAO, wineReviewDAO,
            geoLocationDAO)
        .map(DAO::getInitialiseStatements)
        .filter(Objects::nonNull)
        .flatMap(Arrays::stream)
        .toList();
    try (Statement statement = connection.createStatement()) {
      for (String sql : sqlStatements) {
        statement.execute(sql);
      }
    } catch (SQLException e) {
      log.error("Failed to initialise a Data Access Object", e);
      throw new RuntimeException(e);
    }
  }

  public void teardown() {
    try {
      connection.close();
    } catch (SQLException error) {
      log.error("Failed to close the database connection", error);
    }
  }

  private static Connection setupConnection(String jdbcURL) throws SQLException {
    Properties properties = new Properties();
    properties.setProperty("foreign_keys", "true");
    return DriverManager.getConnection(jdbcURL, properties);
  }
  private static Connection setupInMemoryConnection() throws SQLException {
    return setupConnection("jdbc:sqlite::memory:");
  }
  private static Connection setupPersistentConnection(String directoryName, String fileName) throws SQLException {
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
}
