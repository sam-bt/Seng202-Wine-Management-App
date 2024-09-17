Feature: User Authentication
  Scenario: Register a new user successfully
    Given the user is not authenticated and is registering
    When the user enters a valid username, password, and confirmed password
    Then a new account for the user is created

  Scenario: Registering with an already registered username
    Given the user is not authenticated and is registering
    When the user enters a username which has already been registered
    Then the account is not created

  Scenario: Registering with an invalid username
    Given the user is not authenticated and is registering
    When the user enters an invalid username
    Then the account is not created

  Scenario: Registering with a mismatched password and confirmed password
    Given the user is not authenticated and is registering
    When the user enters a different password and confirmed password
    Then the account is not created
