package seng202.team6.gui.wrapper;

import java.io.IOException;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Builder;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import seng202.team6.gui.Controller;
import seng202.team6.gui.MainController;
import seng202.team6.managers.AuthenticationManager;
import seng202.team6.managers.DatabaseManager;
import seng202.team6.managers.GuiManager;
import seng202.team6.managers.ManagerContext;
import seng202.team6.managers.TaskManager;
import seng202.team6.util.GeolocationResolver;

/**
 * FXWrapper manages a pane in which the different GUIs are loaded. Based on the code written for
 * SENG201 Tutorial 2.
 */

public class FxWrapper {

  @FXML
  private Pane pane;
  private Stage stage;

  /**
   * Initialises the wrapper. Responsible for creating the WinoManger and passing in functions for
   * FXML.
   *
   * @param stage is the initial stage that gets loaded.
   */
  public void init(Stage stage) {
    FxWrapper wrapper = this;
    TaskManager taskManager = new TaskManager();
    stage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST,
        event -> taskManager.teardown());

    PauseTransition delay = new PauseTransition(Duration.millis(200));
    delay.setOnFinished(unused -> {
      if (!GeolocationResolver.hasValidApiKey()) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Invalid or missing ORS API Key");
        alert.setContentText("An ORS API key was not found in an .env file or was invalid. "
                + "Please check the readme or manual to find out more.");
        alert.showAndWait();
        stage.close();
        return;
      }
      // load the database manager and add an event to close the database manager when the
      // window closes
      DatabaseManager databaseManager = new DatabaseManager("database",
              "database.db");
      stage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST,
              event -> databaseManager.teardown());

      // create the manager context
      ManagerContext managerContext = new ManagerContext(
              databaseManager,
              new GuiManager(wrapper),
              new AuthenticationManager(databaseManager),
              taskManager);

      // load the main screen
      loadScreen("/fxml/main_screen.fxml", "Home",
              () -> new MainController(managerContext));
    });
    delay.play();
    this.stage = stage;
  }

  /**
   * Sets the window title.
   *
   * @param title title to set
   */
  public void setWindowTitle(String title) {
    stage.setTitle(title);
  }

  /**
   * Load a new FXML file in to the pane. Also changes the window title and instantiates the
   * controller.
   *
   * @param fxml    The path to the FXML file to load.
   * @param title   The window title to set.
   * @param builder The controller for this window. Used as a builder.
   */
  public void loadScreen(String fxml, String title, Builder<?> builder) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
      // provide a custom Controller with parameters
      loader.setControllerFactory(param -> builder.build());
      Parent parent = loader.load();
      // clear the loading screen which is initially on the fx wrapper
      pane.getChildren().clear();
      pane.getChildren().add(parent);
      if (loader.getController() instanceof Controller controller) {
        controller.init();
      }
      stage.setTitle(title);
    } catch (IOException e) {
      LogManager.getLogger(getClass()).error("Failed to load screen: {}", fxml, e);
    }
  }
}
