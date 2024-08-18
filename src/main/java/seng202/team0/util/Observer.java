package seng202.team0.util;

import java.util.ArrayList;

/**
 * Observer for the observer pattern
 * @author Angus McDougall
 * @param <T> Type of event
 */
public class Observer<T> {

  ArrayList<Listener<T>> listeners;

  /**
   * Connects a listener to this observer
   *
   * @param listener listener
   */
  public void connect(Listener<T> listener) {
    listeners.add(listener);
  }

  /**
   * Runs the listeners with this event
   * @param argument object ot call listeners with
   */
  public void emit(T argument) {
    for (Listener<T> listener : listeners) {
      listener.run(argument);
    }
  }

  /**
   * Removes a listener from this observer
   * <p>
   * NOTE IMPORTANT: this needs to be called manually when destroying objects not tied to this else
   * it will leak the object
   * </p>
   *
   * @param listener listener
   */
  public void remove(Listener<T> listener) {
    listeners.remove(listener);
  }

}
