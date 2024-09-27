package seng202.team6.dao;

import java.sql.Connection;

/**
 * Data Access Object (DAO) for handling wine notes related database operations.
 */
public class WineNotesDAO extends DAO {

  /**
   * Constructs a new WineNotesDAO with the given database connection.
   *
   * @param connection The database connection to be used for wine note operations.
   */
  public WineNotesDAO(Connection connection) {
    super(connection);
  }

  @Override
  void init() {

  }
}
