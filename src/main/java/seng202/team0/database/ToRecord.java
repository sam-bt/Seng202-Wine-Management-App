package seng202.team0.database;

/**
 * Enables adding a record to a table
 *
 * @author Angus McDougall
 */
public interface ToRecord {

  /**
   * This method must fill all the attributes of the record
   * <p>
   * Each attribute should be set on the given record
   * </p>
   *
   * @param record record to set
   */
  void toRecord(Record record);
}
