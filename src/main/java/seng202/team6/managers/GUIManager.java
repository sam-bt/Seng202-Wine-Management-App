package seng202.team6.managers;

import javafx.util.Builder;
import seng202.team6.gui.MainController;
import seng202.team6.gui.wrapper.FxWrapper;

/**
 * Manager for interacting with the GUI
 *
 * @author Angus McDougall
 */
public class GUIManager {

  private final FxWrapper wrapper;
  public MainController mainController;

  /**
   * Constructs a GUIManager
   *
   * @param wrapper fxwrapper
   */
  public GUIManager(FxWrapper wrapper) {
    this.wrapper = wrapper;
  }

  /**
   * Sets the main controller
   * <p>
   * This class requires main controller and main controller requires this. Resolve the circular
   * dependency.
   * </p>
   *
   * @param mainController main controller
   */
  public void setMainController(MainController mainController) {
    this.mainController = mainController;
  }

  /**
   * Sets the window title
   *
   * @param title title to set
   */
  public void setWindowTitle(String title) {
    wrapper.setWindowTitle(title);
  }

  /**
   * Switches the current scene
   *
   * @param fxmlPath path to the fxml to load
   * @param title    title of the scene to display
   * @param builder  builder to instantiate the controller
   */
  private void switchScene(String fxmlPath, String title, Builder<?> builder) {
    mainController.switchScene(fxmlPath, title, builder);
  }

}
