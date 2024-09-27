package seng202.team6.dao;

import java.sql.Connection;

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
  public String[] getInitialiseStatements() {
    return new String[]{
        "CREATE TABLE IF NOT EXISTS WINE (" +
            "ID             INTEGER       PRIMARY KEY," +
            "TITLE          VARCHAR(64)   NOT NULL," +
            "VARIETY        VARCHAR(32)," +
            "COUNTRY        VARCHAR(32)," +
            "REGION         VARCHAR(32)," +
            "WINERY         VARCHAR(64)," +
            "COLOR          VARCHAR(32)," +
            "VINTAGE        INTEGER," +
            "DESCRIPTION    INTEGER," +
            "SCORE_PERCENT  INTEGER," +
            "ABV            FLOAT," +
            "PRICE          FLOAT" +
            ")"
    };
  }
}
