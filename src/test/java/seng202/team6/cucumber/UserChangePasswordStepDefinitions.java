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
import seng202.team6.enums.AuthenticationResponse;
import seng202.team6.model.User;
import seng202.team6.util.PasswordUtil;

public class UserChangePasswordStepDefinitions {

  private DatabaseManager databaseManager;
  private AuthenticationManager authenticationManager;
  private String username;
  private String password;
  private String oldPassword;
  private String newPassword;
  private String confirmNewPassword;

  @Before
  public void setup() throws SQLException {
    databaseManager = new DatabaseManager();
    authenticationManager = new AuthenticationManager(databaseManager);
  }

  @Given("the user is authenticated and changing their password")
  public void theUserIsAuthenticatedAndChangingTheirPassword() {
    username = "MyAccount";
    password = "ValidPassword1!";

    AuthenticationResponse registrationResponse = authenticationManager.validateRegistration(
        username, password, password);
    assertEquals(AuthenticationResponse.REGISTER_SUCCESS, registrationResponse);

    AuthenticationResponse loginResponse = authenticationManager.validateLoginPassword(username, password);
    assertEquals(AuthenticationResponse.LOGIN_SUCCESS, loginResponse);
  }


  @When("the user enters an incorrect old password")
  public void theUserEntersAnIncorrectOldPassword() {
    oldPassword = "OtherValidPass1!";
  }

  @When("the user enters in the correct old password")
  public void theUserEntersInTheCorrectOldPassword() {
    oldPassword = password;
  }

  @And("the user enters a valid new password")
  public void theUserEntersAValidNewPassword() {
    newPassword = "NewValidPass1!";
  }

  @And("the user enters the same valid password to confirm")
  public void theUserEntersAValidConfirmPassword() {
    confirmNewPassword = "NewValidPass1!";
  }

  @And("the user enters an invalid new password")
  public void theUserEntersAnInvalidNewPassword() {
    newPassword = "invalidpass";
  }

  @And("the user enters the same invalid new confirm password")
  public void theUserEntersAnInvalidNewConfirmPassword() {
    newPassword = "invalidpass";
  }

  @And("the user enters their username as a password")
  public void theUserEntersUsernameAsPassword() {
    newPassword = "MyAccount";
  }

  @And("the user enters the same password of their username to confirm")
  public void theUserEntersUsernameAsConfirmPassword() {
    confirmNewPassword = "MyAccount";
  }

  @Then("the accounts password is not changed")
  public void theAccountsPasswordIsNotChanged() {
    AuthenticationResponse response = authenticationManager.validateUpdate(username, oldPassword,
        newPassword, confirmNewPassword);
    assertNotEquals(AuthenticationResponse.PASSWORD_CHANGED_SUCCESS, response);

    User user = databaseManager.getUserDao().get(username);
    String storedHash = user.getPassword();
    assertFalse(PasswordUtil.verifyPassword(newPassword, storedHash, user.getSalt()));
    assertTrue(PasswordUtil.verifyPassword(password, storedHash, user.getSalt()));
  }

  @Then("the accounts password is changed")
  public void theAccountsPasswordIsChanged() {
    AuthenticationResponse response = authenticationManager.validateUpdate(username, oldPassword,
        newPassword, confirmNewPassword);
    assertEquals(AuthenticationResponse.PASSWORD_CHANGED_SUCCESS, response);

    User user = databaseManager.getUserDao().get(username);
    String storedHash = user.getPassword();
    assertTrue(PasswordUtil.verifyPassword(newPassword, storedHash, user.getSalt()));
  }
}
