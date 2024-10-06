package seng202.team6.gui.popup;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import org.controlsfx.control.Rating;
import seng202.team6.gui.Controller;
import seng202.team6.managers.ManagerContext;
import seng202.team6.model.WineReview;
import seng202.team6.service.WineReviewsService;

/**
 * Controller for the wine review popup.
 */
public class WineReviewPopupController extends Controller {

  private final WineReviewsService wineReviewsService;
  @FXML
  private Pane ratingPane;
  @FXML
  private Button submitButton;
  @FXML
  private Button deleteButton;
  @FXML
  private TitledPane createModifyReviewTitlePane;
  @FXML
  private TextArea descriptionTextArea;
  @FXML
  private HBox buttonsContainer;
  @FXML
  private Label characterCountLabel;
  private Rating ratingStars;

  /**
   * Constructor.
   *
   * @param context manager context
   * @param wineReviewsService wine review service
   */
  public WineReviewPopupController(ManagerContext context, WineReviewsService wineReviewsService) {
    super(context);
    this.wineReviewsService = wineReviewsService;
  }

  @Override
  public void init() {
    boolean modifying = wineReviewsService.hasUserReviewed();
    createModifyReviewTitlePane.setText((modifying ? "Modify" : "Create") + " a Review");
    submitButton.setText(modifying ? "Modify" : "Create");

    // create the rating control
    ratingStars = new Rating();
    ratingPane.getChildren().add(ratingStars);

    // limit the number of characters that can be in the description
    descriptionTextArea.textProperty().addListener(((observable, oldText, newText) -> {
      if (newText.length() > WineReviewsService.MAX_DESCRIPTION_CHARACTERS) {
        descriptionTextArea.setText(
            newText.substring(0, WineReviewsService.MAX_DESCRIPTION_CHARACTERS));
      }
      // have to take into account the max limit because descriptionTextArea.setText will not
      // update newText
      int charCount = newText.length();
      characterCountLabel.setText(Math.min(charCount, WineReviewsService.MAX_DESCRIPTION_CHARACTERS)
          + "/" + WineReviewsService.MAX_DESCRIPTION_CHARACTERS);
    }));

    characterCountLabel.textProperty();

    // set the defaults if we are modifying
    if (modifying) {
      WineReview existingReview = wineReviewsService.getUsersReview();
      ratingStars.setRating(existingReview.getRating());
      descriptionTextArea.setText(existingReview.getDescription());
    } else {
      buttonsContainer.getChildren().remove(deleteButton);
    }
  }

  @FXML
  void onBackButtonClick() {
    managerContext.getGuiManager().mainController.closePopup();
  }

  @FXML
  void onDeleteButtonClick() {
    wineReviewsService.deleteUsersReview();
    managerContext.getGuiManager().mainController.closePopup();
  }

  @FXML
  void onSubmitButtonClick() {
    double rating = ratingStars.getRating();
    String description = descriptionTextArea.getText();
    wineReviewsService.addOrUpdateUserReview(rating, description);
    managerContext.getGuiManager().mainController.closePopup();
  }
}
