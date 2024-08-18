package seng202.team0.util;

import seng202.team0.database.Record;
import java.util.List;

/**
 * View parent class (MORE DETAIL HERE!)
 */
public abstract class View {

  /**
   * Collect (MORE DETAIL HERE!)
   * @return
   */
  public List<Record> collect(){
    return null;
    // TODO Implement me!
  }

  /**
   * Empty (MORE DETAIL HERE!)
   * @return
   */
  public boolean empty(){
    // TODO Implement me!
    return false;
  }

  /**
   * next (MORE DETAIL HERE!)
   * @return
   */
  public abstract Record next();

  /**
   * Deep copy (MORE DETAIL HERE!)
   * @return
   */
  public abstract View deepCopy();
}
