package seng202.team0.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
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
  final String dbPath = "jdbc:sqlite:SQLDatabase.db";
  Connection connection;

  public void connectDB() {
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
}
