package seng202.team6.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

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
    super(connection, UserDAO.class);
  }

  @Override
  public String[] getInitialiseStatements() {
    return new String[] {
        "CREATE TABLE IF NOT EXISTS USER (" +
            "USERNAME       VARCHAR(64)   PRIMARY KEY," +
            "PASSWORD       VARCHAR(64)   NOT NULL," +
            "ROLE           VARCHAR(8)    NOT NULL," +
            "SALT           VARCHAR(32)" +
            ")"
    };
  }
}
