package seng202.team6.unittests.managers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.SQLException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team6.managers.AuthenticationManager;
import seng202.team6.managers.DatabaseManager;
import seng202.team6.enums.AuthenticationResponse;

/**
 * Test class for the AuthenticationManager. This class contains unit tests to verify the
 * functionality of the authenticationManager
 */
public class AuthenticationManagerTest {

  // A valid password needs upper and lowercase letters, a symbol, and a number, and is 8-30 chars long.
  String validPass = "testPassword1!";
  private AuthenticationManager authenticationManager;
  private DatabaseManager databaseManager;

  /**
   * Sets up the test environment before each test method. Initializes the AuthenticationManager and
   * DatabaseManager.
   *
   * @throws SQLException if there's an error setting up the database connection
   */
  @BeforeEach
  public void setup() throws SQLException {
    databaseManager = new DatabaseManager();
    authenticationManager = new AuthenticationManager(databaseManager);
  }

  /**
   * Closes the database manager connection after each test method
   */
  @AfterEach
  public void close() {
    databaseManager.teardown();
  }

  /**
   * Tests a valid user registration scenario.
   */
  @Test
  public void testRegistrationValid() {
    String username = "MyAccount";
    String password = validPass;
    String confirmedPassword = validPass;
    AuthenticationResponse response = authenticationManager.validateRegistration(username, password,
        confirmedPassword);
    assertEquals(AuthenticationResponse.REGISTER_SUCCESS, response);
  }

  /**
   * Tests registration attempts with empty fields. Verifies that the service correctly identifies
   * missing username, password, or confirmed password.
   */
  @Test
  public void testRegistrationEmptyFields() {
    String username = "";
    String password = validPass;
    String confirmedPassword = validPass;
    AuthenticationResponse response = authenticationManager.validateRegistration(username, password,
        confirmedPassword);
    assertEquals(AuthenticationResponse.MISSING_FIELDS, response);

    username = "MyAccount";
    password = "";
    confirmedPassword = validPass;
    response = authenticationManager.validateRegistration(username, password,
        confirmedPassword);
    assertEquals(AuthenticationResponse.MISSING_FIELDS, response);

    username = "MyAccount";
    password = validPass;
    confirmedPassword = "";
    response = authenticationManager.validateRegistration(username, password,
        confirmedPassword);
    assertEquals(AuthenticationResponse.MISSING_FIELDS, response);
  }

  /**
   * Tests registration with mismatching password and confirmed password.
   */
  @Test
  public void testRegistrationMismatchingConfirmedPassword() {
    String username = "MyAccount";
    String password = validPass;
    String confirmedPassword = "OtherValidPass2024!";
    AuthenticationResponse response = authenticationManager.validateRegistration(username, password,
        confirmedPassword);
    assertEquals(AuthenticationResponse.MISMATCHING_CONFIRMED_PASSWORD, response);
  }

  /**
   * Tests registration with the password the same as the username.
   */
  @Test
  public void testRegistrationPassEqualUsername() {
    String username = "MyAccount";
    AuthenticationResponse response = authenticationManager.validateRegistration(username, username,
        username);
    assertEquals(AuthenticationResponse.SAME_AS_USERNAME, response);
  }


  /**
   * Tests registration attempts with empty fields. Verifies that the service correctly identifies
   * missing username, password, or confirmed password.
   */
  @Test
  public void testLoginEmptyFields() {
    String username = "";
    String password = validPass;
    AuthenticationResponse response = authenticationManager.validateLogin(username, password);
    assertEquals(AuthenticationResponse.MISSING_FIELDS, response);

    username = "MyAccount";
    password = "";
    response = authenticationManager.validateLogin(username, password);
    assertEquals(AuthenticationResponse.MISSING_FIELDS, response);
  }

  /**
   * Tests registration with an invalid username (containing special characters).
   */
  @Test
  public void testRegistrationInvalidUsername() {
    String username = "My$Account";
    String password = validPass;
    String confirmedPassword = validPass;
    AuthenticationResponse response = authenticationManager.validateRegistration(username, password,
        confirmedPassword);
    assertEquals(AuthenticationResponse.INVALID_USERNAME, response);
  }

  /**
   * Tests registration with an invalid password (containing special characters).
   */
  @Test
  public void testRegistrationInvalidPassword() {
    String username = "MyAccount";
    String password = "invalidpass";
    String confirmedPassword = "invalidpass";
    AuthenticationResponse response = authenticationManager.validateRegistration(username, password,
        confirmedPassword);
    assertEquals(AuthenticationResponse.INVALID_PASSWORD, response);
  }

  /**
   * Tests registration attempt with a username that's already registered.
   */
  @Test
  public void testRegistrationUsernameAlreadyRegistered() {
    String username = "MyAccount";
    String password = validPass;
    String confirmedPassword = validPass;
    registerAccount(username, password);

    // try to register the account again
    AuthenticationResponse response = authenticationManager.validateRegistration(username, password,
        confirmedPassword);
    assertEquals(AuthenticationResponse.USERNAME_ALREADY_REGISTERED, response);
  }

  /**
   * Tests a valid login scenario.
   */
  @Test
  public void testLoginValid() {
    String username = "MyAccount";
    String password = validPass;
    registerAccount(username, password);

    AuthenticationResponse response = authenticationManager.validateLogin(username, password);
    assertEquals(AuthenticationResponse.LOGIN_SUCCESS, response);
  }

