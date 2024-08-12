package seng202.team0.unittests.database;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import seng202.team0.database.Value;

/**
 * Tests Value type.
 * @author Angus McDougall
 */
class ValueTest {


  /**
   * Tests type querying.
   */
  @Test
  void is() {
    assertTrue(Value.make(0.0).isReal());
    assertTrue(Value.make("boo").isString());
  }

  /**
   * Tests uniqueness of type index.
   */
  @Test
  void getTypeIndex() {
    assertNotEquals(Value.make(0.0).getTypeIndex(), Value.make("boo").getTypeIndex());
  }

  /**
   * Tests throwing on invalid access.
   */
  @Test
  void throwsOnInvalidAccess() {
    assertThrows(ClassCastException.class, () -> {
      Value.make("foo").getAsReal();
    });
    assertThrows(ClassCastException.class, () -> {
      Value.make(0.0).getAsString();
    });
  }

  /**
   * Tests contained value access.
   */
  @Test
  void get(){
    assertEquals(Value.make(0.0).getAsReal(), 0.0);
    assertEquals(Value.make("foo").getAsString(), "foo");
  }

  /**
   * Tests equality.
   */
  @Test
  void equals(){
    assertEquals(Value.make(1.0), Value.make(1.0));
    assertNotEquals(Value.make(0.0), Value.make(1.0));
    assertEquals(Value.make("foo"), Value.make("foo"));
    assertNotEquals(Value.make("foo"), Value.make("boo"));
    // Different code runs depending on order
    assertNotEquals(Value.make("foo"), Value.make(1.0));
    assertNotEquals(Value.make(1.0), Value.make("foo"));
  }
}