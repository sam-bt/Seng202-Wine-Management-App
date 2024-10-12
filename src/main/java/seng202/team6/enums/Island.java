package seng202.team6.enums;

/**
 * The Island enum represents the two main islands of New Zealand: North Island and South Island.
 */
public enum Island {

  /**
   * Represents the North Island of New Zealand.
   */
  NORTH("North Island", 'N', 0, 6999),

  /**
   * Represents the South Island of New Zealand.
   */
  SOUTH("South Island", 'S', 7000, 9999);

  private final String name;
  private final char code;
  private final int minPostcode;
  private final int maxPostcode;

  /**
   * Constructs an Island enum with the specified name, code, minimum postcode, and maximum
   * postcode.
   *
   * @param name        the full name of the island
   * @param code        a character representing the island
   * @param minPostcode the minimum postcode associated with the island
   * @param maxPostcode the maximum postcode associated with the island
   */
  Island(String name, char code, int minPostcode, int maxPostcode) {
    this.name = name;
    this.code = code;
    this.minPostcode = minPostcode;
    this.maxPostcode = maxPostcode;
  }


  /**
   * Gets the full name of the island.
   *
   * @return the name of the island
   */
  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return name;
  }
}
