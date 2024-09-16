package seng202.team6.service;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.SecureRandom;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.util.Base64;

public class EncryptionService {
  private static final int ITERATIONS = 10000;
  private static final int KEY_LENGTH = 256;
  private static final String ALGORITHM = "PBKDF2WithHmacSHA1";

  public static String generateSalt() {
    SecureRandom random = new SecureRandom();
    byte[] salt = new byte[16];
    random.nextBytes(salt);
    return Base64.getEncoder().encodeToString(salt);
  }

  public static String hashPassword(String password, String salt) {
    try {
      PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), Base64.getDecoder().decode(salt),
          ITERATIONS, KEY_LENGTH);
      SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
      byte[] hash = keyFactory.generateSecret(spec).getEncoded();
      return Base64.getEncoder().encodeToString(hash);
    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      System.err.println("Error updating password: " + e.getMessage());
      return null;
    }
  }

  public static boolean verifyPassword(String enteredPassword, String storedHash, String salt){
      String hashOfEnteredPassword = hashPassword(enteredPassword, salt);
      return hashOfEnteredPassword.equals(storedHash);
  }
}
