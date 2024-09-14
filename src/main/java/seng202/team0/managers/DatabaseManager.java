package seng202.team0.managers;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seng202.team0.database.User;
import seng202.team0.database.Wine;
import seng202.team0.util.Password;
import org.apache.logging.log4j.LogManager;


/**
 * Mediates access to the database
 *
 */
public class DatabaseManager implements AutoCloseable {

  /**
   * Database connection
   * <p>
   * This is ensured to be always valid
   * </p>
   */
  private Connection connection;

  /**
   * Connects to a db file for management. The path to the file is specified by dbpath
   * <p>
   *   This method will fail if application is opened from a directory without appropriate file perms
   * </p>
   * @throws SQLException if failed to initialize
   */
  public DatabaseManager(String databaseFileName) throws SQLException {

    // Construct a file path for the database
    File dir = new File("sqlDatabase");
    if (!dir.exists()) {
      boolean created = dir.mkdirs();

      if (!created) {
        LogManager.getLogger(getClass()).error("Error creating database directory");
        throw new RuntimeException("Failed to create database");
      }
    }

    // Connect to database
    // This is the path to the db file
    String dbPath = "jdbc:sqlite:sqlDatabase" + File.separator + databaseFileName;
    this.connection = DriverManager.getConnection(dbPath);
    createWinesTable();
    createUsersTable();
  }


  /**
   * Creates an in-memory database for testing
   *
   * @throws SQLException if failed to initialize
   */
  public DatabaseManager() throws SQLException {
    this.connection = DriverManager.getConnection("jdbc:sqlite::memory:");
    createWinesTable();
    createUsersTable();
  }

  /**
   * @throws SQLException on sql error
   */
  private void createWinesTable() throws SQLException {
    String create = "create table if not exists WINE (" +
        // There are a lot of duplicates
        "ID AUTO_INCREMENT PRIMARY KEY," +
        "TITLE varchar(64) NOT NULL," +
        "VARIETY varchar(32)," +
        "COUNTRY varchar(32)," +
        "REGION varchar(32)," +
        "WINERY varchar(64)," +
        "DESCRIPTION text," +
        "SCORE_PERCENT int," +
        "ABV float," +
        "PRICE float);";
    try (Statement statement = connection.createStatement()) {
      statement.execute(create);
    }
  }

  /**
   * Gets a subset of the wines in the database
   * <p>
   * The order of elements should remain stable until a write operation occurs.
   * </p>
   *
   * @param begin beginning element
   * @param end   end element (begin + size)
   * @return subset list of wines
   */
  public ObservableList<Wine> getWinesInRange(int begin, int end) {

    ObservableList<Wine> wines = FXCollections.observableArrayList();
    String query = "select TITLE, VARIETY, COUNTRY, REGION, WINERY, DESCRIPTION, SCORE_PERCENT, ABV, PRICE from WINE order by ROWID limit ? offset ?;";
    try (PreparedStatement statement = connection.prepareStatement(query)) {

      statement.setInt(1, end - begin);
      statement.setInt(2, begin);

      ResultSet set = statement.executeQuery();
      while (set.next()) {

        Wine wine = new Wine(
            set.getString("TITLE"),
            set.getString("VARIETY"),
            set.getString("COUNTRY"),
            set.getString("REGION"),
            set.getString("WINERY"),
            set.getString("DESCRIPTION"),
            set.getInt("SCORE_PERCENT"),
            set.getFloat("ABV"),
            set.getFloat("PRICE")
        );
        wines.add(wine);
      }

    } catch (SQLException e) {
      throw new RuntimeException(e);
    }

    return wines;
  }

  /**
   * Gets the number of wine records
   *
   * @return total number of wine records
   */
  public int getWinesSize() throws SQLException {
    try (Statement statement = connection.createStatement()) {
      ResultSet set = statement.executeQuery("select count(*) from WINE;");
      set.next();
      return set.getInt(1);
    }
  }


  /**
   * Replaces all wines in the database with a new list
   *
   * @param list list of wines
   */
  public void replaceAllWines(List<Wine> list) throws SQLException {
    removeWines();
    addWines(list);
  }

  public void removeWines() throws SQLException {
    String delete = "delete from WINE;";
    try (Statement statement = connection.createStatement()) {
      statement.executeUpdate(delete);
    }
  }

