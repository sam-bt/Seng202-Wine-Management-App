Feature: User Changing Password
  Scenario: The user enters the correct old password and a valid new password (AT_8)
    Given the user is authenticated and changing their password
    When the user enters in the correct old password
    And the user enters a valid new password
    And the user enters the same valid password to confirm
    Then the accounts password is changed

  Scenario: The user enters an incorrect old password (AT_9)
    Given the user is authenticated and changing their password
    When the user enters an incorrect old password
    And the user enters a valid new password
    And the user enters the same valid password to confirm
    Then the accounts password is not changed

  Scenario: The user enters the correct old password but an invalid new password (AT_10)
    Given the user is authenticated and changing their password
    When the user enters in the correct old password
    And the user enters an invalid new password
    And the user enters the same invalid new confirm password
    Then the accounts password is not changed