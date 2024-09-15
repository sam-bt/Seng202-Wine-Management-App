package seng202.team0.managers;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.Logger;
import seng202.team0.database.User;
import seng202.team0.database.Wine;
import seng202.team0.util.Password;
import org.apache.logging.log4j.LogManager;


/**
 * Mediates access to the database
 */
public class DatabaseManager implements AutoCloseable {
  /** Logger for the DatabaseManager class */
  private final Logger log = LogManager.getLogger(getClass());

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
        log.error("Error creating database directory");
        throw new RuntimeException("Failed to create database");
      }
    }

    String dbPath = "jdbc:sqlite:sqlDatabase" + File.separator + databaseFileName;
    this.connection = DriverManager.getConnection(dbPath);
    createWinesTable();
    createUsersTable();
    createWineListsTable();
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
    createWineListsTable();
  }

  /**
   * Creates the WINE table if it does not exist
   *
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
   * @throws SQLException if a database error occurs
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
   * @throws SQLException if a database error occurs
   */
  public void replaceAllWines(List<Wine> list) throws SQLException {
    removeWines();
    addWines(list);
  }

  /**
   * Removes all wines from the database
   *
   * @throws SQLException if a database error occurs
   */
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
   * @throws SQLException if a database error occurs
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
   * Creates the USER table if it does not exist
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

  /**
   * Creates a default admin user if it does not exist
   * @throws SQLException if a database error occurs
   */
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

  /**
   * Retrieves a user from the database by username
   *
   * @param username the username of the user
   * @return the User object if found, null otherwise
   */
  public User getUser(String username) {
    String query = "SELECT * FROM USER WHERE USERNAME = ?";

    try (PreparedStatement statement = connection.prepareStatement(query)) {
      statement.setString(1, username);
      ResultSet set = statement.executeQuery();

      if (set.next()) {
        return new User(set.getString("USERNAME"),set.getString("PASSWORD"),set.getString("ROLE"),set.getString("SALT"));
      }
    } catch (SQLException e) {
      log.error("Database error occurred: {}", e.getMessage(), e);
    }
    return null;
  }

  /**
   * Adds a new user to the database
   *
   * @param username the username of the new user
   * @param password the password of the new user
   * @param salt the salt used for password hashing
   * @return true if the user was successfully added, false otherwise
   */
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
        log.error("Duplicate username: {}", username, e);
      } else {
        log.error("Database error occurred: {}", e.getMessage(), e);
      }
      return false;
    }
  }

  /**
   * Updates the password for an existing user
   *
   * @param username the username of the user
   * @param password the new hashed password
   * @param salt the salt used for password hashing
   * @return true if the user was successfully added, false otherwise
   */
  public boolean updatePassword(String username, String password, String salt) {
    String updateQuery = "UPDATE USER SET PASSWORD = ?, SALT = ? WHERE USERNAME = ?";

    try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
      updateStatement.setString(1, password);
      updateStatement.setString(2, salt);
      updateStatement.setString(3, username);

      int rowsAffected = updateStatement.executeUpdate();
      return rowsAffected > 0;
    } catch (SQLException e) {
      log.error("Error updating password: {}", e.getMessage(), e);
      return false;
    }
  }

  /**
   * Deletes all users from the database except the admin user.
   *
   * @return true if all non-admin users were successfully deleted, false otherwise.
   */
  public boolean deleteAllUsers() {
    String deleteQuery = "delete from USER "
        + "WHERE USERNAME != ?;";
    try (PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {
      deleteStatement.setString(1, "admin");
      deleteStatement.executeUpdate();
      return true;
    } catch (SQLException e) {
      log.error("Error deleting users: {}", e.getMessage(), e);
      return false;
    }
  }

  private void createWineListsTable() throws SQLException {
    String listNameTable = "create table if not exists LIST_NAME (" +
            "ID integer primary key," +
            "USERNAME varchar(32) not null," +
            "NAME varchar(10) not null);";
    String listItemsTable = "create table if not exists LIST_ITEMS (" +
            "ID integer primary key," +
            "LIST_ID int not null," +
            "WINE_ID int not null);";
    try (Statement statement = connection.createStatement()) {
      statement.execute(listNameTable);
    }

    try (Statement statement = connection.createStatement()) {
      statement.execute(listItemsTable);
    }
  }

  private void createAdminFavouritesList() {
    String checkAndInsert = "INSERT INTO LIST_NAME (ID, USERNAME, NAME) values (null, admin, Favourites) " +
            "SELECT 1" +
            "WHERE NOT EXISTS (SELECT 1 FROM LIST_NAME WHERE username = admin)";
    try (Statement statement = connection.createStatement()) {
      statement.execute(checkAndInsert);
    } catch (SQLException error) {
      log.error("Could not add list to the database", error);
    }
  }

  public void createList(String username, String listName) {
    String create = "insert into LIST_NAME (ID, USERNAME, NAME) values (null, ?, ?)";
    try (PreparedStatement statement = connection.prepareStatement(create)) {
      statement.setString(1, username);
      statement.setString(2, listName);
      statement.execute();
    } catch (SQLException error ) {
      log.error("Could not add list to the database", error);
    }
  }

  public void deleteList(String username) {
    String delete = "delete from LIST_NAME where USERNAME = ?";
    try (PreparedStatement statement = connection.prepareStatement(delete)) {
      statement.setString(1, username);
      statement.executeUpdate();
    } catch (SQLException error ) {
      log.error("Could not delete a list from the database", error);
    }
  }

  public List<String> getUserLists(String username) {
    List<String> listNames = new ArrayList<>();
    String query = "select ID, NAME from WINE_LISTS where USERNAME = ?;";
    try (PreparedStatement statement = connection.prepareStatement(query)) {
      statement.setString(1, username);

      ResultSet set = statement.executeQuery();
      while (set.next()) {
        listNames.add(set.getString("NAME"));
      }
    } catch (SQLException error) {
      log.error("Could not read user lists from the database", error);
    }
    return listNames;
  }

  /**
   * Closes the database connection
   * <p>
   *   This method is called automatically when the DatabaseManager is used in a try-with-resources statement
   * </p>
   */
  @Override
  public void close() {
    try {
      connection.close();
      connection = null;

    } catch (SQLException e) {
      log.error("Error closing connection", e);
    }
  }
}
