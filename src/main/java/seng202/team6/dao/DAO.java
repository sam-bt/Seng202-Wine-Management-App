package seng202.team6.dao;

import java.sql.Connection;

/**
 * Abstract base class for Data Access Objects (DAO).
 * This class provides a common structure for all DAOs in the application.
 */
public abstract class DAO {
  /**
   *  The database connected used by this DAO
   */
  protected final Connection connection;

  /**
   * Constructs a new DAO with the given database connection
   * @param connection The database connected to be used by this DAO
   */
  public DAO(Connection connection) {
    this.connection = connection;
  }

  /**
   * Initialises the DAO.
   * This method should be implemented to preform setup operations such as creating tables if
   * they do not exist, or adding default values to a table.
   */
  abstract void init();
}
