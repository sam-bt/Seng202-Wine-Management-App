package seng202.team6.gui.popup;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import seng202.team6.enums.PopupType;
import seng202.team6.gui.Controller;
import seng202.team6.managers.ManagerContext;
import seng202.team6.util.IconPaths;

/**
 * Controller for a general-purpose popup window in the GUI. The controller allows dynamic addition
 * of buttons and content.
 */
public class GeneralPopupController extends Controller {

  @FXML
  private HBox buttonsContainer;

  @FXML
  private Button closeButton;

  @FXML
  private HBox messageContainer;

  @FXML
  private VBox contentContainer;

  @FXML
  private VBox container;

  @FXML
  private Label messageLabel;

  @FXML
  private Label titleLabel;

  private final PopupType popupType;

  /**
   * Constructs a new GeneralPopupController.
   *
   * @param context the manager context
   */
  public GeneralPopupController(ManagerContext context, PopupType popupType) {
    super(context);
    this.popupType = popupType;
  }

  @Override
  public void init() {
    // set up the close icon which shows at the top right of the popup
    SVGPath svgPath = new SVGPath();
    svgPath.getStyleClass().add("icon");
    svgPath.setContent(IconPaths.CLOSE_PATH);
    svgPath.setScaleX(0.05);
    svgPath.setScaleY(0.05);
    closeButton.setGraphic(svgPath);

    // if the popup type has an icon, add it to the message. This requires the popup to have a
    // message.
    Image icon = popupType.getIcon();
    if (icon != null) {
      ImageView iconView = new ImageView(icon);
      iconView.setFitHeight(50);
      iconView.setFitWidth(50);
      iconView.setPreserveRatio(true);
      HBox.setHgrow(iconView, Priority.NEVER);
      messageContainer.getChildren().addFirst(iconView);
    }

    // initially remove the message label unless the setMessage function is called
    container.getChildren().remove(messageContainer);

    // initially remove the content container unless the setContent function is called
    container.getChildren().remove(contentContainer);
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
    if (!container.getChildren().contains(messageContainer)) {
      container.getChildren().addFirst(messageContainer);
    }
  }

  /**
   * Sets the content of the error popup.
   *
   * @param node The content to display in the popup.
   */
  public void setContent(Node node) {
    contentContainer.getChildren().add(node);
    if (!container.getChildren().contains(node)) {
      container.getChildren().addFirst(node);
    }
  }

  /**
   * Adds a button with the specified text and click action to the error popup.
   *
   * @param text The text to display on the button.
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
   * Adds an "Ok" button which closes the popup and preforms no further actions.
   */
  public void addOkButton() {
    addButton("Ok", this::close);
  }

  /**
   * Adds a "Cancel" button which closes the popup and preforms no further actions.
   */
  public void addCancelButton() {
    addButton("Cancel", this::close);
  }

  /**
   * Closes the error popup when the close button is clicked.
   */
  @FXML
  public void close() {
    managerContext.getGuiManager().mainController.closePopup();
  }

  /**
   * Returns the content container.
   *
   * @return the content container
   */
  public VBox getContentContainer() {
    return contentContainer;
  }
}
