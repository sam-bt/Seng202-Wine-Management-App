package seng202.team0.util;

import seng202.team0.database.Record;

/**
 * Views provide a way to iterate tables
 * @author Angus McDougall
 */
public abstract class View {

  /**
   * Returns the current record and increments to next
   * @return current record
   */
  public abstract Record next();

  /**
   * Resets the view to the starting element
   */
  public abstract void reset();

  /**
   * Copies the view and all sub views
   *
   * @return copy of this
   */
  public abstract View deepCopy();
}
