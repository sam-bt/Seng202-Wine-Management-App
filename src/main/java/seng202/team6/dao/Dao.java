package seng202.team6.dao;

import java.sql.Connection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Abstract base class for Data Access Objects (DAO). This class provides a common structure for all
 * DAOs in the application.
 */
public abstract class Dao {

  /**
   * The database connected used by this DAO.
   */
  protected final Connection connection;

  /**
   * Logger instance for logging information and errors related to the DAO.
   */
  protected final Logger log;

  /**
   * Flag to indication whether the DAO should use caching. The implementing class is responsible
   * for utilizing this flag.
   */
  private boolean useCache = true;


  /**
   * Constructs a new DAO with the given database connection and initializes logging.
   *
   * @param connection          The database connection to be used by this DAO.
   * @param implementationClass The class that implements this DAO, used to configure the logger.
   */
  public Dao(Connection connection, Class<?> implementationClass) {
    this.connection = connection;
    this.log = LogManager.getLogger(implementationClass);
  }

  /**
   * Returns an array of SQL statements required to initialize the tables handled by this DAO.
   * Subclasses should override this method to provide their specific SQL initialization
   * statements.
   *
   * @return an array of SQL statements for table initialization, or null if no statements are
   *        needed.
   */
  public String[] getInitialiseStatements() {
    return null;
  }

  /**
   * Returns whether this DAO should use caching.
   *
   * @return true if caching is enabled, otherwise false
   */
  public boolean useCache() {
    return true;
  }

  /**
   * Configures whether this DAO should use caching.
   *
   * @param useCache {@code true} to enable caching, {@code false} to disable it.
   */
  public void setUseCache(boolean useCache) {
    this.useCache = useCache;
  }
}
