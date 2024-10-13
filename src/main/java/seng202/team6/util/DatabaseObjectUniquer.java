package seng202.team6.util;


import java.lang.ref.WeakReference;
import java.util.HashMap;


/**
 * Database objects are required to be unique in memory to assure consistency.
 * <p>
 * This class implements a simple weak cache to resolve that problem. Objects are stored with weak
 * references to prevent memory leaks.
 * </p>
 *
 * @param <T> Object type
 */
public class DatabaseObjectUniquer<T> {

  /**
   * Object map.
   */
  private final HashMap<Long, WeakReference<T>> objects = new HashMap<>();

  /**
   * Timer for garbage collection.
   */
  private int garbageCollectionTimer = 0;

  /**
   * Tries to remove outdated references.
   */
  public void tryGarbageCollect() {
    objects.values().removeIf(object -> object.get() == null);
  }

  /**
   * Tries to get an object from the cache.
   *
   * @param id id
   * @return stored object or null
   */
  public T tryGetObject(long id) {
    WeakReference<T> ref = objects.get(id);
    if (ref == null) {
      return null;
    }
    T strongRef = ref.get();
    if (strongRef == null) {
      objects.remove(id);
    }
    return strongRef;
  }

  /**
   * Adds an object to the cache.
   *
   * @param id     id
   * @param object object
   */
  public void addObject(long id, T object) {
    if (garbageCollectionTimer++ == 4096) {
      tryGarbageCollect();
      garbageCollectionTimer = 0;
    }
    if (objects.containsKey(id)) {
      throw new IllegalStateException(
          "Duplicate keys are not allowed and attempting indicates a leak");
    }
    objects.put(id, new WeakReference<>(object));
  }

  /**
   * Removes an object from the cache.
   *
   * @param id id
   */
  public void removeObject(long id) {
    objects.remove(id);
  }

  /**
   * Removes all objects from the cache.
   */
  public void removeAll() {
    objects.clear();
  }

  /**
   * Returns an upper bound for alive objects.
   *
   * @return number of objects in map
   */
  public int size() {
    return objects.size();
  }

  /**
   * Clears all objects in cache.
   */
  public void clear() {
    objects.clear();
  }


}
