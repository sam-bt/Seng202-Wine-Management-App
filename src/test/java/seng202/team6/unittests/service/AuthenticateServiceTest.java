package seng202.team6.unittests.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.SQLException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team6.managers.DatabaseManager;
import seng202.team6.model.AuthenticationResponse;
import seng202.team6.service.AuthenticationService;

/**
 * Test class for the AuthenticationService.
 * This class contains unit tests to verify the functionality of the AuthenticationService
 */
public class AuthenticateServiceTest {
  private AuthenticationService authenticationService;
  private DatabaseManager databaseManager;

  /**
   * Sets up the test environment before each test method.
   * Initializes the AuthenticationManager, DatabaseManager, and AuthenticationService.
   *
   * @throws SQLException if there's an error setting up the database connection
   */
  @BeforeEach
  public void setup() throws SQLException {
    databaseManager = new DatabaseManager();
    authenticationService = new AuthenticationService(databaseManager);
  }

  /**
   * Closes the database manager connection after each test method
   */
  @AfterEach
  public void close() {
    databaseManager.close();
  }

  /**
   * Tests a valid user registration scenario.
   */
  @Test
  public void testRegistrationValid() {
    String username = "MyAccount";
    String password = "MyPassword";
    String confirmedPassword = "MyPassword";
    AuthenticationResponse response = authenticationService.validateRegistration(username, password,
        confirmedPassword);
    assertEquals(AuthenticationResponse.REGISTER_SUCCESS, response);
  }

  /**
   * Tests registration attempts with empty fields.
   * Verifies that the service correctly identifies missing username, password, or confirmed password.
   */
  @Test
  public void testRegistrationEmptyFields() {
    String username = "";
    String password = "MyPassword";
    String confirmedPassword = "MyPassword";
    AuthenticationResponse response = authenticationService.validateRegistration(username, password,
        confirmedPassword);
    assertEquals(AuthenticationResponse.MISSING_FIELDS, response);

    username = "MyAccount";
    password = "";
    confirmedPassword = "MyPassword";
    response = authenticationService.validateRegistration(username, password,
        confirmedPassword);
    assertEquals(AuthenticationResponse.MISSING_FIELDS, response);

    username = "MyAccount";
    password = "MyPassword";
    confirmedPassword = "";
    response = authenticationService.validateRegistration(username, password,
        confirmedPassword);
    assertEquals(AuthenticationResponse.MISSING_FIELDS, response);
  }

  /**
   * Tests registration with mismatching password and confirmed password.
   */
  @Test
  public void testRegistrationMismatchingConfirmedPassword() {
    String username = "MyAccount";
    String password = "MyPassword";
    String confirmedPassword = "MyOtherPassword";
    AuthenticationResponse response = authenticationService.validateRegistration(username, password,
        confirmedPassword);
    assertEquals(AuthenticationResponse.MISMATCHING_CONFIRMED_PASSWORD, response);
  }

  /**
   * Tests registration attempts with empty fields.
   * Verifies that the service correctly identifies missing username, password, or confirmed password.
   */
  @Test
  public void testLoginEmptyFields() {
    String username = "";
    String password = "MyPassword";
    AuthenticationResponse response = authenticationService.validateLogin(username, password);
    assertEquals(AuthenticationResponse.MISSING_FIELDS, response);

    username = "MyAccount";
    password = "";
    response = authenticationService.validateLogin(username, password);
    assertEquals(AuthenticationResponse.MISSING_FIELDS, response);
  }

  /**
   * Tests registration with an invalid username (containing special characters).
   */
  @Test
  public void testRegistrationInvalidUsername() {
    String username = "My$Account";
    String password = "MyPassword";
    String confirmedPassword = "MyPassword";
    AuthenticationResponse response = authenticationService.validateRegistration(username, password,
        confirmedPassword);
    assertEquals(AuthenticationResponse.INVALID_USERNAME, response);
  }

  /**
   * Tests registration with an invalid password (containing special characters).
   */
  @Test
  public void testRegistrationInvalidPassword() {
    String username = "MyAccount";
    String password = "My$Password";
    String confirmedPassword = "My$Password";
    AuthenticationResponse response = authenticationService.validateRegistration(username, password,
        confirmedPassword);
    assertEquals(AuthenticationResponse.INVALID_PASSWORD, response);
  }

  /**
   * Tests registration attempt with a username that's already registered.
   */
  @Test
  public void testRegistrationUsernameAlreadyRegistered() {
    String username = "MyAccount";
    String password = "MyPassword";
    String confirmedPassword = "MyPassword";
    registerAccount(username, password);

    // try to register the account again
    AuthenticationResponse response = authenticationService.validateRegistration(username, password,
        confirmedPassword);
    assertEquals(AuthenticationResponse.USERNAME_ALREADY_REGISTERED, response);
  }

