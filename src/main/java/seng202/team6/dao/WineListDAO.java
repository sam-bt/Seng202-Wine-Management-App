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
    super(connection, WineListDAO.class);
  }

  @Override
  public String[] getInitialiseStatements() {
    return new String[] {
        "CREATE TABLE IF NOT EXISTS LIST_NAME (" +
            "ID             INTEGER       PRIMARY KEY," +
            "USERNAME       VARCHAR(32)   NOT NULL," +
            "NAME           VARCHAR(10)   NOT NULL," +
            "FOREIGN KEY (USERNAME) REFERENCES USER(USERNAME) ON DELETE CASCADE" +
            ")",

        "CREATE TABLE IF NOT EXISTS LIST_ITEMS (" +
            "ID             INTEGER       PRIMARY KEY," +
            "LIST_ID        INTEGER       NOT NULL," +
            "WINE_ID        INTEGER       NOT NULL," +
            "FOREIGN KEY (LIST_ID) REFERENCES LIST_NAME(ID) ON DELETE CASCADE," +
            "FOREIGN KEY (WINE_ID) REFERENCES WINE(ID) ON DELETE CASCADE" +
            ")"
    };
  }
}
