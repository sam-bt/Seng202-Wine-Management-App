package seng202.team6.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import seng202.team6.model.GeoLocation;
import seng202.team6.model.Wine;

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

  private Wine extractWineFromResultSet(ResultSet resultSet, boolean requireGeolocation) throws SQLException {
    GeoLocation geoLocation = null; // todo - change this to grab the geolocation when required
    return new Wine(
        resultSet.getLong("ID"),
        null, // null DatabaseManager for now as this will be replaced
        resultSet.getString("TITLE"),
        resultSet.getString("VARIETY"),
        resultSet.getString("COUNTRY"),
        resultSet.getString("REGION"),
        resultSet.getString("WINERY"),
        resultSet.getString("COLOR"),
        resultSet.getInt("VINTAGE"),
        resultSet.getString("DESCRIPTION"),
        resultSet.getInt("SCORE_PERCENT"),
        resultSet.getFloat("ABV"),
        resultSet.getFloat("PRICE"),
        geoLocation
    );
  }

  private void setWineParameters(PreparedStatement statement, Wine wine, int paramIndex) throws SQLException {
    statement.setString(paramIndex++, wine.getTitle());
    statement.setString(paramIndex++, wine.getVariety());
    statement.setString(paramIndex++, wine.getCountry());
    statement.setString(paramIndex++, wine.getRegion());
    statement.setString(paramIndex++, wine.getWinery());
    statement.setString(paramIndex++, wine.getColor());
    statement.setInt(paramIndex++, wine.getVintage());
    statement.setString(paramIndex++, wine.getDescription());
    statement.setInt(paramIndex++, wine.getScorePercent());
    statement.setFloat(paramIndex++, wine.getAbv());
    statement.setFloat(paramIndex++, wine.getPrice());
  }
}
