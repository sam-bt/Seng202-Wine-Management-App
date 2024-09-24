package seng202.team6.gui.popup;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Pane;
import org.controlsfx.control.Rating;
import seng202.team6.gui.Controller;
import seng202.team6.managers.ManagerContext;
import seng202.team6.model.WineList;
import seng202.team6.model.WineReview;
import seng202.team6.service.AuthenticationService;
import seng202.team6.service.WineReviewValidator;

public class WineReviewPopupController extends Controller {
  @FXML
  private Pane ratingPane;

  @FXML
  private Button submitButton;

  @FXML
  private TitledPane createModifyReviewTitlePane;

  @FXML
  private TextArea descriptionTextArea;

  private Rating ratingStars;

  private final AuthenticationService authenticationService;
  private final CloseCallback closeCallback;
  private final WineReview existingReview;
  private final long reviewingWineID;

  public WineReviewPopupController(ManagerContext context,
      AuthenticationService authenticationService, CloseCallback closeCallback, WineReview existingReview, long reviewingWineID) {
    super(context);
    this.authenticationService = authenticationService;
    this.closeCallback = closeCallback;
    this.existingReview = existingReview;
    this.reviewingWineID = reviewingWineID;
  }

  @Override
  public void init() {
    createModifyReviewTitlePane.setText((isModifying() ? "Modify" : "Create") + " a Review");
    submitButton.setText(isModifying() ? "Modify" : "Create");

    // create the rating control
    ratingStars = new Rating();
    ratingPane.getChildren().add(ratingStars);

    // limit the number of characters that can be in the description
    descriptionTextArea.textProperty().addListener(((observable, oldText, newText) -> {
      if (newText.length() > WineReviewValidator.MAX_DESCRIPTION_CHARACTERS) {
        descriptionTextArea.setText(newText.substring(0, WineReviewValidator.MAX_DESCRIPTION_CHARACTERS));
      }
    }));

    // set the defaults if we are modifying
    if (isModifying()) {
      ratingStars.setRating(existingReview.getRating());
      descriptionTextArea.setText(existingReview.getDescription());
    }
  }

  @FXML
  void onBackButtonClick() {
    managerContext.GUIManager.mainController.closePopup();
  }

  @FXML
  void onSubmitButtonClick() {
    double rating = ratingStars.getRating();
    String description = descriptionTextArea.getText();
    String username = authenticationService.getAuthenticatedUsername();
    if (isModifying()) {
      existingReview.setRating(rating);
      existingReview.setDescription(description);
      managerContext.databaseManager.updateWineReview(username, reviewingWineID, rating, description);
      if (closeCallback != null)
        closeCallback.onClose(null, existingReview, null);
    } else {
      WineReview createWineView = managerContext.databaseManager.addWineReview(username, reviewingWineID, rating, description);
      if (closeCallback != null)
        closeCallback.onClose(createWineView, null, null);
    }
    managerContext.GUIManager.mainController.closePopup();
  }

  private boolean isModifying() {
    return existingReview != null;
  }

  public interface CloseCallback {
    void onClose(WineReview createdReview, WineReview modifiedReview, WineReview deletedReview);
  }
}
