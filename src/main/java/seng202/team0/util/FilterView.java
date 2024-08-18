package seng202.team0.util;

import java.util.function.Function;
import seng202.team0.database.Record;

/**
 * Filter view filters a view according to a predicate
 * @author Angus McDougall
 */
public class FilterView extends View {

  private final View view;
  private final Function<Record, Boolean> filter;
  public FilterView(View view, Function<Record, Boolean> filter){
    this.view = view;
    this.filter = filter;
  }

  @Override
  public Record next() {
    while (true) {
      Record current = view.next();
      if (current == null) {
        return null;
      }
      if (filter.apply(current)) {
        return current;
      }
    }
  }

  /**
   * Resets the view to the starting element
   */
  @Override
  public void reset() {
    view.reset();
  }

  @Override
  public View deepCopy() {
    return new FilterView(view.deepCopy(), filter);
  }
}
