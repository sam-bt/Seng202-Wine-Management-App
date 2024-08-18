package seng202.team0.util;

import java.util.function.Function;
import seng202.team0.database.Record;

/**
 * Filter view (MORE DETAIL HERE!)
 */
public class FilterView extends View {

  /**
   * Contstructor (MORE DETAIL HERE!)
   * @param view
   * @param filter
   */
  public FilterView(View view, Function<Record, Boolean> filter){
    // TODO Implement me!
  }

  /**
   * next (MORE DETAIL HERE!)
   * @return
   */
  @Override
  public Record next() {
    // TODO Implement me!
    return null;
  }

  /**
   * deep copy (MORE DETAIL HERE!)
   * @return
   */
  @Override
  public View deepCopy() {
    // TODO Implement me!
    return null;
  }
}
