package seng202.team6.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seng202.team6.managers.DatabaseManager;
import seng202.team6.model.Vineyard;
import seng202.team6.model.VineyardTour;

import java.util.ArrayList;
import java.util.List;

public class TourPlanningService {
    private final DatabaseManager databaseManager;
    private final VineyardTour vineyardTour;
    private final ObservableList<Vineyard> vineyards = FXCollections.observableArrayList();

    public TourPlanningService(DatabaseManager databaseManager, VineyardTour vineyardTour) {
        this.databaseManager = databaseManager;
        this.vineyardTour = vineyardTour;
    }

    public void init() {
        vineyards.addAll(databaseManager.getVineyardsDAO().getAllFromTour(vineyardTour));
    }

    public VineyardTour getVineyardTour() {
        return vineyardTour;
    }

    public ObservableList<Vineyard> getVineyards() {
        return vineyards;
    }

    public void addVineyard(Vineyard vineyard) {
        databaseManager.getVineyardTourDAO().addVineyard(vineyardTour, vineyard);
        vineyards.add(vineyard);
    }

    public void removeVineyard(Vineyard vineyard) {
        databaseManager.getVineyardTourDAO().removeVineyard(vineyardTour, vineyard);
        vineyards.remove(vineyard);
    }
}
