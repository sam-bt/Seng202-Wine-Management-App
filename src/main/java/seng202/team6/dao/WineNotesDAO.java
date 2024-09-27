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
    super(connection, WineNotesDAO.class);
  }

  @Override
  public String[] getInitialiseStatements() {
    return new String[] {
        "CREATE TABLE IF NOT EXISTS NOTES (" +
            "ID             INTEGER       PRIMARY KEY," +
            "USERNAME       VARCHAR(64)   NOT NULL," +
            "WINE_ID        INTEGER       NOT NULL, " +
            "NOTE           TEXT," +
            "FOREIGN KEY (USERNAME) REFERENCES USER(USERNAME) ON DELETE CASCADE," +
            "FOREIGN KEY (WINE_ID) REFERENCES WINE(ID) ON DELETE CASCADE" +
            ")"
    };
  }
}
