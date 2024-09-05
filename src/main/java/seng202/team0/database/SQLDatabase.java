package seng202.team0.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import seng202.team0.database.exceptions.DuplicateTableException;
import seng202.team0.database.exceptions.TableNotFoundException;

/**
 * SQL Version of Database Uses JDBC
 *
 * @author mpe133
 */
public class SQLDatabase extends Database {

  // This is the path to the db file
  private final String dbPath = "jdbc:sqlite:sqlDatabase" + File.separator + "SQLDatabase.db";

  // Connection to the database
  private Connection connection = null;

  /**
   * Connects to a db file for management The path to the file is specified by dbpath
   */
  public SQLDatabase() {

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
   * To disconnect/close database
   */
  public void disconnectDb() {
    try {
      this.connection.close();

      // Reset connection
      connection = null;

    } catch (SQLException e) {
      //TODO More robust logging here
      e.printStackTrace();
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
      throw new IllegalStateException("Database not connected");
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
   * Takes a table and constructs a SQL statement that can be used<br> to a table based of tableName
   * parameters
   *
   * @param tableName name of the SQL table
   * @param source    DataTable that will be read from to construct statement
   * @return the SQL statement itself
   */
  public String constructTableStatementString(String tableName, DataTable source) {
    //TODO Rewrite primary key autogen

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
   * Uses the INSERT command in sql to add a Datatable into a sql table
   *
   * @param targetTable the name of the sql table
   * @param source      the source table
   */
  public void insertIntoTable(String targetTable, DataTable source) {
    // Ensure connected
    if (connection == null) {
      throw new IllegalStateException("Database not connected");
    }

    //Create statement
    String preparedRowString = buildPreparedRowInsertStatement(source, targetTable);

    try {
      PreparedStatement preparedStatement = connection.prepareStatement(preparedRowString);

      // Iterate through table, get values and construct statements
      for (int i = 0; i < source.rowSize(); i++) {
        for (int j = 0; j < source.columnSize(); j++) {
          Value checkValue = source.get(i, j);
          switch (checkValue.getTypeIndex()) {
            case 0:
              preparedStatement.setString(j, checkValue.getAsString());
              break;

            case 1:
              preparedStatement.setDouble(j, checkValue.getAsReal());
              break;

            default:
              throw new IllegalArgumentException("Invalid type index"); // Wrong data type
          }
        }
        preparedStatement.addBatch(); // Add to batch of statements to be executed
      }

      int[] rowsAffected = preparedStatement.executeBatch();
      System.out.println("Rows affected: " + rowsAffected.length);


    } catch (SQLException e) {
      e.printStackTrace();
    }

  }

  /**
   * Generates the insert statment for a given table
   *
   * @param dataTable
   * @return
   */
  public String buildPreparedRowInsertStatement(DataTable dataTable, String targetTable) {
    StringBuilder statement = new StringBuilder();
    statement.append("INSERT INTO ");
    statement.append(targetTable.replaceAll("\\s+", ""));
    statement.append(" (");

    // Get columns for target fields
    int numOfCols = dataTable.columnSize();
    for (int i = 0; i < numOfCols; i++) {
      statement.append(dataTable.getColumnName(i));
      if (i < numOfCols - 1) {
        statement.append(",");
      }
    }

    statement.append(") VALUES (");

    // Get values
    for (int i = 0; i < numOfCols; i++) {
      statement.append("?"); // Used later on to set values
      if (i < numOfCols - 1) {
        statement.append(",");
      }
    }
    statement.append(")");

    // Return string
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

    // Ensure database is connected
    if (connection == null) {
      throw new IllegalStateException("Database not connected");
    }

    // Remove whitespace
    tableName = tableName.replaceAll("\\s+", "");

    // Make sure table exists
    if (!tableExists(tableName)) {
      throw new TableNotFoundException(tableName + " does not exist.");
    }

    // Create string statement
    String removeTableStatement = "DROP TABLE IF EXISTS " + tableName;

    // Execute statement
    try (Statement statement = connection.createStatement()) {
      statement.execute(removeTableStatement);
      System.out.println(tableName + "removed");

    } catch (SQLException e) {
      // TODO More robust logging
      e.printStackTrace();
      System.err.println(e.getMessage());
    }
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
        tables.close();
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