  /**
   * Adds the wines in the list to the database
   *
   * @param list list of wines
   */
  public void addWines(List<Wine> list) throws SQLException {
    // null key is auto generated
    String insert = "insert into WINE values(null, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
    try (PreparedStatement insertStatement = connection.prepareStatement(insert)) {
      for (Wine wine : list) {
        insertStatement.setString(1, wine.getTitle());
        insertStatement.setString(2, wine.getVariety());
        insertStatement.setString(3, wine.getCountry());
        insertStatement.setString(4, wine.getRegion());
        insertStatement.setString(5, wine.getWinery());
        insertStatement.setString(6, wine.getDescription());
        insertStatement.setInt(7, wine.getScorePercent());
        insertStatement.setFloat(8, wine.getAbv());
        insertStatement.setFloat(9, wine.getPrice());
        insertStatement.executeUpdate();
      }

    }
  }

  /**
   * @throws SQLException on sql error
   */
  private void createUsersTable() throws SQLException {
    String create = "create table if not exists USER (" +
        "USERNAME varchar(64) PRIMARY KEY," +
        "PASSWORD varchar(64) NOT NULL," +
        "ROLE varchar(8) NOT NULL," +
        "SALT varchar(32))";
    try (Statement statement = connection.createStatement()) {
      statement.execute(create);
    }
    createDefaultAdminUser();
  }

  private void createDefaultAdminUser() throws SQLException {
    String checkAndInsert = "INSERT INTO USER (username, password, role, salt) " +
        "SELECT ?, ?, ?, ? " +
        "WHERE NOT EXISTS (SELECT 1 FROM USER WHERE username = ?)";
    try (PreparedStatement statement = connection.prepareStatement(checkAndInsert)) {
      String salt = Password.generateSalt();
      String password = Password.hashPassword("admin", salt);
      statement.setString(1, "admin");
      statement.setString(2, password);
      statement.setString(3, "admin");
      statement.setString(4, salt);
      statement.setString(5, "admin");
      statement.executeUpdate();
    }
  }

  public User getUser(String username) {
    User user;
    String query = "SELECT * FROM USER WHERE USERNAME = ?";

    try (PreparedStatement statement = connection.prepareStatement(query)) {
      statement.setString(1, username);
      ResultSet set = statement.executeQuery();

      if (set.next()) {
        user = new User(set.getString("USERNAME"),set.getString("PASSWORD"),set.getString("ROLE"),set.getString("SALT"));
      } else {
        return null;
      }
    } catch (SQLException e) {
      LogManager.getLogger(getClass()).error("Database error occurred: {}", e.getMessage(), e);
      return null;
    }
    return user;
  }


  public boolean addUser(String username, String password, String salt) {
    String insert = "insert into USER values(?, ?, ?, ?);";
    try (PreparedStatement insertStatement = connection.prepareStatement(insert)) {
      insertStatement.setString(1, username);
      insertStatement.setString(2, password);
      insertStatement.setString(3, "user");
      insertStatement.setString(4, salt);
      insertStatement.executeUpdate();
      return true;
    } catch (SQLException e) {
      if (e.getMessage().contains("PRIMARY KEY")) {
        LogManager.getLogger(getClass()).error("Duplicate username: {}", username, e);
        return false;
      } else {
        LogManager.getLogger(getClass()).error("Database error occurred: {}", e.getMessage(), e);
        return false;
      }
    }
  }

  public boolean updatePassword(String username, String password, String salt) {
    String updateQuery = "UPDATE USER SET PASSWORD = ?, SALT = ? WHERE USERNAME = ?";

    try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
      updateStatement.setString(1, password);
      updateStatement.setString(2, salt);
      updateStatement.setString(3, username);

      int rowsAffected = updateStatement.executeUpdate();

      return rowsAffected > 0;
    } catch (SQLException e) {
      LogManager.getLogger(getClass()).error("Error updating password: {}", e.getMessage(), e);
      return false;
    }
  }

  public boolean deleteAllUsers() {
    String deleteQuery = "delete from USER "
        + "WHERE USERNAME != ?;";
    try (PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {
      deleteStatement.setString(1, "admin");
      deleteStatement.executeUpdate();
      return true;

    } catch (SQLException e) {
      LogManager.getLogger(getClass()).error("Error deleting users: {}", e.getMessage(), e);
      return false;
    }
  }

  /**
   * To disconnect/close database
   */
  @Override
  public void close() {
    try {
      this.connection.close();

      // Reset connection
      connection = null;

    } catch (SQLException e) {
      LogManager.getLogger(getClass()).error("Error closing connection", e);
    }
  }
}
