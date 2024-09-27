package seng202.team6.util;

public class Timer {
  private final long startTimeMillis = System.currentTimeMillis();

  public long stop() {
    return System.currentTimeMillis() - startTimeMillis;
  }
}
