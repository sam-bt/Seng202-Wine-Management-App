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
   * Determines the island based on the given postcode.
   *
   * @param postcode the postcode to check
   * @return the Island enum corresponding to the postcode, otherwise null
   */
  public static Island byPostcode(int postcode) {
    for (Island value : values()) {
      if (postcode >= value.getMinPostcode() && postcode <= value.getMaxPostcode()) {
        return value;
      }
    }
    return null;
  }

  /**
   * Determines the island based on the given code.
   *
   * @param code the code to check
   * @return the Island enum corresponding to the code, otherwise null
   */
  public static Island byCode(char code) {
    for (Island value : values()) {
      if (value.getCode() == code) {
        return value;
      }
    }
    return null;
  }

  /**
   * Gets the full name of the island.
   *
   * @return the name of the island
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the character code representing the island.
   *
   * @return the character code of the island
   */
  public char getCode() {
    return code;
  }

  /**
   * Gets the minimum postcode associated with the island.
   *
   * @return the minimum postcode of the island
   */
  public int getMinPostcode() {
    return minPostcode;
  }

  /**
   * Gets the maximum postcode associated with the island.
   *
   * @return the maximum postcode of the island
   */
  public int getMaxPostcode() {
    return maxPostcode;
  }

  @Override
  public String toString() {
    return name;
  }
}
