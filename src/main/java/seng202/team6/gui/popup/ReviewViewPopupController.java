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

public class ReviewViewPopupController extends Controller {

  private final WineReviewsService wineReviewsService;
  private final User reviewer;
  private final WineReview selectedReview;
  private final Wine wine;
  @FXML
  private Pane ratingPane;
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

  public ReviewViewPopupController(ManagerContext context, WineReviewsService wineReviewsService,
      User reviewer, WineReview selectedReview, Wine wine) {
    super(context);
    this.wineReviewsService = wineReviewsService;
    this.reviewer = reviewer;
    this.selectedReview = selectedReview;
    this.wine = wine;
  }

  @Override
  public void init() { // TODO add your personal rating to this screen

    reviewTitlePane.setText(
        "Review by " + reviewer.getUsername() + " for " + wine.getTitle());
    dateLabel.setText(selectedReview.getDate().toString());

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
    managerContext.getGuiManager().mainController.closePopup();
  }

  @FXML
  void onWineButtonClick() {
    Runnable backAction = () -> managerContext.getGuiManager().mainController.openSocialScreen();
    managerContext.getGuiManager().mainController.closePopup();
    managerContext.getGuiManager().mainController.openDetailedWineView(wineReviewsService.getWine(),
        backAction);
  }

  @FXML
  void onUserButtonClick() {
    managerContext.getGuiManager().mainController.closePopup();
  }


}
