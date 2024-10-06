package seng202.team6.model;

public class VineyardFilters {

  private String name;
  private String address;
  private String region;

  public VineyardFilters(String name, String address, String region) {
    this.name = name;
    this.address = address;
    this.region = region;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getRegion() {
    return region;
  }

  public void setRegion(String region) {
    this.region = region;
  }
}
