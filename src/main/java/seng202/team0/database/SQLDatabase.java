package seng202.team0.database;

import com.sun.scenario.DelayedRunnable;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import seng202.team0.database.exceptions.DuplicateTableException;
import seng202.team0.database.exceptions.TableNotFoundException;

/**
 * SQL Version of Database
 * Uses JDBC
 *
 * @author mpe133
 */
public class SQLDatabase extends Database {

  // This is the path to the db file
  private final String dbPath = "jdbc:sqlite:sqlDatabase"+ File.separator + "SQLDatabase.db";

  // Connection to the database
  private Connection connection = null;

  /**
   * Connects to a db file for management
   * The path to the file is specified by dbpath
   *
   */
  public void setupDb() {

    // Check if the database is already connected
    if (connection != null) {
      System.out.println("Database already exists.");
      return;
    }

    // Construct a file path for the database
    File dir = new File("sqlDatabase");
    if (!dir.exists()) {
      dir.mkdirs();
    }

    try {
      // Connect to database
      this.connection = DriverManager.getConnection(dbPath);

    } catch (SQLException e) {
      // TODO More robust logging here
      e.printStackTrace();
    }

  }

  /**
   * Connects to the database, does not create db file!
   *
   * @throws FileNotFoundException if the db file doesn't exist
   */
  public void connectDb() throws FileNotFoundException {

    // Check if the database is already connected
    if (connection != null) {
      System.out.println("Database already exists.");
      return;
    }

    // Ensure database has already been made
    if (!Files.exists(Paths.get(dbPath))) {
      throw new FileNotFoundException(dbPath + " does not exist.");
    }

    try {
      // Connect to database
      connection = DriverManager.getConnection(dbPath);

    } catch (SQLException e) {
      // TODO More robust logging
      e.printStackTrace();

    }
  }

  /**
   * To disconnect/close database
   *
   */
  public void disconnectDb() {
    if (connection != null) {
      try {
        this.connection.close();

        // Reset connection
        connection = null;

      } catch (SQLException e) {
        //TODO More robust logging here
        e.printStackTrace();
      }
    }
  }

  /**
   * Adds a table to the database
   *
   * @param tableName name of table to add
   * @param source    table to add
   * @throws DuplicateTableException thrown if the table already exists
   */
  @Override
  public void addTable(String tableName, DataTable source) throws DuplicateTableException {
    // Ensure the database is connected
    if (connection == null) {
      System.err.println("No database connection.");
      return;
    }

    // Check if table exists
    if (this.tableExists(tableName)) {
      throw new DuplicateTableException(tableName);
    }

    // Construct prompt based on table
    String statementString = constructTableStatementString(tableName, source);

    try (Statement stmt = connection.createStatement()) {
      stmt.execute(statementString);
      System.out.println("Table " + tableName + " has been added.");

    } catch (SQLException e) {
      e.printStackTrace();
      System.err.println("Table " + tableName + " could not be added.");

    }

  }

  /**
   * Takes a table and constructs a SQL statement that can be used<br>
   * to a table based of tableName parameters
   *
   * @param tableName name of the SQL table
   * @param source DataTable that will be read from to construct statement
   * @return the SQL statement itself
   */
  public String constructTableStatementString(String tableName, DataTable source) {

    // Construct start of prompt
    StringBuilder statement = new StringBuilder();
    statement.append("CREATE TABLE IF NOT EXISTS "); // Create table
    statement.append(tableName.replaceAll("\\s+", "")); // Using table name with no whitespace
    statement.append(" ("); // Opening bracket for col names

    int numOfCols = source.columnSize();

    // Loop through cols and get names
    for (int i = 0; i < numOfCols; i++) {

      // Name of the col
      statement.append(source.getColumnName(i));

      // Check type of col
      Value checkValue = source.get(i, 1);
      if (checkValue.getTypeIndex() == 0) {
        statement.append(" TEXT");
      } else {
        statement.append(" INTEGER");
      }

      // This assumes the primary key is in the first row
      if (i == 0) {
        statement.append(" PRIMARY KEY");
      }

      // Comma for last statement
      if (i < numOfCols - 1) {
        statement.append(",");
      }
    }

    // Finish prompt and return
    statement.append(")");
    return statement.toString();
  }

  /**
   * Removes a table from the database
   *
   * @param tableName name of table to remove
   * @throws TableNotFoundException thrown if the table does not exist
   */
  @Override
  public void removeTable(String tableName) throws TableNotFoundException {

  }

  /**
   * Checks if a table exists
   *
   * @param tableName name of table
   * @return if the table exists
   */
  @Override
  public boolean tableExists(String tableName) {
    try {

      // Get database meta data
      DatabaseMetaData metaData = connection.getMetaData();

      // Strip tableName whitespace
      tableName = tableName.replaceAll("\\s+", "");

      // Query for table existence
      ResultSet tables = metaData.getTables(null, null, tableName, null);

      if (tables.next()) {
        return true;
      }

      tables.close();

    } catch (SQLException e) {
      e.printStackTrace();
    }

    return false;
  }

  /**
   * maybe gets a reference to a table
   *
   * @param tableName name of table
   * @return a reference to the table if it exists, else null
   */
  @Override
  public DataTable tryGetTable(String tableName) {
    return null;
  }

  // Getters and Setters:
  public Connection getConnection() {
    return connection;
  }
}
