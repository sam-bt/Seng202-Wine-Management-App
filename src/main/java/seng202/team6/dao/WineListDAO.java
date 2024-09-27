package seng202.team6.dao;

import java.sql.Connection;

/**
 * Data Access Object (DAO) for handling wine list related database operations.
 */
public class WineListDAO extends DAO {

  /**
   * Constructs a new WineListDAO with the given database connection.
   *
   * @param connection The database connection to be used for wine list operations.
   */
  public WineListDAO(Connection connection) {
    super(connection);
  }

  @Override
  void init() {

  }
}
