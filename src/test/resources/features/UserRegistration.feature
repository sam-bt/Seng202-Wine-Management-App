Feature: User Registration

  Scenario: Register a new user successfully (AT_2)
    Given the user is not authenticated and is registering
    When the user enters a valid username, password, and confirmed password
    Then a new account for the user is created

  Scenario: Registering with an already registered username (AT_3)
    Given the user is not authenticated and is registering
    When the user enters a username which has already been registered
    Then the account is not created

  Scenario: Registering with an invalid username (AT_4)
    Given the user is not authenticated and is registering
    When the user enters an invalid username
    Then the account is not created

  Scenario: Registering with a mismatched password and confirmed password (AT_5)
    Given the user is not authenticated and is registering
    When the user enters a different password and confirmed password
    Then the account is not created

  Scenario: Registering with the same password as username (AT_29)
    Given the user is not authenticated and is registering
    When the user enters the same password as their username
    Then the account is not created

  Scenario: Registering with an invalid username (AT_30)
    Given the user is not authenticated and is registering
    When the user enters an invalid password
    Then the account is not created
