package seng202.team6.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seng202.team6.managers.DatabaseManager;
import seng202.team6.model.Vineyard;
import seng202.team6.model.VineyardTour;

/**
 * The TourPlanningService class is responsible for managing the vineyards in a given vineyard tour.
 */
public class TourPlanningService {

  private final DatabaseManager databaseManager;
  private final VineyardTour vineyardTour;
  private final ObservableList<Vineyard> vineyards = FXCollections.observableArrayList();

  /**
   * Constructs a TourPlanningService instance.
   *
   * @param databaseManager the DatabaseManager used to interact with the database
   * @param vineyardTour the VineyardTour that this service manages
   */
  public TourPlanningService(DatabaseManager databaseManager, VineyardTour vineyardTour) {
    this.databaseManager = databaseManager;
    this.vineyardTour = vineyardTour;
  }

  /**
   * Initializes the service by loading all vineyards associated with the current vineyard tour
   * from the database.
   */
  public void init() {
    vineyards.addAll(databaseManager.getVineyardsDao().getAllFromTour(vineyardTour));
  }

  /**
   * Returns the vineyard tour that this service is managing.
   *
   * @return the VineyardTour object being managed
   */
  public VineyardTour getVineyardTour() {
    return vineyardTour;
  }

  /**
   * Returns an observable list of vineyards that are part of the current vineyard tour.
   *
   * @return an observable list containing vineyards in the tour
   */
  public ObservableList<Vineyard> getVineyards() {
    return vineyards;
  }

  /**
   * Adds a vineyard to the current vineyard tour and updates the database.
   *
   * @param vineyard the Vineyard object to be added to the tour
   */
  public void addVineyard(Vineyard vineyard) {
    databaseManager.getVineyardTourDao().addVineyard(vineyardTour, vineyard);
    vineyards.add(vineyard);
  }

  /**
   * Removes a vineyard from the current vineyard tour and updates the database.
   *
   * @param vineyard the Vineyard object to be removed from the tour
   */
  public void removeVineyard(Vineyard vineyard) {
    databaseManager.getVineyardTourDao().removeVineyard(vineyardTour, vineyard);
    vineyards.remove(vineyard);
  }
}
