package seng202.team6.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seng202.team6.managers.DatabaseManager;
import seng202.team6.model.GeoLocation;
import seng202.team6.model.Vineyard;
import seng202.team6.model.VineyardFilters;
import seng202.team6.model.VineyardTour;
import seng202.team6.model.Wine;
import seng202.team6.model.WineList;
import seng202.team6.service.VineyardDataStatService;
import seng202.team6.util.DatabaseObjectUniquer;
import seng202.team6.util.Timer;

/**
 * Data Access Object (DAO) for handling vineyard related database operations.
 */
public class VineyardDao extends Dao {

  /**
   * Cache to store and reuse Vineyard objects to avoid duplication.
   */
  private final DatabaseObjectUniquer<Vineyard> vineyardCache = new DatabaseObjectUniquer<>();

  private final VineyardDataStatService vineyardDataStatService;

  /**
   * Constructs a new VineyardDAO with the given database connection.
   *
   * @param connection The database connection to be used for vineyard operations
   */
  public VineyardDao(Connection connection, VineyardDataStatService vineyardDataStatService) {
    super(connection, VineyardDao.class);
    this.vineyardDataStatService = vineyardDataStatService;
  }

  /**
   * Returns the SQL statements required to initialise the VINEYARD table.
   *
   * @return Array of SQL statements for initialising the VINEYARD table
   */
  @Override
  public String[] getInitialiseStatements() {
    return new String[]{
        "CREATE TABLE IF NOT EXISTS VINEYARD ("
            + "ID             INTEGER       PRIMARY KEY,"
            + "NAME           VARCHAR(64)   NOT NULL,"
            + "ADDRESS        VARCHAR(64)   NOT NULL,"
            + "REGION         VARCHAR(32)   NOT NULL,"
            + "WEBSITE        TEXT,"
            + "DESCRIPTION    TEXT,"
            + "LOGO_URL       TEXT"
            + ")"
    };
  }

  /**
   * Retrieves the total number of vineyards in the database.
   *
   * @return The count of vineyards in the VINEYARD table
   */
  public int getCount() {
    Timer timer = new Timer();
    String sql = "SELECT COUNT(*) FROM VINEYARD";
    try (Statement statement = connection.createStatement()) {
      try (ResultSet resultSet = statement.executeQuery(sql)) {
        if (resultSet.next()) {
          int count = resultSet.getInt(1);
          log.info("Counted {} vineyards in {}ms", count, timer.currentOffsetMilliseconds());
          return count;
        }
      }
    } catch (SQLException error) {
      log.error("Failed to count the number of vineyards", error);
    }
    return 0;
  }

