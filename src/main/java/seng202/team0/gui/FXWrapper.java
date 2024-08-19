package seng202.team0.gui;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Builder;
import seng202.team0.managers.AuthenticationManager;
import seng202.team0.managers.DatabaseManager;
import seng202.team0.managers.GUIManager;
import seng202.team0.managers.ManagerContext;
import seng202.team0.managers.MapManager;

/**
 * FXWrapper manages a pane in which the different GUIs are loaded.
 * Based on the code written for SENG201 Tutorial 2.
 */

public class FXWrapper {
    @FXML
    private Pane pane;
    private Stage stage;
    private ManagerContext managerContext;

    /**
     * Initialises the wrapper.
     * Responsible for creating the WinoManger and passing in functions for FXML.
     * @param stage is the initial stage that gets loaded.
     */
    public void init(Stage stage) {
        this.stage = stage;
        this.managerContext = new ManagerContext(
            new DatabaseManager(),
            new AuthenticationManager(),
            new MapManager(),
            new GUIManager(this)
        );
        loadScreen("/fxml/main_screen.fxml", "Home", () -> new MainController(this.managerContext));
        //this.managerContext.GUIManager.launchHomeScreen(this.managerContext);
    }

    /**
     * Load a new FXML file in to the pane.
     * Also changes the window title and instantiates the controller.
     * @param fxml The path to the FXML file to load.
     * @param title The window title to set.
     * @param builder The controller for this window. Used as a builder.
     */
    public void loadScreen(String fxml, String title, Builder<?> builder) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            // provide a custom Controller with parameters
            loader.setControllerFactory(param -> builder.build());
            Parent parent = loader.load();
            pane.getChildren().clear(); // IMPORTANT
            pane.getChildren().add(parent);
            stage.setTitle(title);
        } catch (IOException e) {
            System.out.println("Failed to load screen: " + fxml);
            e.printStackTrace();
        }
    }


}
