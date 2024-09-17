package seng202.team6.cucumber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.sql.SQLException;
import seng202.team6.managers.DatabaseManager;
import seng202.team6.model.AuthenticationResponse;
import seng202.team6.service.AuthenticationService;

public class UserLoginStepDefinitions {
  private DatabaseManager databaseManager;
  private AuthenticationService authenticationService;
  private String username;
  private String password;

  @Before
  public void setup() throws SQLException {
    databaseManager = new DatabaseManager();
    authenticationService = new AuthenticationService(databaseManager);
  }

  @After
  public void close() {
    databaseManager.close();
  }

  @Given("the user is not authenticated and is logging in")
  public void theUserIsNotAuthenticatedAndIsLoggingIn() {
    authenticationService.setAuthenticatedUsername(null);
  }

  @When("the user enters an invalid username or password combination")
  public void theUserEntersAnInvalidUsernameOrPasswordCombination() {
    username = "MyAccount";
    password = "MyPassword";
  }

  @When("the user enters a correct username and password combination")
  public void theUserEntersACorrectUsernameAndPasswordCombination() {
    username = "MyAccount";
    password = "MyPassword";
    authenticationService.validateRegistration(username, password, password);
  }

  @Then("the account is logged in")
  public void theAccountIsLoggedIn() {
    AuthenticationResponse response = authenticationService.validateLogin(username, password);
    assertEquals(AuthenticationResponse.LOGIN_SUCCESS, response);
    assertTrue(authenticationService.isAuthenticated());
    assertEquals(username, authenticationService.getAuthenticatedUsername());
  }

  @Then("the account is not logged in")
  public void theAccountIsNotLoggedIn() {
    AuthenticationResponse response = authenticationService.validateLogin(username, password);
    assertEquals(AuthenticationResponse.INVALID_USERNAME_PASSWORD_COMBINATION, response);
    assertFalse(authenticationService.isAuthenticated());
    assertNotEquals(username, authenticationService.getAuthenticatedUsername());
  }
}