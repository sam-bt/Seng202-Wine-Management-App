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
import seng202.team6.managers.AuthenticationManager;
import seng202.team6.managers.OldDatabaseManager;
import seng202.team6.model.AuthenticationResponse;

public class UserLogoutStepDefinitions {
  private AuthenticationManager authenticationManager;
  @Before
  public void setup() throws SQLException {
    authenticationManager = new AuthenticationManager(new OldDatabaseManager());
  }

  @Given("the user is authenticated and wants to logout")
  public void theUserIsAuthenticatedAndWantsToLogout() {
    String username = "MyAccount";
    String password = "MyPassword";

    AuthenticationResponse registrationResponse = authenticationManager.validateRegistration(
        username, password, password);
    assertEquals(AuthenticationResponse.REGISTER_SUCCESS, registrationResponse);

    AuthenticationResponse loginResponse = authenticationManager.validateLogin(username, password);
    assertEquals(AuthenticationResponse.LOGIN_SUCCESS, loginResponse);

    assertTrue(authenticationManager.isAuthenticated());
    assertEquals(authenticationManager.getAuthenticatedUsername(), username);
  }

  @When("the user requests to logout")
  public void theUserRequestsToLogout() {
    authenticationManager.logout();
  }

  @Then("the user is logged out of their account")
  public void theUserIsLoggedOutOfTheirAccount() {
    assertFalse(authenticationManager.isAuthenticated());
    assertFalse(authenticationManager.isAdmin());
    assertFalse(authenticationManager.isAdminFirstLogin());
    assertNull(authenticationManager.getAuthenticatedUsername());
  }

}