  /**
   * Retrieves a range of vineyards from the VINEYARD table.
   *
   * @param begin           The start index of the range (inclusive)
   * @param end             The end index of the range (exclusive)
   * @param vineyardFilters The vineyard filters to be applied
   * @return An ObservableList of Vineyard objects within the specified range
   */
  public ObservableList<Vineyard> getAllInRange(int begin, int end,
      VineyardFilters vineyardFilters) {
    Timer timer = new Timer();
    String sql = "SELECT VINEYARD.ID as vineyard_id, VINEYARD.*, GEOLOCATION.LATITUDE, "
        + "GEOLOCATION.LONGITUDE "
        + "FROM VINEYARD "
        + "LEFT JOIN GEOLOCATION ON LOWER(VINEYARD.ADDRESS) LIKE LOWER(GEOLOCATION.NAME)"
        + (vineyardFilters == null ? "" :
        "where VINEYARD.NAME like ? "
            + "and ADDRESS like ? "
            + "and REGION like ? ")
        + "ORDER BY VINEYARD.ID "
        + "LIMIT ? "
        + "OFFSET ?";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      int paramIndex = 1;
      if (vineyardFilters != null) {
        statement.setString(paramIndex++,
            vineyardFilters.getName().isEmpty() ? "%" : "%" + vineyardFilters.getName() + "%");
        statement.setString(paramIndex++,
            vineyardFilters.getAddress().isEmpty() ? "%"
                : "%" + vineyardFilters.getAddress() + "%");
        statement.setString(paramIndex++,
            vineyardFilters.getRegion().isEmpty() ? "%" : "%" + vineyardFilters.getRegion() + "%");
      }
      statement.setInt(paramIndex++, end - begin);
      statement.setInt(paramIndex, begin);

      try (ResultSet resultSet = statement.executeQuery()) {
        ObservableList<Vineyard> vineyards = extractAllVineyardsFromResultSet(resultSet,
            "vineyard_id");
        log.info("Successfully retrieved {} vineyards in range {}-{} in {}ms",
            vineyards.size(), begin, end, timer.currentOffsetMilliseconds());
        return vineyards;
      }
    } catch (SQLException error) {
      log.info("Failed to retrieve vineyards in range {}-{}", begin, end, error);
    }
    return FXCollections.emptyObservableList();
  }

  /**
   * Retrieves a vineyard by its name from the VINEYARD table.
   *
   * @param name The name of the vineyard.
   * @return The corresponding Vineyard object, or null if not found.
   */
  public Vineyard get(String name) {
    Timer timer = new Timer();
    String sql = "SELECT VINEYARD.ID as vineyard_id, VINEYARD.*, GEOLOCATION.LATITUDE, "
        + "GEOLOCATION.LONGITUDE "
        + "FROM VINEYARD "
        + "LEFT JOIN GEOLOCATION ON LOWER(VINEYARD.ADDRESS) LIKE LOWER(GEOLOCATION.NAME) "
        + "WHERE VINEYARD.NAME = ?";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, name);
      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          log.info("Successfully retrieved vineyard with name '{}' in {}ms", name,
              timer.currentOffsetMilliseconds());
          return extractVineyardFromResultSet(resultSet, "vineyard_id");
        }
        log.warn("Could not retrieve vineyard with name '{}' in {}ms", name,
            timer.currentOffsetMilliseconds());
      }
    } catch (SQLException error) {
      log.info("Failed to retrieve vineyard with name {}", name, error);
    }
    return null;
  }

  /**
   * Inserts a list of vineyards into the VINEYARD table.
   *
   * @param vineyards The list of Vineyard objects to be added.
   */
  public void addAll(List<Vineyard> vineyards) {
    Timer timer = new Timer();
    String sql = "INSERT INTO VINEYARD values (null, ?, ?, ?, ?, ?, ?);";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      for (Vineyard vineyard : vineyards) {
        statement.setString(1, vineyard.getName());
        statement.setString(2, vineyard.getAddress());
        statement.setString(3, vineyard.getRegion());
        statement.setString(4, vineyard.getWebsite());
        statement.setString(5, vineyard.getDescription());
        statement.setString(6, vineyard.getLogoUrl());
        statement.addBatch();
      }

      int rowsAffected = Arrays.stream(statement.executeBatch()).sum();
      log.info("Successfully added {} vineyards in {}ms",
          rowsAffected, rowsAffected, timer.currentOffsetMilliseconds());
    } catch (SQLException error) {
      log.error("Failed to add vineyards", error);
    }
  }

  /**
   * Create a vineyard and inserts it into the VINEYARD table.
   *
   * @param name        the name of the vineyard
   * @param address     the address of the vineyard
   * @param region      the region of the vineyard
   * @param website     the website of the vineyard
   * @param description the description of the vineyard
   * @param logoUrl     the logoUrl of the vineyard
   * @param geoLocation the geoLocation of the vineyard
   */
  public Vineyard add(String name, String address, String region, String website,
      String description, String logoUrl, GeoLocation geoLocation) {
    Timer timer = new Timer();
    String sql = "INSERT INTO VINEYARD values (null, ?, ?, ?, ?, ?, ?);";
    try (PreparedStatement statement = connection.prepareStatement(sql,
        Statement.RETURN_GENERATED_KEYS)) {
      statement.setString(1, name);
      statement.setString(2, address);
      statement.setString(3, region);
      statement.setString(4, website);
      statement.setString(5, description);
      statement.setString(6, logoUrl);
      statement.executeUpdate();

      try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          long id = generatedKeys.getLong(1);
          log.info("Successfully created vineyard with ID {} in {}ms",
              id, timer.currentOffsetMilliseconds());
          Vineyard vineyard = new Vineyard(id, name, address, region, website, description, logoUrl,
              geoLocation);
          vineyardCache.addObject(id, vineyard);

          return vineyard;
        }
        log.warn("Could not create vineyard with name '{}' in {}ms",
            name, timer.currentOffsetMilliseconds());
      }
    } catch (SQLException error) {
      log.error("Failed to create vineyard", error);
    }
    return null;
  }

  /**
   * Deletes a vineyard from the database.
   *
   * @param vineyard the vineyard to be deleted
   */
  public void remove(Vineyard vineyard) {
    Timer timer = new Timer();
    String sql = "DELETE FROM VINEYARD WHERE ID = ?;";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setLong(1, vineyard.getId());

      int rowsAffected = statement.executeUpdate();
      if (rowsAffected == 1) {
        log.info("Successfully deleted vineyard with ID {} in {}ms", vineyard.getId(),
            timer.currentOffsetMilliseconds());
      } else {
        log.warn("Could not delete vineyard with ID {} in {}ms", vineyard.getId(),
            timer.currentOffsetMilliseconds());
      }
      vineyardCache.removeObject(vineyard.getId());
    } catch (SQLException e) {
      log.error("Failed to delete vineyard with ID {}", vineyard.getId());
    }
  }

  /**
   * Retrieves all vineyards associated with a given vineyard tour.
   *
   * @param vineyardTour The VineyardTour object.
   * @return A list of Vineyard objects associated with the tour.
   */
  public List<Vineyard> getAllFromTour(VineyardTour vineyardTour) {
    Timer timer = new Timer();
    String sql = "SELECT VINEYARD.ID as vineyard_id, VINEYARD.*, GEOLOCATION.LATITUDE, "
        + "GEOLOCATION.LONGITUDE "
        + "FROM VINEYARD_TOUR_ITEM "
        + "LEFT JOIN VINEYARD ON VINEYARD.ID = VINEYARD_TOUR_ITEM.VINEYARD_ID "
        + "LEFT JOIN GEOLOCATION ON LOWER(VINEYARD.ADDRESS) LIKE LOWER(GEOLOCATION.NAME)"
        + "WHERE TOUR_ID = ?";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setLong(1, vineyardTour.getId());

      try (ResultSet resultSet = statement.executeQuery()) {
        ObservableList<Vineyard> vineyards = extractAllVineyardsFromResultSet(resultSet,
            "vineyard_id");
        log.info("Successfully retrieved all {} vineyards in tour '{}' with id '{}' in "
                + "{}ms",
            vineyards.size(), vineyardTour.getName(), vineyardTour.getId(),
            timer.currentOffsetMilliseconds());
        return vineyards;
      }
    } catch (SQLException error) {
      log.info("Failed to retrieve vineyards in tour '{}' with id '{}'",
          vineyardTour.getName(), vineyardTour.getId(), error);
    }
    return FXCollections.emptyObservableList();
  }

  /**
   * Retrieves all vineyards which wines in a list contain.
   *
   * @param wineList The WineList to search for vineyards from wines.
   * @return A list of Vineyard objects associated with the tour.
   */
  public ObservableList<Vineyard> getAllInList(WineList wineList) {
    Timer timer = new Timer();
    String sql = "SELECT VINEYARD.ID as vineyard_id, VINEYARD.*, "
            + "GEOLOCATION.LATITUDE, GEOLOCATION.LONGITUDE "
            + "FROM WINE "
            + "INNER JOIN LIST_ITEMS ON WINE.ID = LIST_ITEMS.WINE_ID "
            + "INNER JOIN LIST_NAME ON LIST_ITEMS.LIST_ID = LIST_NAME.ID "
            + "INNER JOIN VINEYARD ON VINEYARD.NAME = WINE.WINERY "
            + "LEFT JOIN GEOLOCATION on lower(WINE.REGION) like lower(GEOLOCATION.NAME) "
            + "WHERE LIST_NAME.ID = ?";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setLong(1, wineList.id());

      try (ResultSet resultSet = statement.executeQuery()) {
        ObservableList<Vineyard> vineyards = extractAllVineyardsFromResultSet(
            resultSet, "vineyard_id");
        log.info("Successfully retrieved all {} vineyards in list '{}' in {}ms",
            vineyards.size(), wineList.name(), timer.currentOffsetMilliseconds());
        return vineyards;
      }
    } catch (SQLException error) {
      log.info("Failed to retrieve vineyards in list '{}'", wineList.name(), error);
    }
    return FXCollections.emptyObservableList();
  }

  /**
   * Updates a range of unique values using the vineyards data stat service.
   *
   * <p>
   * When the cache is invalidated by write operations to the database this must be called.
   * </p>
   */
  public void updateUniques() {
    Timer timer = new Timer();
    String query = "SELECT NAME, ADDRESS, REGION FROM VINEYARD";
    try (PreparedStatement statement = connection.prepareStatement(query)) {
      try (ResultSet resultSet = statement.executeQuery()) {
        while (resultSet.next()) {
          String name = resultSet.getString("NAME");
          String address = resultSet.getString("ADDRESS");
          String region = resultSet.getString("REGION");
          vineyardDataStatService.getUniqueNames().add(name);
          vineyardDataStatService.getUniqueAddresses().add(address);
          vineyardDataStatService.getUniqueRegions().add(region);
        }
      }
      log.info("Successfully updated unique values vineyard cache");
    } catch (SQLException e) {
      log.error("Failed to update unique values vineyard cache", e);
    }
  }

  /**
   * Extracts all vineyards from the provided ResultSet and stores them in an ObservableList.
   *
   * @param resultSet The ResultSet from which vineyards are to be extracted
   * @return An ObservableList of Vineyard objects
   * @throws SQLException If an error occurs while processing the ResultSet
   */
  private ObservableList<Vineyard> extractAllVineyardsFromResultSet(ResultSet resultSet,
      String idColumnName)
      throws SQLException {
    ObservableList<Vineyard> vineyards = FXCollections.observableArrayList();
    while (resultSet.next()) {
      vineyards.add(extractVineyardFromResultSet(resultSet, idColumnName));
    }
    return vineyards;
  }

  /**
   * Extracts a Vineyard object from the provided ResultSet. The wine cache is checked before
   * creating a new Vineyard instance
   *
   * @param resultSet The ResultSet from which vineyards are to be extracted
   * @return The extracted Vineyard object
   * @throws SQLException If an error occurs while processing the ResultSet
   */
  private Vineyard extractVineyardFromResultSet(ResultSet resultSet, String idColumnName)
      throws SQLException {
    long id = resultSet.getLong(idColumnName);
    Vineyard cachedVineyard = vineyardCache.tryGetObject(id);
    if (cachedVineyard != null) {
      return cachedVineyard;
    }

    GeoLocation geoLocation = createGeoLocation(resultSet);
    Vineyard vineyard = new Vineyard(
        id,
        resultSet.getString("NAME"),
        resultSet.getString("ADDRESS"),
        resultSet.getString("REGION"),
        resultSet.getString("WEBSITE"),
        resultSet.getString("DESCRIPTION"),
        resultSet.getString("LOGO_URL"),
        geoLocation
    );
    vineyardCache.addObject(id, vineyard);

    return vineyard;
  }

  /**
   * Extracts the latitude and longitude from the provided ResultSet and creates a new GeoLocation
   * object.
   *
   * @param set The ResultSet from which geolocations are to be extracted
   * @return The extract Geolocation if available, otherwise null if either the latitude or
   *        longitude were null
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
   * Binds listeners to the Vineyard object to ensure that any changes to the vineyards properties
   * are automatically reflected in the database.
   *
   * @param vineyard The Vineyard object to bind listeners to
   */
  private void bindUpdater(Vineyard vineyard) {
    vineyard.nameProperty().addListener((observableValue, before, after) -> {
      try {
        updateAttribute(vineyard.getId(), "NAME", update -> {
          update.setString(1, after);
        });
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    });
    vineyard.addressProperty().addListener((observableValue, before, after) -> {
      try {
        updateAttribute(vineyard.getId(), "ADDRESS", update -> {
          update.setString(1, after);
        });
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    });
    vineyard.regionProperty().addListener((observableValue, before, after) -> {
      try {
        updateAttribute(vineyard.getId(), "REGION", update -> {
          update.setString(1, after);
        });
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    });
    vineyard.websiteProperty().addListener((observableValue, before, after) -> {
      try {
        updateAttribute(vineyard.getId(), "WEBSITE", update -> {
          update.setString(1, after);
        });
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    });
    vineyard.descriptionProperty().addListener((observableValue, before, after) -> {
      try {
        updateAttribute(vineyard.getId(), "DESCRIPTION", update -> {
          update.setString(1, after);
        });
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    });
    vineyard.logoUrlProperty().addListener((observableValue, before, after) -> {
      try {
        updateAttribute(vineyard.getId(), "LOGO_URL", update -> {
          update.setString(1, after);
        });
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    });
  }

  /**
   * Updates a specific attribute of the user in the WINE table.
   *
   * @param attributeName   name of attribute
   * @param attributeSetter callback to set attribute
   */
  private void updateAttribute(long id, String attributeName,
      DatabaseManager.AttributeSetter attributeSetter) throws SQLException {
    Timer timer = new Timer();
    String sql = "UPDATE VINEYARD set " + attributeName + " = ? where ID = ?";
    try (PreparedStatement update = connection.prepareStatement(sql)) {
      attributeSetter.setAttribute(update);
      update.setLong(2, id);

      int rowsAffected = update.executeUpdate();
      if (rowsAffected == 1) {
        log.info("Successfully updated attribute '{}' for vineyard with ID {} in {}ms",
            attributeName, id, timer.currentOffsetMilliseconds());
      } else {
        log.info("Could not update attribute '{}' for vineyard with ID {} in {}ms",
            attributeName, id, timer.currentOffsetMilliseconds());
      }
    }
  }

  /**
   * Checks if the GEOLOCATION table already has data to avoid re-adding default geolocations.
   *
   * @return true if the GEOLOCATION table has data, false otherwise.
   */
  public boolean vineyardsTableHasData() {
    String sql = "SELECT 1 FROM VINEYARD";
    try (Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql)) {
      return resultSet.next();
    } catch (SQLException error) {
      log.error("Failed to check if default vineyard have been added", error);
      return false;
    }
  }

}
