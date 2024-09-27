package seng202.team6.dao;

import java.sql.Connection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Abstract base class for Data Access Objects (DAO).
 * This class provides a common structure for all DAOs in the application.
 */
public abstract class DAO {
  /**
   *  The database connected used by this DAO
   */
  protected final Connection connection;

  protected final Logger log;

  /**
   * Constructs a new DAO with the given database connection
   * @param connection The database connected to be used by this DAO
   */
  public DAO(Connection connection, Class<?> implementationClass) {
    this.connection = connection;
    this.log = LogManager.getLogger(implementationClass);
  }

  /**
   * Initialises the DAO.
   * This method should be implemented to preform setup operations such as creating tables if
   * they do not exist, or adding default values to a table.
   */
  abstract void init();
}
