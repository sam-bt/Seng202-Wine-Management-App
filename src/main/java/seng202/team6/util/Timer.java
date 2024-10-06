package seng202.team6.util;

/**
 * Simple class to time method calls.
 */
public class Timer {

  private final long startTimeMillis = System.currentTimeMillis();

  /**
   * Gets the time since construction in milliseconds.
   *
   * @return time since construction
   */
  public long currentOffsetMilliseconds() {
    return System.currentTimeMillis() - startTimeMillis;
  }
}
