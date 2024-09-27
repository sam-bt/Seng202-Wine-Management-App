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
    super(connection, WineReviewDAO.class);
  }

  @Override
  public String[] getInitialiseStatements() {
    return new String[] {
        "CREATE TABLE IF NOT EXISTS NOTES (" +
            "ID             INTEGER       PRIMARY KEY," +
            "USERNAME       VARCHAR(64)   NOT NULL," +
            "WINE_ID        INTEGER       NOT NULL, " +
            "NOTE           TEXT" +
            ")",
        "CREATE TABLE IF NOT EXISTS WINE_REVIEW (" +
            "ID             INTEGER       PRIMARY KEY," +
            "USERNAME       varchar(64)   NOT NULL," +
            "WINE_ID        INTEGER       NOT NULL," +
            "RATING         DOUBLE        NOT NULL," +
            "DESCRIPTION    VARCHAR(256)  NOT NULL," +
            "DATE           DATE          NOT NULL," +
            "FOREIGN KEY (USERNAME) REFERENCES USER(USERNAME) ON DELETE CASCADE," +
            "FOREIGN KEY (WINE_ID) REFERENCES WINE(ID) ON DELETE CASCADE" +
            ")"
    };
  }
}
