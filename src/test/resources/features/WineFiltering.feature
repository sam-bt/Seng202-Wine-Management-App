Feature: Wine Filtering
  Background:
    Given the wine with title: "Seasonal Collection Syrah", country: "New Zealand", region: "Hawke's Bay", colour: "Red", vintage: 2021, score: 93, abv: 14, price $30.00
    And the wine with title: "Seasonal Collection Lagrein", country: "New Zealand", region: "Hawke's Bay", colour: "Red", vintage: 2021, score: 91, abv: 13, price $25.00
    And the wine with title: "N Block Chardonnay", country: "New Zealand", region: "Waipara", colour: "White", vintage: 2019, score: 88, abv: 5, price $15.00
    And the wine with title: "Australian Wine", country: "Australia", region: "Outback", colour: "White", vintage: 2019, score: 70, abv: 12, price $80.00

  Scenario: Filtering wines by name
    Given the user is viewing the raw wine data
    When the user searches for title containing "Season"
    Then the list of wines is updated to show wines containing name "Season"

  Scenario: Filtering wines by country
    Given the user is viewing the raw wine data
    When the user searches for country matching "New Zealand"
    Then the list of wines is updated to show wines matching country "New Zealand"
    And the list of wines has size 3

  Scenario: Filtering wines by colour
    Given the user is viewing the raw wine data
    When the user searches for colour matching "Red"
    Then the list of wines is updated to show wines matching colour "Red"
    And the list of wines has size 2

  Scenario: Filtering wines by score
    Given the user is viewing the raw wine data
    When the user searches for score between 90 and 100
    Then the list of wines is updated to show wines with score between 90 and 100
    And the list of wines has size 2

  Scenario: Filtering wines by vintage
    Given the user is viewing the raw wine data
    When the user searches for vintage between 2021 and 2024
    Then the list of wines is updated to show wines with vintage between 2021 and 2024

  Scenario: Filtering wines by abv
    Given the user is viewing the raw wine data
    When the user searches for abv between 10.0 and 20.0
    Then the list of wines is updated to show wines with abv between 10.0 and 20.0

  Scenario: Filtering wines by price
    Given the user is viewing the raw wine data
    When the user searches for price between 90 and 100
    Then the list of wines is updated to show wines with price between $90.00 and $100.00

