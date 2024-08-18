package seng202.team0.database;

/**
 * This class represents a variant of possible DataTable values
 * <p>
 * Type erasure puts no bound on possible types and is a major source of bugs. We can solve this by
 * enforcing a finite set. Unfortunately there does not appear to be an easy or efficient way of
 * representing a union.
 * </p>
 *
 * @author Angus McDougall
 */
public abstract class Value implements Comparable<Value> {

  private static final int STRING_TYPE_INDEX = 0;
  private static final int REAL_TYPE_INDEX = 1;
  /**
   * Makes a string value
   *
   * @param value Value
   * @return String value
   */
  public static Value make(String value) {
    return new StringValue(value);
  }

  /**
   * Makes a real value
   *
   * @param value Value
   * @return Real value
   */
  public static Value make(double value) {
    return new RealValue(value);
  }

  /**
   * Maybe returns a value constructed from object if it is a valid value
   *
   * @param object object
   * @return maybe a value
   */
  public static Value tryMakeFromObject(Object object) {
    if (object instanceof String string) {
      return make(string);
    } else if (object instanceof Double doubleVal) {
      return make(doubleVal);
    }
    return null;
  }

  /**
   * Gets the type index for a class object
   *
   * @param clazz class object
   * @return index of type if valid else -1
   */
  public static int getTypeIndex(Class<?> clazz) {
    if (clazz == Double.class || clazz == double.class) {
      return REAL_TYPE_INDEX;
    } else if (clazz == String.class) {
      return STRING_TYPE_INDEX;
    }
    return -1;
  }

  /**
   * Checks if a type index is valid
   *
   * @param idx index of type
   * @return if the index refers to a type
   */
  public static boolean isInvalidTypeIndex(int idx) {
    return idx == -1;
  }


  /**
   * Checks if the contained value is a string
   *
   * @return If the contained value is a string
   */
  public boolean isString() {
    return this instanceof StringValue;
  }

  /**
   * Checks if the contained value is a real
   *
   * @return If the contained value is a real
   */
  public boolean isReal() {
    return this instanceof RealValue;
  }

  /**
   * Gets the type index of the contained value. This is deliberately opaque
   *
   * @return Index of the type
   */
  public abstract int getTypeIndex();

  /**
   * Gets the contained value as a string
   *
   * @return The current value as a string
   * @throws ClassCastException If the contained value is not a string
   */
  public String getAsString() throws ClassCastException {
    return ((StringValue) this).value;
  }

  /**
   * Gets the contained value as a real
   *
   * @return The current value as a real
   * @throws ClassCastException If the contained value is not a real
   */
  public double getAsReal() throws ClassCastException {
    return ((RealValue) this).value;
  }

  /**
   * Type representing a string
   *
   * @author Angus McDougall
   */
  private static class StringValue extends Value implements Comparable<Value> {

    public String value;

    StringValue(String value) {
      this.value = value;
    }

    @Override
    public int getTypeIndex() {
      return STRING_TYPE_INDEX;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj instanceof StringValue value2) {
        return value.equals(value2.value);
      }
      return false;
    }

    @Override
    public int compareTo(Value stringValue) {
      return value.compareTo(((StringValue) stringValue).value);
    }
  }

  /**
   * Type representing a real (currently double)
   *
   * @author Angus McDougall
   */
  private static class RealValue extends Value implements Comparable<Value> {

    public double value;

    RealValue(double value) {
      this.value = value;
    }

    @Override
    public int getTypeIndex() {
      return REAL_TYPE_INDEX;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj instanceof RealValue value2) {
        return value == value2.value;
      }
      return false;
    }

    @Override
    public int compareTo(Value value2) {
      return Double.compare(value, ((RealValue) value2).value);
    }
  }
}
