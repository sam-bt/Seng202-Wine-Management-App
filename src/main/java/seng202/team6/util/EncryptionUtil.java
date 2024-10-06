package seng202.team6.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import org.apache.logging.log4j.LogManager;

/**
 * Service class providing encryption and password hashing functionality. This class uses
 * PBKDF2WithHmacSHA1 for secure password hashing and verification.
 */
public class EncryptionUtil {

  /**
   * The number of iterations for the PBKDF2WithHmacSHA1 algorithm.
   */
  private static final int ITERATIONS = 10000;

  /**
   * The desired bit-length of the derived key.
   */
  private static final int KEY_LENGTH = 256;

  /**
   * The cryptographic algorithm used for key derivation.
   */
  private static final String ALGORITHM = "PBKDF2WithHmacSHA1";

  /**
   * Generates a random salt for use in password hashing.
   *
   * @return A Base64 encoded string representation of the generated salt.
   */
  public static String generateSalt() {
    SecureRandom random = new SecureRandom();
    byte[] salt = new byte[16];
    random.nextBytes(salt);
    return Base64.getEncoder().encodeToString(salt);
  }

  /**
   * Hashes a password using PBKDF2WithHmacSHA1 with the provided salt.
   *
   * @param password The password to hash.
   * @param salt     The salt to use in the hashing process.
   * @return A Base64 encoded string representation of the hashed password, or null if an error
   * occurs during the hashing process.
   */
  public static String hashPassword(String password, String salt) {
    try {
      PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), Base64.getDecoder().decode(salt),
          ITERATIONS, KEY_LENGTH);
      SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
      byte[] hash = keyFactory.generateSecret(spec).getEncoded();
      return Base64.getEncoder().encodeToString(hash);
    } catch (NoSuchAlgorithmException | InvalidKeySpecException error) {
      LogManager.getLogger(EncryptionUtil.class).error("Error updating password", error);
      return null;
    }
  }

  /**
   * Verifies a password against a given hash.
   *
   * @param enteredPassword The password to verify.
   * @param storedHash      The stored hash to compare against.
   * @param salt            The salt used in the original hashing process.
   * @return true if the entered password matches the stored hash, false otherwise.
   */
  public static boolean verifyPassword(String enteredPassword, String storedHash, String salt) {
    String hashOfEnteredPassword = hashPassword(enteredPassword, salt);
    return hashOfEnteredPassword != null && hashOfEnteredPassword.equals(storedHash);
  }
}