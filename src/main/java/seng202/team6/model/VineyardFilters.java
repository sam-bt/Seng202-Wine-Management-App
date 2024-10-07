package seng202.team6.model;

/**
 * The VineyardFilters represents filters used for filtering vineyard data.
 */
public class VineyardFilters {

  private String name;
  private String address;
  private String region;

  /**
   * Constructs a VineyardFilters instance with specified name, address, and region.
   *
   * @param name    the name of the vineyard to filter
   * @param address the address of the vineyard to filter
   * @param region  the region of the vineyard to filter
   */
  public VineyardFilters(String name, String address, String region) {
    this.name = name;
    this.address = address;
    this.region = region;
  }

  /**
   * Returns the name filter.
   *
   * @return the name of the vineyard to filter
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name filter.
   *
   * @param name the name of the vineyard to filter
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Returns the address filter.
   *
   * @return the address of the vineyard to filter
   */
  public String getAddress() {
    return address;
  }

  /**
   * Sets the address filter.
   *
   * @param address the address of the vineyard to filter
   */
  public void setAddress(String address) {
    this.address = address;
  }

  /**
   * Returns the region filter.
   *
   * @return the region of the vineyard to filter
   */
  public String getRegion() {
    return region;
  }

  /**
   * Sets the region filter.
   *
   * @param region the region of the vineyard to filter
   */
  public void setRegion(String region) {
    this.region = region;
  }
}
