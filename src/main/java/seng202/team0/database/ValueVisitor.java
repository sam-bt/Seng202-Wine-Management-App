package seng202.team0.database;

/**
 * Implements visitor pattern for values
 * <p>
 *   This should be the preferred API for accessing values depending on some type.
 * </p>
 * @author Angus McDougall
 */
public interface ValueVisitor {

  /**
   * Visits a real value
   * @param value value
   */
  void visit(double value);

  /**
   * Visits a string value
   * @param string value
   */
  void visit(String string);
}
