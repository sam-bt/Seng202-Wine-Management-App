package seng202.team6.gui;

import java.sql.Date;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import kotlin.Pair;
import org.controlsfx.control.Rating;
import seng202.team6.dao.WineDao;
import seng202.team6.gui.controls.ReviewCard;
import seng202.team6.gui.controls.WineCard;
import seng202.team6.managers.ManagerContext;
import seng202.team6.model.User;
import seng202.team6.model.Wine;
import seng202.team6.model.WineReview;
import seng202.team6.service.WineReviewsService;


/**
 * Controller for the social screen.
 */
public class SocialController extends Controller {

  @FXML
  private TilePane reviewsViewContainer;

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

    ObservableList<Pair<WineReview, Wine>> reviews = managerContext.getDatabaseManager()
        .getAggregatedDao().getWineReviewsAndWines(0, 100);

    reviewsViewContainer.getChildren().clear();
    reviews.forEach(pair -> createReviewCard(pair.getFirst(), pair.getSecond()));
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
   * @param selectedWine the selected wine to open
   *
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
        .mainController
        .openPopupReviewView(wineReviewsService, reviewer, selectedReview, selectedWine);
  }

  @FXML
  void onSearch() {
    managerContext.getGuiManager().mainController.openUserSearchPopup();
  }

  @FXML
  void onApply() {

  }

  @FXML
  void onReset() {

  }

}
