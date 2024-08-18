package seng202.team0.util;


import java.util.Arrays;
import java.util.Comparator;
import seng202.team0.database.Record;

/**
 * Sorted view provides a way to sort a given view
 *
 * @author Angus McDougalll
 */
public class SortedView extends View {

  private final Record[] records;
  private final View view;
  private int index;
  private Comparator<Record> comparator;

  /**
   * Sorts a view according to a comparator
   *
   * @param view       view to sort
   * @param comparator comparator for record
   */
  public SortedView(View view, Comparator<Record> comparator) {
    this.index = 0;
    this.view = view;
    this.comparator = comparator;
    // Preallocate array
    int elements = 0;
    view.reset();
    while (view.next() != null) {
      elements++;
    }
    view.reset();
    this.records = new Record[elements];
    // Fill and sort elements
    int i = 0;
    Record record;
    while ((record = view.next()) != null) {
      records[i++] = record;
    }
    view.reset();
    Arrays.sort(records, comparator);
  }

  /**
   * Returns the current record and increments to next
   *
   * @return current record
   */
  @Override
  public Record next() {
    return records[index++];
  }

  /**
   * Resets the view to the starting element
   */
  @Override
  public void reset() {
    index = 0;
  }

  /**
   * Copies the view and all sub views
   *
   * @return copy of this
   */
  @Override
  public View deepCopy() {
    return new SortedView(view.deepCopy(), comparator);
  }
}
