package seng202.team0.util;

/**
 * Listener for use in the observer pattern
 * @author Angus McDougall
 */
public interface Listener<T> {

  /**
   * Invokes the listener with an object argument
   * @param arg argument
   */
  public abstract void run(T arg);
}
