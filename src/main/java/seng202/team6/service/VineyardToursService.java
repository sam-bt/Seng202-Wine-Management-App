package seng202.team6.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seng202.team6.enums.Island;
import seng202.team6.managers.AuthenticationManager;
import seng202.team6.managers.DatabaseManager;
import seng202.team6.model.User;
import seng202.team6.model.Vineyard;
import seng202.team6.model.VineyardTour;

public class VineyardToursService {

  private final AuthenticationManager authenticationManager;
  private final DatabaseManager databaseManager;
  private final ObservableList<VineyardTour> vineyardTours = FXCollections.observableArrayList();

  public VineyardToursService(AuthenticationManager authenticationManager,
      DatabaseManager databaseManager) {
    this.authenticationManager = authenticationManager;
    this.databaseManager = databaseManager;
  }

  public void init() {
    User user = authenticationManager.getAuthenticatedUser();
    vineyardTours.addAll(databaseManager.getVineyardTourDao().getAll(user));
  }

  public boolean isVineyardInTour(VineyardTour vineyardTour, Vineyard vineyard) {
    return databaseManager.getVineyardTourDao().isVineyardInTour(vineyardTour, vineyard);
  }

  public void createVineyardTour(String name, Island island) {
    User user = authenticationManager.getAuthenticatedUser();
    VineyardTour vineyardTour = databaseManager.getVineyardTourDao().create(user, name, island);
    if (vineyardTour != null) {
      vineyardTours.add(vineyardTour);
    }
  }

  public ObservableList<VineyardTour> getVineyardTours() {
    return vineyardTours;
  }
}
