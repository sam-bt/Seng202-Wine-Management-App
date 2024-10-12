package seng202.team6.gui.wrapper;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Builder;
import org.apache.logging.log4j.LogManager;
import seng202.team6.gui.Controller;
import seng202.team6.gui.MainController;
import seng202.team6.managers.AuthenticationManager;
import seng202.team6.managers.DatabaseManager;
import seng202.team6.managers.GuiManager;
import seng202.team6.managers.ManagerContext;

/**
 * FXWrapper manages a pane in which the different GUIs are loaded. Based on the code written for
 * SENG201 Tutorial 2.
 */

public class FxWrapper {

  @FXML
  private Pane pane;
  private Stage stage;
  private ManagerContext managerContext;

  /**
   * Initialises the wrapper. Responsible for creating the WinoManger and passing in functions for
   * FXML.
   *
   * @param stage is the initial stage that gets loaded.
   */
  public void init(Stage stage) {
    this.stage = stage;
    try {
      DatabaseManager databaseManager = new DatabaseManager("database", "database.db");
      GuiManager guiManager = new GuiManager(this);
      this.managerContext = new ManagerContext(
          databaseManager,
          guiManager,
          new AuthenticationManager(databaseManager)
      );
      guiManager.setManagerContext(managerContext);
      stage.setOnCloseRequest((event) -> managerContext.getDatabaseManager().teardown());
    } catch (Exception exception) {
      // If we fail to initialize the managers we are kinda screwed
      throw new RuntimeException("Failed to instantiate manager context", exception);
    }
    loadScreen("/fxml/main_screen.fxml", "Home", () -> new MainController(this.managerContext));
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
      pane.getChildren().clear(); // IMPORTANT
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
