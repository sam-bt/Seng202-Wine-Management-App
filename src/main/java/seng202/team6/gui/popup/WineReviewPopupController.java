package seng202.team6.gui.popup;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Pane;
import org.controlsfx.control.Rating;
import seng202.team6.gui.Controller;
import seng202.team6.managers.ManagerContext;

public class WineReviewPopupController extends Controller {
  @FXML
  private Pane ratingPane;

  @FXML
  private Button createModifyButton;

  @FXML
  private TitledPane createModifyReviewTitlePane;

  private Rating rating;

  private final boolean isModifying;

  public WineReviewPopupController(ManagerContext context, boolean isModifying) {
    super(context);
    this.isModifying = isModifying;
  }

  @Override
  public void init() {
    createModifyReviewTitlePane.setText((isModifying ? "Modify" : "Create") + " a Review");
    createModifyButton.setText(isModifying ? "Modify" : "Create");

    // create the rating control
    rating = new Rating();
    ratingPane.getChildren().add(rating);
  }

  @FXML
  void onBackButtonClick() {
    managerContext.GUIManager.mainController.closePopup();
  }
}
