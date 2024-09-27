package seng202.team6.util;

public class Timer {
  private long startTimeMillis;

  public void start() {
    startTimeMillis = System.currentTimeMillis();
  }

  public long stop() {
    return System.currentTimeMillis() - startTimeMillis;
  }
}
