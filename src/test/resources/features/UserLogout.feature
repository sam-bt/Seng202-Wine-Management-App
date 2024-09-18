Feature: User Logout
  Scenario: Successfully log out (AT_11)
    Given the user is authenticated and wants to logout
    When the user requests to logout
    Then the user is logged out of their account
