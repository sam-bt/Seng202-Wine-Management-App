package seng202.team6.cucumber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.sql.SQLException;
import seng202.team6.managers.DatabaseManager;
import seng202.team6.model.AuthenticationResponse;
import seng202.team6.service.AuthenticationService;

public class UserLogoutStepDefinitions {
  private AuthenticationService authenticationService;

  @Before
  public void setup() throws SQLException {
    DatabaseManager databaseManager = new DatabaseManager();
    authenticationService = new AuthenticationService(databaseManager);
  }

  @Given("the user is authenticated and wants to logout")
  public void theUserIsAuthenticatedAndWantsToLogout() {
    String username = "MyAccount";
    String password = "MyPassword";

    AuthenticationResponse registrationResponse = authenticationService.validateRegistration(
        username, password, password);
    assertEquals(AuthenticationResponse.REGISTER_SUCCESS, registrationResponse);

    AuthenticationResponse loginResponse = authenticationService.validateLogin(username, password);
    assertEquals(AuthenticationResponse.LOGIN_SUCCESS, loginResponse);

    assertTrue(authenticationService.isAuthenticated());
    assertEquals(authenticationService.getAuthenticatedUsername(), username);
  }

  @When("the user requests to logout")
  public void theUserRequestsToLogout() {
    authenticationService.logout();
  }

  @Then("the user is logged out of their account")
  public void theUserIsLoggedOutOfTheirAccount() {
    assertFalse(authenticationService.isAuthenticated());
    assertFalse(authenticationService.isAdmin());
    assertFalse(authenticationService.isAdminFirstLogin());
    assertNull(authenticationService.getAuthenticatedUsername());
  }

}
