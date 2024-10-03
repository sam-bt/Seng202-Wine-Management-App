package seng202.team6.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seng202.team6.managers.DatabaseManager;
import seng202.team6.model.WineFilters;
import seng202.team6.model.GeoLocation;
import seng202.team6.model.Wine;
import seng202.team6.util.DatabaseObjectUniquer;
import seng202.team6.util.Timer;

/**
 * Data Access Object (DAO) for handling wine related database operations.
 */
public class WineDAO extends DAO {

  /**
   * Cache to store and reuse Wine objects to avoid duplication
   */
  private final DatabaseObjectUniquer<Wine> wineCache = new DatabaseObjectUniquer<>();

  /**
   * Constructs a new WineDAO with the given database connection.
   *
   * @param connection The database connection to be used for wine operations.
   */
  public WineDAO(Connection connection) {
    super(connection, WineDAO.class);
  }

  /**
   * Returns the SQL statements required to initialise the WINE table.
   *
   * @return Array of SQL statements for initialising the WINE table
   */
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

  /**
   * Retrieves the total number of wines in the database.
   *
   * @return The count of wines in the WINE table
   */
  public int getCount() {
    Timer timer = new Timer();
    String sql = "SELECT COUNT(*) FROM WINE";
    try (Statement statement = connection.createStatement()) {
      try (ResultSet resultSet = statement.executeQuery(sql)) {
        if (resultSet.next()) {
          int count = resultSet.getInt(1);
          log.info("Counted {} wines in {}ms", count, timer.stop());
          return count;
        }
      }
    } catch (SQLException error) {
      log.error("Failed to count the number of wines", error);
    }
    return 0;
  }

  /**
   * Retrieves all wines from the WINE table.
   *
   * @return An ObservableList of all Wine objects in the database
   */
  public ObservableList<Wine> getAll() {
    Timer timer = new Timer();
    String sql = "SELECT * from WINE "
        + "LEFT JOIN GEOLOCATION ON LOWER(WINE.REGION) LIKE LOWER(GEOLOCATION.NAME)"
        + "ORDER BY WINE.ID ";
    try (Statement statement = connection.createStatement()) {
      try (ResultSet resultSet = statement.executeQuery(sql)) {
        ObservableList<Wine> wines = extractAllWinesFromResultSet(resultSet);
        log.info("Successfully retrieved all {} wines in {}ms", wines.size(), timer.stop());
        return wines;
      }
    } catch (SQLException error) {
      log.info("Failed to retrieve wines in range", error);
    }
    return FXCollections.emptyObservableList();
  }

  /**
   * Retrieves a range of wines from the WINE table.
   *
   * @param begin The start index of the range (inclusive)
   * @param end   The end index of the range (exclusive)
   * @return An ObservableList of Wine objects within the specified range
   */
  public ObservableList<Wine> getAllInRange(int begin, int end) {
    return getAllInRange(begin, end, null);
  }

