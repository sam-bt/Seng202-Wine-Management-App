package seng202.team0.managers;

import javafx.util.Builder;
import seng202.team0.gui.FXWrapper;
import seng202.team0.gui.MainController;

/**
 * Manager for interacting with the GUI
 * @author Angus McDougall
 */
public class GUIManager {

  private final FXWrapper wrapper;
  public MainController mainController;

  /**
   * Constructs a GUIManager
   * @param wrapper fxwrapper
   */
  public GUIManager(FXWrapper wrapper) {
    this.wrapper = wrapper;
  }

  /**
   * Sets the main controller
   * <p>
   * This class requires main controller and main controller requires this. Resolve the circular dependency.
   * </p>
   * @param mainController main controller
   */
  public void setMainController(MainController mainController){
    this.mainController = mainController;
  }

  /**
   * Sets the window title
   * @param title title to set
   */
  public void setWindowTitle(String title){
    wrapper.setWindowTitle(title);
  }

  /**
   * Switches the current scene
   * @param fxmlPath path to the fxml to load
   * @param title title of the scene to display
   * @param builder builder to instantiate the controller
   */
  private void switchScene(String fxmlPath, String title, Builder<?> builder) {
    mainController.switchScene(fxmlPath, title, builder);
  }

}
