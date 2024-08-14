package seng202.team0.database;

import seng202.team0.exceptions.DuplicateRecordException;
import seng202.team0.exceptions.DuplicateTableException;
import seng202.team0.exceptions.RecordNotFoundException;
import seng202.team0.exceptions.TableNotFoundException;

/**
 * Database abstraction. Instantiate an inheritor to use
 * @author Angus McDougall
 */
abstract public class Database {


  /**
   * Adds a table to the database
   * @param tableName name of table to add
   * @param source table to add
   * @throws DuplicateTableException thrown if the table already exists
   */
  public abstract void addTable(String tableName, DataTable source) throws DuplicateTableException;

  /**
   * Removes a table from the database
   * @param tableName name of table to remove
   * @throws TableNotFoundException thrown if the table does not exist
   */
  public abstract void removeTable(String tableName) throws TableNotFoundException;

  /**
   * Checks if a table exists
   * @param tableName name of table
   * @return if the table exists
   */
  public abstract boolean tableExists(String tableName);

  /**
   * maybe gets a reference to a table
   * @param tableName name of table
   * @return a reference to the table if it exists, else null
   */
  public abstract DataTable tryGetTable(String tableName);

}
