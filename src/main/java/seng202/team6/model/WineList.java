package seng202.team6.model;

/**
 * This class represents a list of wines in the database.
 *
 * @param id   id
 * @param name name
 */
public record WineList(long id, String name) {

  /**
   * Converts this object to a string.
   *
   * @return string
   */
  @Override
  public String toString() {
    return name;
  }
}
