package seng202.team0.util;

import seng202.team0.database.Record;

/**
 * Union view combines two sub views into one
 * @author Angus McDougall
 */
public class UnionView extends View{

  View lhs;
  View rhs;

  /**
   * Creates a union view from two sub views
   *
   * @param lhs lhs view
   * @param rhs rhs view
   */
  UnionView(View lhs, View rhs) {
    this.lhs = lhs;
    this.rhs = rhs;
  }

  /**
   * Returns the current record and increments to next
   *
   * @return current record
   */
  @Override
  public Record next() {
    Record lhsRecord = lhs.next();
    if (lhsRecord != null) {
      return lhsRecord;
    }
    return rhs.next();
  }

  /**
   * Resets the view to the starting element
   */
  @Override
  public void reset() {
    lhs.reset();
    rhs.reset();
  }

  /**
   * Copies the view and all sub views
   *
   * @return copy of this
   */
  @Override
  public View deepCopy() {
    return new UnionView(lhs.deepCopy(), rhs.deepCopy());
  }
}
