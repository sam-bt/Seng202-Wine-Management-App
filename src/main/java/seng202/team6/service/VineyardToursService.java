package seng202.team6.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seng202.team6.enums.Island;
import seng202.team6.managers.AuthenticationManager;
import seng202.team6.managers.DatabaseManager;
import seng202.team6.model.User;
import seng202.team6.model.Vineyard;
import seng202.team6.model.VineyardTour;

/**
 * The VineyardToursService class handles operations related to vineyard tours, including creating
 * tours, checking if a vineyard is part of a tour, and retrieving tours associated with the
 * currently authenticated user.
 */
public class VineyardToursService {

  private final AuthenticationManager authenticationManager;
  private final DatabaseManager databaseManager;
  private final ObservableList<VineyardTour> vineyardTours = FXCollections.observableArrayList();

  /**
   * Constructs a VineyardToursService instance.
   *
   * @param authenticationManager the authentication manager used to retrieve the authenticated
   *                              user.
   * @param databaseManager       the database manager used to interact with the vineyard tours
   *                              database.
   */
  public VineyardToursService(AuthenticationManager authenticationManager,
      DatabaseManager databaseManager) {
    this.authenticationManager = authenticationManager;
    this.databaseManager = databaseManager;
  }

  /**
   * Initializes the vineyard tours list by loading all tours associated with the authenticated user
   * from the database.
   */
  public void init() {
    User user = authenticationManager.getAuthenticatedUser();
    vineyardTours.addAll(databaseManager.getVineyardTourDao().getAll(user));
  }

  /**
   * Checks if a specified vineyard is part of the given vineyard tour.
   *
   * @param vineyardTour the vineyard tour to check
   * @param vineyard     the vineyard to check for
   * @return true if the vineyard is part of the tour, false otherwise
   */
  public boolean isVineyardInTour(VineyardTour vineyardTour, Vineyard vineyard) {
    return databaseManager.getVineyardTourDao().isVineyardInTour(vineyardTour, vineyard);
  }

  /**
   * Creates a new vineyard tour for the authenticated user.
   *
   * @param name   the name of the vineyard tour
   * @param island the island where the tour is located
   */
  public void createVineyardTour(String name, Island island) {
    User user = authenticationManager.getAuthenticatedUser();
    VineyardTour vineyardTour = databaseManager.getVineyardTourDao().create(user, name, island);
    if (vineyardTour != null) {
      vineyardTours.add(vineyardTour);
    }
  }

  /**
   * Retrieves the list of vineyard tours for the authenticated user.
   *
   * @return an observable list of vineyard tours
   */
  public ObservableList<VineyardTour> getVineyardTours() {
    return vineyardTours;
  }
}
