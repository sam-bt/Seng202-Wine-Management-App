package seng202.team6.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team6.dao.GeoLocationDao;
import seng202.team6.dao.VineyardDao;
import seng202.team6.model.GeoLocation;
import seng202.team6.model.Vineyard;
import seng202.team6.util.GeolocationResolver;
import seng202.team6.util.ProcessCsv;
import seng202.team6.util.Timer;

/**
 * The VineyardDefaultsService class is responsible for loading default vineyard data from a CSV
 * file and adding it to the database. If enabled, it also resolves missing addresses using the
 * GeolocationResolver.
 */
public class VineyardDefaultsService {

  private final Logger log = LogManager.getLogger(getClass());
  private final GeolocationResolver geolocationResolver;
  private final GeoLocationDao geoLocationDao;
  private final VineyardDao vineyardDao;
  private final boolean resolveMissingAddresses;

  /**
   * Constructs a VineyardDefaultsService instance.
   *
   * @param geoLocationDao          the data access object for geolocation information
   * @param vineyardDao             the data access object for vineyard information
   * @param resolveMissingAddresses a boolean flag to indicate whether missing addresses should be
   *                                resolved using geolocation services
   */
  public VineyardDefaultsService(GeoLocationDao geoLocationDao, VineyardDao vineyardDao,
      boolean resolveMissingAddresses) {
    this.geoLocationDao = geoLocationDao;
    this.vineyardDao = vineyardDao;
    this.resolveMissingAddresses = resolveMissingAddresses;
    this.geolocationResolver = resolveMissingAddresses ? new GeolocationResolver() : null;
  }

  /**
   * Initializes the vineyard data. If the vineyard table is empty, this method loads default
   * vineyard data from a CSV file. If address resolution is enabled, it resolves missing addresses
   * by querying the geolocation API and updating the database.
   */
  public void init() {
    Timer timer = new Timer();
    if (vineyardDao.vineyardsTableHasData()) {
      log.info("Skip loading default vineyards as the VINEYARD table is not empty in {}ms",
          timer.currentOffsetMilliseconds());
      return;
    }

    List<Vineyard> vineyards = loadDefaultVineyards();
    if (resolveMissingAddresses) {
      Set<String> addresses = vineyards.stream().map(Vineyard::getAddress)
          .collect(Collectors.toSet());
      Set<String> addressesInDatabase = geoLocationDao.getExistingLocationNames(addresses);
      List<String> missingAddresses = findMissingAddresses(addresses, addressesInDatabase);
      if (!missingAddresses.isEmpty()) {
        Map<String, GeoLocation> missingAddressesGeolocations = geolocationResolver.resolveAll(
            missingAddresses);
        geoLocationDao.addAll(missingAddressesGeolocations);
      }
    }
    vineyardDao.addAll(vineyards);
  }

  /**
   * Loads the default vineyards from a CSV file.
   *
   * @return a list of default vineyards loaded from the CSV
   */
  private List<Vineyard> loadDefaultVineyards() {
    List<Vineyard> vineyards = new ArrayList<>();
    List<String[]> rows = ProcessCsv.getCsvRows(
        getClass().getResourceAsStream("/data/nz_vineyards.csv"));
    for (int i = 1; i < rows.size(); i++) {
      String[] row = rows.get(i);
      String name = row[0];
      String address = row[1];
      String region = row[2];
      String website = row[3];
      String description = row[4];
      String logoUrl = row[5];
      Vineyard vineyard = new Vineyard(-1, name, address, region, website, description, logoUrl,
          null);
      vineyards.add(vineyard);
    }
    return vineyards;
  }

  /**
   * Finds the addresses that are missing from the geolocation database.
   *
   * @param addresses           a set of addresses to check
   * @param addressesInDatabase a set of addresses already in the database
   * @return a list of addresses that are missing from the database
   */
  private List<String> findMissingAddresses(Set<String> addresses,
      Set<String> addressesInDatabase) {
    List<String> missingAddresses = new ArrayList<>();
    for (String address : addresses) {
      if (!addressesInDatabase.contains(address)) {
        missingAddresses.add(address);
      }
    }
    return missingAddresses;
  }
}
