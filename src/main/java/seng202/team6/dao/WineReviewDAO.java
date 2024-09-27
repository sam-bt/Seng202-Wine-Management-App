package seng202.team6.dao;

import java.sql.Connection;

/**
 * Data Access Object (DAO) for handling wine review related database operations.
 */
public class WineReviewDAO extends DAO {

  /**
   * Constructs a new WineReviewDAO with the given database connection.
   *
   * @param connection The database connection to be used for wine review operations.
   */
  public WineReviewDAO(Connection connection) {
    super(connection);
  }

  @Override
  void init() {

  }
}
