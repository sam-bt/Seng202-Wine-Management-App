package seng202.team6.unittests.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Base64;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team6.gui.MainController;
import seng202.team6.managers.AuthenticationManager;
import seng202.team6.managers.GuiManager;
import seng202.team6.managers.ManagerContext;
import seng202.team6.util.PasswordUtil;

/**
 * Test class for the PasswordUtil class. This class contains unit tests to verify the
 * functionality of the encryption utilities, including password hashing, salt generation, and
 * password verification.
 */
class PasswordUtilTest {

  private ManagerContext managerContext;
  private AuthenticationManager authenticationManager;
  private GuiManager guiManager;

  @BeforeEach
  void setUp() {
    managerContext = mock(ManagerContext.class);
    authenticationManager = mock(AuthenticationManager.class);
    guiManager = mock(GuiManager.class);

    when(managerContext.getAuthenticationManager()).thenReturn(authenticationManager);
    when(managerContext.getGuiManager()).thenReturn(guiManager);
  }

  /**
   * Tests that disabled is unchanged when it is not the admin's first login.
   */
  @Test
  void testCheckAdminLoginNotFirstLogin() {
    when(authenticationManager.isAdminFirstLogin()).thenReturn(false);
    boolean result = PasswordUtil.checkAdminLogin(managerContext, true);
    assertTrue(result);
    result = PasswordUtil.checkAdminLogin(managerContext, false);
    assertFalse(result);
  }

  /**
   * Tests that the hashing algorithm exists and produces a non-null, non-empty result.
   */
  @Test
  public void testAlgorithmExists() {
    String password = "password";
    String salt = "salt";
    String hashedPassword = PasswordUtil.hashPassword(password, salt);
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
    String hashedPassword = PasswordUtil.hashPassword(password, salt);
    assertNotNull(hashedPassword);
    assertFalse(hashedPassword.isEmpty());
    for (int i = 0; i < 10; i++) {
      assertEquals(hashedPassword, PasswordUtil.hashPassword(password, salt));
    }
  }

  /**
   * Tests that the salt generation function produces unique salts.
   */
  @Test
  public void testRandomSalts() {
    Set<String> salts = new HashSet<>();
    for (int i = 0; i < 1000; i++) {
      salts.add(PasswordUtil.generateSalt());
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
    String hashedPassword1 = PasswordUtil.hashPassword(password, salt1);
    String hashedPassword2 = PasswordUtil.hashPassword(password, salt2);
    assertNotEquals(hashedPassword1, hashedPassword2);
  }

  /**
   * Tests that a valid password can be correctly verified against its hash.
   */
  @Test
  public void validateValidPassword() {
    String password = "password";
    String salt = PasswordUtil.generateSalt();
    String hashedPassword = PasswordUtil.hashPassword(password, salt);
    assertTrue(PasswordUtil.verifyPassword(password, hashedPassword, salt));
  }

  /**
   * Tests that an invalid password is correctly rejected during verification.
   */
  @Test
  public void validateInvalidPassword() {
    String password = "password";
    String salt = PasswordUtil.generateSalt();
    String hashedPassword = PasswordUtil.hashPassword(password, salt);
    password = "otherpassword";
    assertFalse(PasswordUtil.verifyPassword(password, hashedPassword, salt));
  }

  /**
   * Tests the password verification behavior when the hashed password is null.
   */
  @Test
  public void validateNullHashedPassword() {
    String password = "password";
    String salt = PasswordUtil.generateSalt();
    String hashedPassword = null;
    password = "otherpassword";
    assertFalse(PasswordUtil.verifyPassword(password, hashedPassword, salt));
  }







}
