package seng202.team0.database;

/**
 * This class represents a variant of possible DataTable values. Type erasure puts no bound on
 * possible types and is a major source of bugs. We can solve this by enforcing a finite set.
 * Unfortunately there does not appear to be an easy or efficient way of representing a union.
 *
 * @author Angus McDougall
 */
public abstract class Value {


  /**
   * Makes a string value.
   *
   * @param value Value
   * @return String value
   */
  public static Value make(String value) {
    return new StringValue(value);
  }

  /**
   * Makes a real value.
   *
   * @param value Value
   * @return Real value
   */
  public static Value make(double value) {
    return new RealValue(value);
  }

  /**
   * Checks if the contained value is a string.
   *
   * @return If the contained value is a string
   */
  public boolean isString() {
    return this instanceof StringValue;
  }

  /**
   * Checks if the contained value is a real.
   *
   * @return If the contained value is a real
   */
  public boolean isReal() {
    return this instanceof RealValue;
  }

  /**
   * Gets the type index of the contained value. This is deliberately opaque.
   *
   * @return Index of the type
   */
  public abstract int getTypeIndex();

  /**
   * Gets the contained value as a string.
   *
   * @return The current value as a string
   * @throws ClassCastException If the contained value is not a string
   */
  public String getAsString() throws ClassCastException {
    return ((StringValue) this).value;
  }

  /**
   * Gets the contained value as a real.
   *
   * @return The current value as a real
   * @throws ClassCastException If the contained value is not a real
   */
  public double getAsReal() throws ClassCastException {
    return ((RealValue) this).value;
  }

  /**
   * Type representing a string.
   *
   * @author Angus McDougall
   */
  private static class StringValue extends Value {

    public String value;

    StringValue(String value) {
      this.value = value;
    }

    @Override
    public int getTypeIndex() {
      return 0;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj instanceof StringValue value2) {
        return value.equals(value2.value);
      }
      return false;
    }
  }

  /**
   * Type representing a real (currently double).
   *
   * @author Angus McDougall
   */
  private static class RealValue extends Value {

    public double value;

    RealValue(double value) {
      this.value = value;
    }

    @Override
    public int getTypeIndex() {
      return 1;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj instanceof RealValue value2) {
        return value == value2.value;
      }
      return false;
    }
  }

}
