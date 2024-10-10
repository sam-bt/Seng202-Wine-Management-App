package seng202.team6.gui.popup;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import seng202.team6.gui.Controller;
import seng202.team6.managers.ManagerContext;
import seng202.team6.util.IconPaths;

/**
 * The ErrorPopupController class is responsible for controlling the error popup window in the GUI.
 */
public class ErrorPopupController extends Controller {

  @FXML
  private HBox buttonsContainer;

  @FXML
  private Label messageLabel;

  @FXML
  private Label titleLabel;

  @FXML
  private Button closeButton;

  /**
   * Constructs a ErrorPopupController.
   *
   * @param context Manager context
   */
  public ErrorPopupController(ManagerContext context) {
    super(context);
  }

  /**
   * Initializes the error popup.
   */
  @Override
  public void init() {
    SVGPath svgPath = new SVGPath();
    svgPath.getStyleClass().add("icon");
    svgPath.setContent(IconPaths.CLOSE_PATH);
    svgPath.setScaleX(0.05);
    svgPath.setScaleY(0.05);
    closeButton.setGraphic(svgPath);
  }

  /**
   * Sets the title of the error popup.
   *
   * @param title The title to display on the popup.
   */
  public void setTitle(String title) {
    titleLabel.setText(title);
  }

  /**
   * Sets the message of the error popup.
   *
   * @param message The message to display in the popup.
   */
  public void setMessage(String message) {
    messageLabel.setText(message);
  }

  /**
   * Adds a button with the specified text and click action to the error popup.
   *
   * @param text        The text to display on the button.
   * @param clickAction The action to execute when the button is clicked.
   */
  public void addButton(String text, Runnable clickAction) {
    Button button = new Button(text);
    button.getStyleClass().add("secondary-button");
    button.setMinWidth(100);
    button.setFont(Font.font(16));
    button.setOnMouseClicked(event -> clickAction.run());
    buttonsContainer.getChildren().add(button);
  }

  /**
   * Closes the error popup when the close button is clicked.
   */
  @FXML
  public void close() {
    managerContext.getGuiManager().mainController.closePopup();
  }
}