  /**
   * Tests a valid user registration scenario.
   */
  @Test
  public void testUpdatePassEqualUsername() {
    String username = "MyAccount";
    String password = validPass;
    registerAccount(username, password);

    AuthenticationResponse response = authenticationManager.validateUpdate(username, password,
        username, username);
    assertEquals(AuthenticationResponse.SAME_AS_USERNAME, response);
  }

  /**
   * Tests login with an invalid username and password combination.
   */
  @Test
  public void testLoginInvalidUsernamePasswordCombination() {
    String username = "MyAccount";
    String password = validPass;
    AuthenticationResponse response = authenticationManager.validateLogin(username, password);
    assertEquals(AuthenticationResponse.INVALID_USERNAME_PASSWORD_COMBINATION, response);
  }

  /**
   * Tests login with a correct username but incorrect password.
   */
  @Test
  public void testLoginCorrectUsernameIncorrectPassword() {
    String username = "MyAccount";
    String password = validPass;
    registerAccount(username, password);

    password = "OtherValidPass2024!";
    AuthenticationResponse response = authenticationManager.validateLogin(username, password);
    assertEquals(AuthenticationResponse.INVALID_USERNAME_PASSWORD_COMBINATION, response);
  }

  /**
   * Tests password update scenarios with missing fields.
   */
  @Test
  public void testUpdateMissingFields() {
    String username = "MyAccount";
    String password = validPass;
    registerAccount(username, password);

    username = "";
    String oldPassword = validPass;
    String newPassword = validPass;
    String confirmNewPassword = "OtherValidPass2024!";
    AuthenticationResponse response = authenticationManager.validateUpdate(username, oldPassword,
        newPassword, confirmNewPassword);
    assertEquals(AuthenticationResponse.MISSING_FIELDS, response);

    username = "MyAccount";
    oldPassword = "";
    newPassword = "OtherValidPass2024!";
    response = authenticationManager.validateRegistration(username, oldPassword, newPassword);
    assertEquals(AuthenticationResponse.MISSING_FIELDS, response);

    username = "MyAccount";
    oldPassword = validPass;
    newPassword = "";
    response = authenticationManager.validateRegistration(username, oldPassword, newPassword);
    assertEquals(AuthenticationResponse.MISSING_FIELDS, response);
  }

  /**
   * Tests password update with an incorrect old password.
   */
  @Test
  public void testUpdateIncorrectOldPassword() {
    String username = "MyAccount";
    String password = validPass;
    registerAccount(username, password);

    String oldPassword = "OtherValidPass2024!";
    String newPassword = "NewValidPass2024!";
    String confirmNewPassword = "NewValidPass2024!";
    AuthenticationResponse response = authenticationManager.validateUpdate(username, oldPassword,
        newPassword, confirmNewPassword);
    assertEquals(AuthenticationResponse.INCORRECT_OLD_PASSWORD, response);
  }

  /**
   * Tests updating the admin password to 'admin', which should be disallowed.
   */
  @Test
  public void testUpdateAdminPasswordToAdmin() {
    String username = "admin";
    String oldPassword = "admin";
    String newPassword = validPass;
    String confirmNewPassword = validPass;
    AuthenticationResponse response = authenticationManager.validateUpdate(username, oldPassword,
        newPassword, confirmNewPassword);
    assertEquals(AuthenticationResponse.PASSWORD_CHANGED_SUCCESS, response);

    oldPassword = newPassword;
    newPassword = "admin";
    confirmNewPassword = "admin";
    response = authenticationManager.validateUpdate(username, oldPassword, newPassword,
        confirmNewPassword);
    assertEquals(AuthenticationResponse.ADMIN_PASSWORD_CANNOT_BE_ADMIN, response);
  }

  /**
   * Tests password update where the new password is the same as the old password.
   */
  @Test
  public void testUpdateOldPasswordSameAsNew() {
    String username = "MyAccount";
    String password = validPass;
    String confirmNewPassword = validPass;
    registerAccount(username, password);

    AuthenticationResponse response = authenticationManager.validateUpdate(username, password,
        password, confirmNewPassword);
    assertEquals(AuthenticationResponse.OLD_PASSWORD_SAME_AS_NEW, response);
  }

  /**
   * Tests password update with an invalid new password (does not meet requirements)
   */
  @Test
  public void testUpdateInvalidNewPassword() {
    String username = "MyAccount";
    String password = validPass;
    registerAccount(username, password);

    String newPassword = "invalidpass";
    String confirmNewPassword = "invalidpass";
    AuthenticationResponse response = authenticationManager.validateUpdate(username, password,
        newPassword, confirmNewPassword);
    assertEquals(AuthenticationResponse.INVALID_PASSWORD, response);
  }

  /**
   * Tests password update with non-matching new passwords
   */
  @Test
  public void testUpdateWrongConfirmPassword() {
    String username = "MyAccount";
    String password = validPass;
    registerAccount(username, password);

    String newPassword = "OtherValidPass2024!";
    String confirmNewPassword = "NotOtherValidPass2024!";
    AuthenticationResponse response = authenticationManager.validateUpdate(username, password,
        newPassword, confirmNewPassword);
    assertEquals(AuthenticationResponse.MISMATCHING_CONFIRMED_PASSWORD, response);
  }

  /**
   * Helper method to register an account for testing purposes.
   *
   * @param username the username to register
   * @param password the password to use for registration
   */
  private void registerAccount(String username, String password) {
    AuthenticationResponse response = authenticationManager.validateRegistration(username, password,
        password);
    assertEquals(AuthenticationResponse.REGISTER_SUCCESS, response);
  }
}