  /**
   * Retrieves a range of wines from the WINE table.
   *
   * @param begin The start index of the range (inclusive)
   * @param end   The end index of the range (exclusive)
   * @param wineFilters The wine filters to be applied
   * @return An ObservableList of Wine objects within the specified range
   */
  public ObservableList<Wine> getAllInRange(int begin, int end, WineFilters wineFilters) {
    Timer timer = new Timer();
    String sql = "SELECT * from WINE "
        + "LEFT JOIN GEOLOCATION ON LOWER(WINE.REGION) LIKE LOWER(GEOLOCATION.NAME)"
        + (wineFilters == null ? "" :
        "where TITLE like ? "
            + "and COUNTRY like ? "
            + "and WINERY like ? "
            + "and COLOR like ? "
            + "and VINTAGE between ? and ?"
            + "and SCORE_PERCENT between ? and ? "
            + "and ABV between ? and ? "
            + "and PRICE between ? and ? ")
        + "ORDER BY WINE.ID "
        + "LIMIT ? "
        + "OFFSET ?";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      int paramIndex = 1;
      if (wineFilters != null) {
        statement.setString(paramIndex++,
            wineFilters.getTitle().isEmpty() ? "%" : "%" + wineFilters.getTitle() + "%");
        statement.setString(paramIndex++,
            wineFilters.getCountry().isEmpty() ? "%" : "%" + wineFilters.getCountry() + "%");
        statement.setString(paramIndex++,
            wineFilters.getWinery().isEmpty() ? "%" : "%" + wineFilters.getWinery() + "%");
        statement.setString(paramIndex++,
            wineFilters.getColor().isEmpty() ? "%" : "%" + wineFilters.getColor() + "%");
        statement.setInt(paramIndex++, wineFilters.getMinVintage());
        statement.setInt(paramIndex++, wineFilters.getMaxVintage());
        statement.setDouble(paramIndex++, wineFilters.getMinScore());
        statement.setDouble(paramIndex++, wineFilters.getMaxScore());
        statement.setDouble(paramIndex++, wineFilters.getMinAbv());
        statement.setDouble(paramIndex++, wineFilters.getMaxAbv());
        statement.setDouble(paramIndex++, wineFilters.getMinPrice());
        statement.setDouble(paramIndex++, wineFilters.getMaxPrice());

      }
      statement.setInt(paramIndex++, end - begin);
      statement.setInt(paramIndex, begin);

      try (ResultSet resultSet = statement.executeQuery()) {
        ObservableList<Wine> wines = extractAllWinesFromResultSet(resultSet);
        log.info("Successfully retrieved {} wines in range {}-{} in {}ms", wines.size(),
            begin, end, timer.stop());
        return wines;
      }
    } catch (SQLException error) {
      log.info("Failed to retrieve wines in range {}-{}", begin, end, error);
    }
    return FXCollections.emptyObservableList();
  }

  /**
   * Replaces all wines in the WINE table by first removing all existing wines and then adding the
   * provided lists of wines.
   *
   * @param wines The list of wines to be added to the table
   */
  public void replaceAll(List<Wine> wines) {
    removeAll();
    addAll(wines);
  }

  public void add(Wine wine) {
    Timer timer = new Timer();
    String sql = "INSERT INTO WINE VALUES (null, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    try (PreparedStatement statement = connection.prepareStatement(sql,
        Statement.RETURN_GENERATED_KEYS)) {
      setWineParameters(statement, wine, 1);
      statement.executeUpdate();

      try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          wine.setKey(generatedKeys.getLong(1));
          log.info("Successfully added wine with ID {} in {}ms", wine.getKey(), timer.stop());
          if (useCache()) {
            wineCache.addObject(wine.getKey(), wine);
          }
          bindUpdater(wine);
        } else {
          log.info("Could not add wine with ID {} in {}ms", wine.getKey(), timer.stop());
        }
      }
    } catch (SQLException error) {
      log.error("Failed to add a batch of wines", error);
    }
  }

  /**
   * Adds a list of wines to the WINE table in batch mode to improve performance. The batch is
   * executed every 2048 wines to prevent excessive memory usage.
   * <p>
   * SQL Lite does not support batch generated key returning so all the wines in the specified list
   * are considered invalid. After calling this method, you must then use getAll or getAllInRange in
   * order to fetch new wine objects with valid ID's.
   * </p>
   *
   * @param wines The list of wines to be added to the table
   */
  public void addAll(List<Wine> wines) {
    Timer timer = new Timer();
    String sql = "INSERT INTO WINE VALUES (null, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    try {
      connection.setAutoCommit(false);
    } catch (SQLException error) {
      log.error("Failed to disable database auto committing", error);
    }

    int rowsAffected = 0;
    int numberOfBatches = 1;
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      for (int i = 0; i < wines.size(); i++) {
        setWineParameters(statement, wines.get(i), 1);
        statement.addBatch();

        if (i > 0 && i % 2048 == 0) {
          for (int rowsAffectedInBatch : statement.executeBatch()) {
            rowsAffected += rowsAffectedInBatch;
          }
          numberOfBatches++;
        }
      }
      for (int rowsAffectedInBatch : statement.executeBatch()) {
        rowsAffected += rowsAffectedInBatch;
      }
      connection.commit();
      connection.setAutoCommit(true);
    } catch (SQLException error) {
      log.error("Failed to add a batch of wines", error);
      return;
    }
    log.info("Successfully added {} out of {} wines using {} batches in {}ms", rowsAffected,
        wines.size(), numberOfBatches, timer.stop());
  }

  /**
   * Removes all wines from the WINE table.
   */
  public void removeAll() {
    Timer timer = new Timer();
    String sql = "DELETE FROM WINE";
    try (Statement statement = connection.createStatement()) {
      int rowsAffected = statement.executeUpdate(sql);
      log.info("Successfully removed {} wines in {}ms", rowsAffected, timer.stop());
    } catch (SQLException error) {
      log.error("Failed to remove all wines", error);
    }
  }

  /**
   * Extracts all wines from the provided ResultSet and stores them in an ObservableList.
   *
   * @param resultSet The ResultSet from which wines are to be extracted
   * @return An ObservableList of Wine objects
   * @throws SQLException If an error occurs while processing the ResultSet
   */
  ObservableList<Wine> extractAllWinesFromResultSet(ResultSet resultSet)
      throws SQLException {
    ObservableList<Wine> wines = FXCollections.observableArrayList();
    while (resultSet.next()) {
      wines.add(extractWineFromResultSet(resultSet));
    }
    return wines;
  }

  /**
   * Extracts a Wine object from the provided ResultSet. The wine cache is checked before creating a
   * new Wine instance
   *
   * @param resultSet The ResultSet from which wines are to be extracted
   * @return The extracted Wine object
   * @throws SQLException If an error occurs while processing the ResultSet
   */
  Wine extractWineFromResultSet(ResultSet resultSet) throws SQLException {
    long id = resultSet.getLong("ID");
    if (useCache()) {
      Wine cachedWine = wineCache.tryGetObject(id);
      if (cachedWine != null) {
        return cachedWine;
      }
    }

    GeoLocation geoLocation = createGeoLocation(resultSet);
    Wine wine = new Wine(
        id,
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
    if (useCache()) {
      wineCache.addObject(id, wine);
    }
    bindUpdater(wine);
    return wine;
  }

  /**
   * Extracts the latitude and longitude from the provided ResultSet and creates a new GeoLocation
   * object
   *
   * @param set The ResultSet from which geolocations are to be extracted
   * @return The extract Geolocation if available, otherwise null if either the latitude or
   * longitude were null
   * @throws SQLException If an error occurs while processing the ResultSet
   */
  private GeoLocation createGeoLocation(ResultSet set) throws SQLException {
    double latitude = set.getDouble("LATITUDE");
    if (set.wasNull()) {
      return null;
    }
    double longitude = set.getDouble("LONGITUDE");
    if (set.wasNull()) {
      return null;
    }
    return new GeoLocation(latitude, longitude);
  }


  /**
   * Sets the parameters for the PreparedStatement with the Wine objects data.
   *
   * @param statement  The PreparedStatement to set the parameters for
   * @param wine       The wine whose data will be used to set
   * @param startIndex The starting param index
   * @throws SQLException If an error occurs while setting the PreparedStatement's parameters
   */
  private void setWineParameters(PreparedStatement statement, Wine wine, int startIndex)
      throws SQLException {
    statement.setString(startIndex++, wine.getTitle());
    statement.setString(startIndex++, wine.getVariety());
    statement.setString(startIndex++, wine.getCountry());
    statement.setString(startIndex++, wine.getRegion());
    statement.setString(startIndex++, wine.getWinery());
    statement.setString(startIndex++, wine.getColor());
    statement.setInt(startIndex++, wine.getVintage());
    statement.setString(startIndex++, wine.getDescription());
    statement.setInt(startIndex++, wine.getScorePercent());
    statement.setFloat(startIndex++, wine.getAbv());
    statement.setFloat(startIndex++, wine.getPrice());
  }

  /**
   * Binds listeners to the Wine object to ensure that any changes to the wines properties are
   * automatically reflected in the database.
   *
   * @param wine The Wine object to bind listeners to
   */
  private void bindUpdater(Wine wine) {
    wine.titleProperty().addListener((observableValue, before, after) -> {
      updateAttribute(wine.getKey(), "TITLE", update -> {
        update.setString(1, after);
      });
    });
    wine.varietyProperty().addListener((observableValue, before, after) -> {
      updateAttribute(wine.getKey(), "VARIETY", update -> {
        update.setString(1, after);
      });
    });

    wine.countryProperty().addListener((observableValue, before, after) -> {
      updateAttribute(wine.getKey(), "COUNTRY", update -> {
        update.setString(1, after);
      });
    });
    wine.regionProperty().addListener((observableValue, before, after) -> {
      updateAttribute(wine.getKey(), "REGION", update -> {
        update.setString(1, after);
      });
    });
    wine.wineryProperty().addListener((observableValue, before, after) -> {
      updateAttribute(wine.getKey(), "WINERY", update -> {
        update.setString(1, after);
      });
    });
    wine.colorProperty().addListener((observableValue, before, after) -> {
      updateAttribute(wine.getKey(), "COLOR", update -> {
        update.setString(1, after);
      });
    });
    wine.vintageProperty().addListener((observableValue, before, after) -> {
      updateAttribute(wine.getKey(), "VINTAGE", update -> {
        update.setInt(1, (Integer) after);
      });
    });
    wine.descriptionProperty().addListener((observableValue, before, after) -> {
      updateAttribute(wine.getKey(), "DESCRIPTION", update -> {
        update.setString(1, after);
      });
    });
    wine.scorePercentProperty().addListener((observableValue, before, after) -> {
      updateAttribute(wine.getKey(), "SCORE_PERCENT", update -> {
        update.setInt(1, (Integer) after);
      });
    });
    wine.abvProperty().addListener((observableValue, before, after) -> {
      updateAttribute(wine.getKey(), "ABV", update -> {
        update.setFloat(1, (Float) after);
      });
    });
    wine.priceProperty().addListener((observableValue, before, after) -> {
      updateAttribute(wine.getKey(), "PRICE", update -> {
        update.setFloat(1, (Float) after);
      });
    });
  }

  /**
   * Updates a specific attribute of the user in the WINE table
   *
   * @param attributeName   name of attribute
   * @param attributeSetter callback to set attribute
   */
  private void updateAttribute(long id, String attributeName,
      DatabaseManager.AttributeSetter attributeSetter) {
    if (id == -1) {
      log.warn("Skipping attribute update '{}' for wine with ID -1",
          attributeName);
      return;
    }
    Timer timer = new Timer();
    String sql = "UPDATE WINE set " + attributeName + " = ? where ID = ?";
    try (PreparedStatement update = connection.prepareStatement(sql)) {
      attributeSetter.setAttribute(update);
      update.setLong(2, id);

      int rowsAffected = update.executeUpdate();
      if (rowsAffected == 1) {
        log.info("Successfully updated attribute '{}' for wine with ID {} in {}ms",
            attributeName, id, timer.stop());
      } else {
        log.info("Could not update attribute '{}' for wine with ID {} in {}ms",
            attributeName, id, timer.stop());
      }
    } catch (SQLException error) {
      log.error("Failed to update attribute '{}' for wine with ID {} in {}ms",
          attributeName, id, timer.stop(), error);
    }
  }
}
