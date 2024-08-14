package seng202.team0.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Builder;
import seng202.team0.WinoManager;

import java.io.IOException;

/**
 * FXWrapper manages a pane in which the different GUIs are loaded.
 * Based on the code written for SENG201 Tutorial 2.
 */

public class FXWrapper {
    @FXML
    private Pane pane;
    private Stage stage;

    /**
     * Initialises the wrapper.
     * Responsible for creating the WinoManger and passing in functions for FXML.
     * @param stage is the initial stage that gets loaded.
     */
    public void init(Stage stage) {
        this.stage = stage;
        new WinoManager(this::launchHomeScreen,this::launchWineScreen,this::launchListsScreen,this::launchVineyardsScreen,this::launchDataSetsScreen,this::launchConsumptionCalculatorScreen);
    }

    /**
     * Load a new FXML file in to the pane.
     * Also changes the window title and instantiates the controller.
     * @param fxml The path to the FXML file to load.
     * @param title The window title to set.
     * @param builder The controller for this window. Used as a builder.
     */
    private void loadScreen(String fxml, String title, Builder<?> builder) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            // provide a custom Controller with parameters
            loader.setControllerFactory(param -> builder.build());
            Parent parent = loader.load();
            pane.getChildren().clear(); // IMPORTANT
            pane.getChildren().add(parent);
            stage.setTitle(title);
        } catch (IOException e) {
            System.out.println("Failed to load screen:");
            e.getCause();
            e.printStackTrace();
        }
    }

    /**
     * Loads the home screen.
     * @param manager the WinoManager instance.
     */
    public void launchHomeScreen(WinoManager manager) {
        loadScreen("/fxml/home_screen.fxml", "Home", () -> new HomeScreenController(manager));
    }
    /**
     * Loads the wine screen.
     * @param manager the WinoManager instance.
     */
    public void launchWineScreen(WinoManager manager) {
        loadScreen("/fxml/wine_screen.fxml", "Wine Information", () -> new WineScreenController(manager));
    }
    /**
     * Loads the lists screen.
     * @param manager the WinoManager instance.
     */
    public void launchListsScreen(WinoManager manager) {
        loadScreen("/fxml/list_screen.fxml", "My Lists", () -> new WishlistController(manager));
    }
    /**
     * Loads the vineyard screen.
     * @param manager the WinoManager instance.
     */
    public void launchVineyardsScreen(WinoManager manager) {
        loadScreen("/fxml/vineyard_screen.fxml", "Vineyards", () -> new VineyardScreenController(manager));
    }
    /**
     * Loads the datasets screen.
     * @param manager the WinoManager instance.
     */
    public void launchDataSetsScreen(WinoManager manager) {
        loadScreen("/fxml/dataset_screen.fxml", "Manage Data Sets", () -> new DataTableController(manager));
    }
    /**
     * Loads the consumption calculator screen.
     * @param manager the WinoManager instance.
     */
    public void launchConsumptionCalculatorScreen(WinoManager manager) {
        loadScreen("/fxml/consumption_calculator_screen.fxml", "Consumption Calculator", () -> new ConsumptionCalculatorController(manager));
    }

}
