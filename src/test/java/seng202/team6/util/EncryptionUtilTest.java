package seng202.team6.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Base64;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

/**
 * Test class for the EncryptionUtil class.
 * This class contains unit tests to verify the functionality of the encryption utilities,
 * including password hashing, salt generation, and password verification.
 */
public class EncryptionUtilTest {
  /**
   * Tests that the hashing algorithm exists and produces a non-null, non-empty result.
   */
  @Test
  public void testAlgorithmExists() {
    String password = "password";
    String salt = "salt";
    String hashedPassword = EncryptionUtil.hashPassword(password, salt);
    assertNotNull(hashedPassword);
    assertFalse(hashedPassword.isEmpty());
  }

  /**
   * Tests that the hashing function produces consistent results for the same input.
   */
  @Test
  public void testConsistentHash() {
    String password = "password";
    String salt = "salt";
    String hashedPassword = EncryptionUtil.hashPassword(password, salt);
    assertNotNull(hashedPassword);
    assertFalse(hashedPassword.isEmpty());
    for (int i = 0; i < 10; i++) {
      assertEquals(hashedPassword, EncryptionUtil.hashPassword(password, salt));
    }
  }

  /**
   * Tests that the salt generation function produces unique salts.
   */
  @Test
  public void testRandomSalts() {
    Set<String> salts = new HashSet<>();
    for (int i = 0; i < 1000; i++) {
      salts.add(EncryptionUtil.generateSalt());
    }
    assertTrue(salts.size() > 1);
  }

  /**
   * Tests that different salts produce different hash results for the same password.
   */
  @Test
  void testHashPasswordDifferentSalts() {
    String password = "password";
    String salt1 = Base64.getEncoder().encodeToString("salt1".getBytes());
    String salt2 = Base64.getEncoder().encodeToString("salt2".getBytes());
    String hashedPassword1 = EncryptionUtil.hashPassword(password, salt1);
    String hashedPassword2 = EncryptionUtil.hashPassword(password, salt2);
    assertNotEquals(hashedPassword1, hashedPassword2);
  }

  /**
   * Tests that a valid password can be correctly verified against its hash.
   */
  @Test
  public void validateValidPassword() {
    String password = "password";
    String salt = EncryptionUtil.generateSalt();
    String hashedPassword = EncryptionUtil.hashPassword(password, salt);
    assertTrue(EncryptionUtil.verifyPassword(password, hashedPassword, salt));
  }

  /**
   * Tests that an invalid password is correctly rejected during verification.
   */
  @Test
  public void validateInvalidPassword() {
    String password = "password";
    String salt = EncryptionUtil.generateSalt();
    String hashedPassword = EncryptionUtil.hashPassword(password, salt);
    password = "otherpassword";
    assertFalse(EncryptionUtil.verifyPassword(password, hashedPassword, salt));
  }

  /**
   * Tests the password verification behavior when the hashed password is null.
   */
  @Test
  public void validateNullHashedPassword() {
    String password = "password";
    String salt = EncryptionUtil.generateSalt();
    String hashedPassword = null;
    password = "otherpassword";
    assertFalse(EncryptionUtil.verifyPassword(password, hashedPassword, salt));
  }
}
