package seng202.team6.gui.popup;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Pane;
import org.controlsfx.control.Rating;
import seng202.team6.gui.Controller;
import seng202.team6.managers.ManagerContext;
import seng202.team6.model.User;
import seng202.team6.model.Wine;
import seng202.team6.model.WineReview;
import seng202.team6.service.WineReviewsService;

/**
 * Controller for the review view popup.
 */
public class ReviewViewPopupController extends Controller {

  private final WineReviewsService wineReviewsService;
  private final User reviewer;
  private final WineReview selectedReview;
  private final Wine wine;
  @FXML
  private Pane ratingPane;
  @FXML
  private Label userLabel;
  @FXML
  private Button userButton;
  @FXML
  private Button wineButton;
  @FXML
  private TitledPane reviewTitlePane;
  @FXML
  private Label descriptionLabel;
  @FXML
  private Label dateLabel;

  /**
   * Constructor.
   *
   * @param context            context
   * @param wineReviewsService wine reviews service
   * @param reviewer           reviewing user
   * @param selectedReview     selected review
   * @param wine               wine
   */
  public ReviewViewPopupController(ManagerContext context, WineReviewsService wineReviewsService,
      User reviewer, WineReview selectedReview, Wine wine) {
    super(context);
    this.wineReviewsService = wineReviewsService;
    this.reviewer = reviewer;
    this.selectedReview = selectedReview;
    this.wine = wine;
  }

  @Override
  public void init() {

    reviewTitlePane.setText(
        "Review for " + wine.getTitle());
    dateLabel.setText(selectedReview.getDate().toString());
    userLabel.setText(reviewer.getUsername());
    Rating ratingStars = new Rating();
    ratingStars.setUpdateOnHover(false);
    ratingStars.setMouseTransparent(true);
    ratingStars.setOnMouseClicked(Event::consume);
    ratingStars.setOnMouseDragEntered(Event::consume);
    ratingStars.setRating(selectedReview.getRating());
    ratingPane.getChildren().add(ratingStars);

    if (selectedReview.getDescription().isEmpty()) {
      descriptionLabel.setText("No Description Provided");
    } else {
      descriptionLabel.setText(selectedReview.getDescription());
    }


  }

  @FXML
  void onBackButtonClick() {
    getManagerContext().getGuiManager().closePopup();
  }

  @FXML
  void onWineButtonClick() {
    Runnable backAction = () -> getManagerContext().getGuiManager().openSocialScreen();
    getManagerContext().getGuiManager().closePopup();
    getManagerContext().getGuiManager().openDetailedWineView(wineReviewsService.getWine(),
        backAction);
  }

  @FXML
  void onUserButtonClick() {
    getManagerContext().getGuiManager().closePopup();
    getManagerContext().getGuiManager().openUserProfilePopup(reviewer);
  }


}
