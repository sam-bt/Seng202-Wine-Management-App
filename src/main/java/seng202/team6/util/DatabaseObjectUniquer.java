package seng202.team6.util;


import java.util.WeakHashMap;


/**
 * Database objects are required to be unique in memory to assure consistancy
 * <p>
 *   This class implements a simple weak cache to resolve that problem.
 *   Objects are stored with weak references to prevent memory leaks. Map is auto cleared by java
 * </p>
 * @param <T> Object type
 */
public class DatabaseObjectUniquer<T> {

  /**
   * Object map
   */
  private final WeakHashMap<Long, T> objects = new WeakHashMap<>();

  /**
   * Tries to get an object from the cache
   * @param id id
   * @return stored object or null
   */
  public T tryGetObject(long id) {
    return objects.get(id);
  }

  /**
   * Adds an object to the cache
   * @param id id
   * @param object object
   */
  public void addObject(long id, T object) {
    if(objects.containsKey(id))
      throw new IllegalStateException("Duplicate keys are not allowed and attempting indicates a leak");
    objects.put(id, object);
  }
}
