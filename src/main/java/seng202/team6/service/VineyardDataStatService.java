package seng202.team6.service;

import java.util.HashSet;
import java.util.Set;

/**
 * Service class responsible for managing and providing unique vineyard data.
 */
public class VineyardDataStatService {

  /**
   * A set that holds the unique names of vineyards.
   */
  private final Set<String> uniqueNames = new HashSet<>();

  /**
   * A set that holds the unique addresses of vineyards.
   */
  private final Set<String> uniqueAddresses = new HashSet<>();

  /**
   * A set that holds the unique regions where vineyards are located.
   */
  private final Set<String> uniqueRegions = new HashSet<>();

  /**
   * Retrieves the set of unique vineyard names.
   *
   * @return A Set containing unique vineyard names.
   */
  public Set<String> getUniqueNames() {
    return uniqueNames;
  }

  /**
   * Retrieves the set of unique vineyard addresses.
   *
   * @return A Set containing unique vineyard addresses.
   */
  public Set<String> getUniqueAddresses() {
    return uniqueAddresses;
  }

  /**
   * Retrieves the set of unique vineyard regions.
   *
   * @return A Set containing unique vineyard regions.
   */
  public Set<String> getUniqueRegions() {
    return uniqueRegions;
  }
}
