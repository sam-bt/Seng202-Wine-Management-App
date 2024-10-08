package seng202.team6.unittests.util;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team6.util.DatabaseObjectUniquer;

/**
 * Tests the database object uniquer
 */
class DatabaseObjectUniquerTest {

  TestClass ref;
  DatabaseObjectUniquer<TestClass> objects = new DatabaseObjectUniquer<>();

  /**
   * Adds a test object
   */
  @BeforeEach
  void setup() {
    ref = new TestClass();
    objects.addObject(1, ref);
  }

  /**
   * Tests getting an object
   */
  @Test
  void tryGetObject() {
    assertEquals(ref, objects.tryGetObject(1));
  }

  /**
   * Tests adding an object
   */
  @Test
  void addObject() {
    TestClass ref2 = new TestClass();
    objects.addObject(2, ref2);
    assertEquals(2, objects.size());

  }

  /**
   * Tests references eventually get cleared from the map
   *
   * @throws InterruptedException if interrupted
   */
  @Test
  void gc() throws InterruptedException {
    for (int i = 0; i < 1000; i++) {
      TestClass ref2 = new TestClass();
      objects.addObject(2 + i, ref2);
      assertEquals(2 + i, objects.size());
    }

    for (int i = 0; i < 1000; i++) {
      objects.tryGarbageCollect();
      System.gc();
      if (objects.size() == 1) {
        break;
      }
      sleep(10);
    }
    assertEquals(1, objects.size());

  }

  static class TestClass {

  }


}