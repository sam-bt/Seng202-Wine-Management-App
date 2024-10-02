package seng202.team6.cucumber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import seng202.team6.managers.DatabaseManager;
import seng202.team6.model.Filters;
import seng202.team6.model.Wine;

public class WineFilteringStepDefinitions {

  private DatabaseManager databaseManager;
  private Filters filters;
  private List<Wine> filteredWines;

  @Before
  public void setup() throws SQLException {
    databaseManager = new DatabaseManager();
  }

  @After
  public void close() {
    databaseManager.teardown();
  }

  @Given("the user is viewing the raw wine data")
  public void theUserIsViewingTheRawWineData() {
    filters = new Filters();
    filteredWines = new ArrayList<>();
  }

  @And("the wine with title: {string}, country: {string}, region: {string}, colour: {string}, vintage: {int}, score: {int}, abv: {float}, price ${float}")
  public void theWineWithTitleRegionVintageScorePrice$(String title, String country, String region,
      String colour, int vintage, int score, float abv, float price)
      throws SQLException {
    Wine wine = new Wine(
        -1,
        title,
        "",
        country,
        region,
        "",
        colour,
        vintage,
        "",
        score,
        abv,
        price,
        null);
    databaseManager.getWineDAO().addAll(List.of(wine));
  }

  @When("the user searches for title containing {string}")
  public void theUserSearchesForTitleContaining(String title) {
    filters.setTitle(title);
    applySearch();
  }

  @When("the user searches for country matching {string}")
  public void theUserSearchesForCountryMatching(String country) {
    filters.setCountry(country);
    applySearch();
  }

  @When("the user searches for colour matching {string}")
  public void theUserSearchesForColourMatching(String colour) {
    filters.setColor(colour);
    applySearch();
  }


  @When("the user searches for vintage between {int} and {int}")
  public void theUserSearchesForVintageBetweenAnd(int minVintage, int maxVintage) {
    filters.setMinVintage(minVintage);
    filters.setMaxVintage(maxVintage);
    applySearch();
  }

  @When("the user searches for score between {int} and {int}")
  public void theUserSearchesForScoreBetweenAnd(int minScore, int maxScore) {
    filters.setMinScore(minScore);
    filters.setMaxScore(maxScore);
    applySearch();
  }

  @When("the user searches for abv between {double} and {double}")
  public void theUserSearchesForAbvBetweenAnd(double minAbv, double maxAbv) {
    filters.setMinAbv(minAbv);
    filters.setMaxAbv(maxAbv);
    applySearch();
  }

  @When("the user searches for price between {double} and {double}")
  public void theUserSearchesForPriceBetweenAnd(double minPrice, double maxPrice) {
    filters.setMinPrice(minPrice);
    filters.setMaxPrice(maxPrice);
    applySearch();
  }

  @Then("the list of wines is updated to show wines containing name {string}")
  public void theListOfWinesIsUpdatedToShowWinesMatchingName(String title) {
    assertTrue(filteredWines.stream()
        .allMatch(wine -> wine.getTitle().contains(title)));
  }

  @Then("the list of wines is updated to show wines matching country {string}")
  public void theListOfWinesIsUpdatedToShowWinesMatchingCountry(String country) {
    assertTrue(filteredWines.stream()
        .allMatch(wine -> wine.getCountry().equals(country)));
  }

  @Then("the list of wines is updated to show wines matching colour {string}")
  public void theListOfWinesIsUpdatedToShowWinesMatchingColour(String colour) {
    assertTrue(filteredWines.stream()
        .allMatch(wine -> wine.getColor().equals(colour)));
  }


  @Then("the list of wines is updated to show wines with vintage between {int} and {int}")
  public void theListOfWinesIsUpdatedToShowWinesWithVintageBetweenAnd(int minVintage,
      int maxVintage) {
    assertTrue(filteredWines.stream()
        .allMatch(wine -> wine.getVintage() >= minVintage && wine.getVintage() <= maxVintage));
  }

  @Then("the list of wines is updated to show wines with score between {int} and {int}")
  public void theListOfWinesIsUpdatedToShowWinesWithScoreBetweenAnd(int minScore, int maxScore) {
    assertTrue(filteredWines.stream()
        .allMatch(
            wine -> wine.getScorePercent() >= minScore && wine.getScorePercent() <= maxScore));
  }

  @Then("the list of wines is updated to show wines with abv between {double} and {double}")
  public void theListOfWinesIsUpdatedToShowWinesWithAbvBetweenAnd(double minAbv, double maxAbv) {
    assertTrue(filteredWines.stream()
        .allMatch(wine -> wine.getAbv() >= minAbv && wine.getAbv() <= maxAbv));
  }

  @Then("the list of wines is updated to show wines with price between ${double} and ${double}")
  public void theListOfWinesIsUpdatedToShowWinesWithPriceBetweenAnd(double minPrice,
      double maxPrice) {
    assertTrue(filteredWines.stream()
        .allMatch(wine -> wine.getPrice() >= minPrice && wine.getPrice() <= maxPrice));
  }

  private void applySearch() {
    filteredWines = databaseManager.getWineDAO().getAllInRange(0, Integer.MAX_VALUE, filters);
  }

  @And("the list of wines has size {int}")
  public void theListOfWinesHasSize(int size) {
    assertEquals(size, filteredWines.size());
  }
}
