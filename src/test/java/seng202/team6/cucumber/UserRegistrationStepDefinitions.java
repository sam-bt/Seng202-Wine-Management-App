package seng202.team6.cucumber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.sql.SQLException;
import seng202.team6.managers.AuthenticationManager;
import seng202.team6.managers.DatabaseManager;
import seng202.team6.model.AuthenticationResponse;
import seng202.team6.model.User;

public class UserRegistrationStepDefinitions {

  private DatabaseManager databaseManager;
  private AuthenticationManager authenticationManager;
  private String username;
  private String password;
  private String confirmedPassword;

  @Before
  public void setup() throws SQLException {
    databaseManager = new DatabaseManager();
    authenticationManager = new AuthenticationManager(databaseManager);
  }

  @After
  public void close() {
    databaseManager.teardown();
  }

  @Given("the user is not authenticated and is registering")
  public void the_user_is_not_authenticated_and_is_registering() {
    authenticationManager.setAuthenticatedUser(null);
  }

  @When("the user enters a valid username, password, and confirmed password")
  public void the_user_enters_a_valid_username_password_and_confirmed_password() {
    username = "MyAccount";
    password = "ValidPassword1!";
    confirmedPassword = password;
  }

  @When("the user enters a username which has already been registered")
  public void the_user_enters_a_username_which_has_already_been_registered() {
    // Write code here that turns the phrase above into concrete actions
    String existingUsername = "MyAccount";
    String existingPassword = "ValidPassword1!";
    User user = new User(username, password, "user", "salt");
    databaseManager.getUserDAO().add(user);
    username = existingUsername;
    password = "OtherValidPass1!";
    confirmedPassword = password;
  }

  @When("the user enters an invalid username")
  public void the_user_enters_an_invalid_username() {
    username = ".My%Username$";
    password = "ValidPassword1!";
    confirmedPassword = password;
  }

  @When("the user enters an invalid password")
  public void the_user_enters_an_invalid_password() {
    username = "MyUsername";
    password = "invalid";
    confirmedPassword = password;
  }

  @When("the user enters the same password as their username")
  public void the_user_enters_same_pass_as_user() {
    username = "MyUsername";
    password = "MyUsername!";
    confirmedPassword = password;
  }

  @When("the user enters a different password and confirmed password")
  public void the_user_enters_a_different_password_and_confirmed_password() {
    username = "MyAccount";
    password = "ValidPassword1!";
    confirmedPassword = "notValidPassword1!";
  }


  @Then("a new account for the user is created")
  public void a_new_account_for_the_user_is_created() {
    AuthenticationResponse response = authenticationManager.validateRegistration(username, password,
        confirmedPassword);
    assertEquals(AuthenticationResponse.REGISTER_SUCCESS, response);
  }

  @Then("the account is not created")
  public void the_account_is_not_created() {
    AuthenticationResponse response = authenticationManager.validateRegistration(username, password,
        confirmedPassword);
    assertNotEquals(AuthenticationResponse.REGISTER_SUCCESS, response);
  }
}
