package seng202.team6.dao;

import java.sql.Connection;

/**
 * Data Access Object (DAO) for handling user related database operations.
 */
public class UserDAO extends DAO {

  /**
   * Constructs a new UserDAO with the given database connection.
   *
   * @param connection The database connection to be used for user operations.
   */
  public UserDAO(Connection connection) {
    super(connection);
  }

  @Override
  void init() {

  }
}
