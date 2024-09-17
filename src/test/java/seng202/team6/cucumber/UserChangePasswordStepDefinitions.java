package seng202.team6.cucumber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.sql.SQLException;
import seng202.team6.managers.AuthenticationManager;
import seng202.team6.managers.DatabaseManager;
import seng202.team6.model.AuthenticationResponse;
import seng202.team6.model.User;
import seng202.team6.service.AuthenticationService;
import seng202.team6.service.EncryptionService;

public class UserChangePasswordStepDefinitions {
  private DatabaseManager databaseManager;
  private AuthenticationManager authenticationManager;
  private AuthenticationService authenticationService;
  private String username;
  private String password;
  private String oldPassword;
  private String newPassword;

  @Before
  public void setup() throws SQLException {
    authenticationManager = new AuthenticationManager();
    databaseManager = new DatabaseManager();
    authenticationService = new AuthenticationService(authenticationManager, databaseManager);
  }

  @Given("the user is authenticated and changing their password")
  public void theUserIsAuthenticatedAndChangingTheirPassword() {
    username = "MyAccount";
    password = "MyPassword";

    AuthenticationResponse registrationResponse = authenticationService.validateRegistration(username, password, password);
    assertEquals(AuthenticationResponse.REGISTER_SUCCESS, registrationResponse);

    AuthenticationResponse loginResponse = authenticationService.validateLogin(username, password);
    assertEquals(AuthenticationResponse.LOGIN_SUCCESS, loginResponse);
  }


  @When("the user enters an incorrect old password")
  public void theUserEntersAnIncorrectOldPassword() {
    oldPassword = "MyOtherPassword";
  }

  @When("the user enters in the correct old password")
  public void theUserEntersInTheCorrectOldPassword() {
    oldPassword = password;
  }

  @And("the user enters a valid new password")
  public void theUserEntersAValidNewPassword() {
    newPassword = "MyNewPassword";
  }

  @And("the user enters an invalid new password")
  public void theUserEntersAnInvalidNewPassword() {
    newPassword = "My&New%Password";
  }

  @Then("the accounts password is not changed")
  public void theAccountsPasswordIsNotChanged() {
    AuthenticationResponse response = authenticationService.validateUpdate(username, oldPassword,
        newPassword);
    assertNotEquals(AuthenticationResponse.PASSWORD_CHANGED_SUCCESS, response);

    User user = databaseManager.getUser(username);
    String storedHash = user.getPassword();
    assertFalse(EncryptionService.verifyPassword(newPassword, storedHash, user.getSalt()));
    assertTrue(EncryptionService.verifyPassword(password, storedHash, user.getSalt()));
  }

  @Then("the accounts password is changed")
  public void theAccountsPasswordIsChanged() {
    AuthenticationResponse response = authenticationService.validateUpdate(username, oldPassword,
        newPassword);
    assertEquals(AuthenticationResponse.PASSWORD_CHANGED_SUCCESS, response);

    User user = databaseManager.getUser(username);
    String storedHash = user.getPassword();
    assertTrue(EncryptionService.verifyPassword(newPassword, storedHash, user.getSalt()));
    assertFalse(EncryptionService.verifyPassword(password, storedHash, user.getSalt()));
  }
}
