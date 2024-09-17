Feature: User Login
  Scenario: Login successfully with correct username and password combination (AT_6)
    Given the user is not authenticated and is logging in
    When the user enters a correct username and password combination
    Then the account is logged in

  Scenario: Login with invalid username or password combination (AT_7)
    Given the user is not authenticated and is logging in
    When the user enters an invalid username or password combination
    Then the account is not logged in