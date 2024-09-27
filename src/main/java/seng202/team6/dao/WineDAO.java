package seng202.team6.dao;

import java.sql.Connection;
import java.sql.Statement;

/**
 * Data Access Object (DAO) for handling wine related database operations.
 */
public class WineDAO extends DAO {

  /**
   * Constructs a new WineDAO with the given database connection.
   *
   * @param connection The database connection to be used for wine operations.
   */
  public WineDAO(Connection connection) {
    super(connection, WineDAO.class);
  }

  @Override
  void init() {

  }
}
