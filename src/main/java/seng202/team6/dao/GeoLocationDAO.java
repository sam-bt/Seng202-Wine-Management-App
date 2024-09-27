package seng202.team6.dao;

import java.sql.Connection;

/**
 * Data Access Object (DAO) for handling geolocation related database operations.
 */
public class GeoLocationDAO extends DAO {

  /**
   * Constructs a new GeoLocationDAO with the given database connection.
   *
   * @param connection The database connection to be used for geolocation operations.
   */
  public GeoLocationDAO(Connection connection) {
    super(connection);
  }

  @Override
  void init() {

  }
}