  /**
   * Tests a valid login scenario.
   */
  @Test
  public void testLoginValid() {
    String username = "MyAccount";
    String password = "MyPassword";
    registerAccount(username, password);

    AuthenticationResponse response = authenticationService.validateLogin(username, password);
    assertEquals(AuthenticationResponse.LOGIN_SUCCESS, response);
  }

  /**
   * Tests login with an invalid username and password combination.
   */
  @Test
  public void testLoginInvalidUsernamePasswordCombination() {
    String username = "MyAccount";
    String password = "MyPassword";
    AuthenticationResponse response = authenticationService.validateLogin(username, password);
    assertEquals(AuthenticationResponse.INVALID_USERNAME_PASSWORD_COMBINATION, response);
  }

  /**
   * Tests login with a correct username but incorrect password.
   */
  @Test
  public void testLoginCorrectUsernameIncorrectPassword() {
    String username = "MyAccount";
    String password = "MyPassword";
    registerAccount(username, password);

    password = "MyOtherPassword";
    AuthenticationResponse response = authenticationService.validateLogin(username, password);
    assertEquals(AuthenticationResponse.INVALID_USERNAME_PASSWORD_COMBINATION, response);
  }

  /**
   * Tests password update scenarios with missing fields.
   */
  @Test
  public void testUpdateMissingFields() {
    String username = "MyAccount";
    String password = "MyPassword";
    registerAccount(username, password);

    username = "";
    String oldPassword = "MyPassword";
    String newPassword = "MyNewPassword";
    AuthenticationResponse response = authenticationService.validateUpdate(username, oldPassword, newPassword);
    assertEquals(AuthenticationResponse.MISSING_FIELDS, response);

    username = "MyAccount";
    oldPassword = "";
    newPassword = "MyNewPassword";
    response = authenticationService.validateRegistration(username, oldPassword, newPassword);
    assertEquals(AuthenticationResponse.MISSING_FIELDS, response);

    username = "MyAccount";
    oldPassword = "MyPassword";
    newPassword = "";
    response = authenticationService.validateRegistration(username, oldPassword, newPassword);
    assertEquals(AuthenticationResponse.MISSING_FIELDS, response);
  }

  /**
   * Tests password update with an incorrect old password.
   */
  @Test
  public void testUpdateIncorrectOldPassword() {
    String username = "MyAccount";
    String password = "MyPassword";
    registerAccount(username, password);

    String oldPassword = "MyOtherPassword";
    String newPassword = "MyNewPassword";
    AuthenticationResponse response = authenticationService.validateUpdate(username, oldPassword, newPassword);
    assertEquals(AuthenticationResponse.INCORRECT_OLD_PASSWORD, response);
  }

  /**
   * Tests updating the admin password to 'admin', which should be disallowed.
   */
  @Test
  public void testUpdateAdminPasswordToAdmin() {
    String username = "admin";
    String oldPassword = "admin";
    String newPassword = "MyNewPassword";
    AuthenticationResponse response = authenticationService.validateUpdate(username, oldPassword, newPassword);
    assertEquals(AuthenticationResponse.PASSWORD_CHANGED_SUCCESS, response);

    oldPassword = newPassword;
    newPassword = "admin";
    response = authenticationService.validateUpdate(username, oldPassword, newPassword);
    assertEquals(AuthenticationResponse.ADMIN_PASSWORD_CANNOT_BE_ADMIN, response);
  }

  /**
   * Tests password update where the new password is the same as the old password.
   */
  @Test
  public void testUpdateOldPasswordSameAsNew() {
    String username = "MyAccount";
    String password = "MyPassword";
    registerAccount(username, password);

    AuthenticationResponse response = authenticationService.validateUpdate(username, password, password);
    assertEquals(AuthenticationResponse.OLD_PASSWORD_SAME_AS_NEW, response);
  }

  /**
   * Tests password update with an invalid new password (containing special characters).
   */
  @Test
  public void testUpdateInvalidNewPassword() {
    String username = "MyAccount";
    String password = "MyPassword";
    registerAccount(username, password);

    String newPassword = "My$Password";
    AuthenticationResponse response = authenticationService.validateUpdate(username, password, newPassword);
    assertEquals(AuthenticationResponse.INVALID_PASSWORD, response);
  }

  /**
   * Helper method to register an account for testing purposes.
   *
   * @param username the username to register
   * @param password the password to use for registration
   */
  private void registerAccount(String username, String password) {
    AuthenticationResponse response = authenticationService.validateRegistration(username, password,
        password);
    assertEquals(AuthenticationResponse.REGISTER_SUCCESS, response);
  }
}
