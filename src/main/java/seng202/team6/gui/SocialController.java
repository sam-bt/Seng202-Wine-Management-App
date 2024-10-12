package seng202.team6.gui;

import java.sql.SQLException;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;
import kotlin.Pair;
import org.apache.logging.log4j.LogManager;
import org.controlsfx.control.RangeSlider;
import seng202.team6.gui.controls.AutoCompletionTextField;
import seng202.team6.gui.controls.ReviewCard;
import seng202.team6.managers.ManagerContext;
import seng202.team6.model.ReviewFilters;
import seng202.team6.model.User;
import seng202.team6.model.Wine;
import seng202.team6.model.WineReview;
import seng202.team6.service.WineReviewsService;
import seng202.team6.util.FilterUtil;


/**
 * Controller for the social screen.
 */
public class SocialController extends Controller {

  @FXML
  AnchorPane filtersPane;
  AutoCompletionTextField usernameTextField;
  AutoCompletionTextField wineNameTextField;
  @FXML
  private TilePane reviewsViewContainer;
  @FXML
  private ReviewFilters currentFilters;
  private RangeSlider ratingSlider;

  /**
   * Constructor.
   *
   * @param managerContext manager context
   */
  public SocialController(ManagerContext managerContext) {
    super(managerContext);
  }

  /**
   * Initializes this controller.
   */
  @Override
  public void init() {

    reviewsViewContainer.setHgap(5);

    this.wineNameTextField = FilterUtil.createAutoCompleteTextField(9.0, 215.0);
    this.usernameTextField = FilterUtil.createAutoCompleteTextField(9.0, 275.0);

    filtersPane.getChildren().add(usernameTextField);
    filtersPane.getChildren().add(wineNameTextField);

    this.ratingSlider = FilterUtil.createSlider(11, 345, 1, 5, 1);
    ratingSlider.setMajorTickUnit(1);
    ratingSlider.setMinorTickCount(0);

    filtersPane.getChildren().add(ratingSlider);

    filtersPane.sceneProperty().addListener((observable, oldScene, newScene) -> {
      FilterUtil.installRangeSliderTooltip(ratingSlider);
    });

    ratingSlider.setSnapToTicks(true);
    try {
      openReviewsInRange(null);
    } catch (SQLException exception) {
      LogManager.getLogger(getClass()).error("Failed to open reviews", exception);
    }

  }

  /**
   * Creates a card for a review.
   *
   * @param review review
   */
  public void createReviewCard(WineReview review, Wine wine) {
    ReviewCard card = new ReviewCard(reviewsViewContainer.widthProperty(),
        reviewsViewContainer.hgapProperty(), review, wine);
    card.setOnMouseClicked(event -> {
      if (event.getClickCount() == 2) {
        openReviewOnClick(review, wine);
      }
    });
    reviewsViewContainer.getChildren().add(card);
  }

  /**
   * Opens a review.
   *
   * @param selectedReview the selected review to open
   * @param selectedWine   the selected wine to open
   */
  @FXML
  public void openReviewOnClick(WineReview selectedReview, Wine selectedWine) {

    String reviewerUsername = selectedReview.getUsername();
    User reviewer = managerContext.getDatabaseManager().getUserDao().get(reviewerUsername);
    WineReviewsService wineReviewsService = new WineReviewsService(
        managerContext.getAuthenticationManager(),
        managerContext.getDatabaseManager(),
        selectedWine);
    managerContext
        .getGuiManager()
        .openPopupReviewView(wineReviewsService, reviewer, selectedReview, selectedWine);
  }

  private void openReviewsInRange(ReviewFilters filters) throws SQLException {

    ObservableList<Pair<WineReview, Wine>> reviews = managerContext.getDatabaseManager()
        .getAggregatedDao().getWineReviewsAndWines(0, 100, filters);

    reviewsViewContainer.getChildren().clear();
    reviews.forEach(pair -> createReviewCard(pair.getFirst(), pair.getSecond()));

  }

  @FXML
  void onSearch() {
    managerContext.getGuiManager().openUserSearchPopup();
  }

  @FXML
  void onApply() throws SQLException {

    currentFilters = new ReviewFilters(
        usernameTextField.getText(),
        wineNameTextField.getText(),
        (int) ratingSlider.getLowValue(),
        (int) ratingSlider.getHighValue()
    );

    openReviewsInRange(currentFilters);

  }

  @FXML
  void onReset() {

    ratingSlider.setHighValue(5);
    ratingSlider.setLowValue(1);
    wineNameTextField.setText("");
    usernameTextField.setText("");

    this.currentFilters = null;
    try {
      openReviewsInRange(null);
    } catch (SQLException exception) {
      LogManager.getLogger(getClass()).error("Failed to open reviews", exception);
    }

  }

}
