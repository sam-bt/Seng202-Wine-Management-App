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
import kotlin.Pair;
import org.controlsfx.control.Rating;
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
  TableView<Pair<WineReview, Wine>> reviewTableView;

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
    setupReviewTableColumns();
    openReviewInRange(0, 100);
    reviewTableView.setOnMouseClicked(this::openReviewOnClick);
  }

  /**
   * Opens a page of reviews from the database according to filters.
   *
   * @param begin first element
   * @param end   last element + 1
   */
  private void openReviewInRange(int begin, int end) {
    // Clear existing data
    reviewTableView.getItems().clear();

    ObservableList<Pair<WineReview, Wine>> reviews = managerContext.getDatabaseManager()
        .getAggregatedDao().getWineReviewsAndWines(begin, end);

    // Set fetched data to the table
    reviewTableView.setItems(reviews);
  }

  /**
   * Sets up the review table columns.
   */
  public void setupReviewTableColumns() {
    // Clear any existing cols
    reviewTableView.getColumns().clear();

    // Create and config cols
    reviewTableView.setEditable(true);

    final TableColumn<Pair<WineReview, Wine>, String> titleColumn = new TableColumn<>("Wine Name");
    final TableColumn<Pair<WineReview, Wine>, String> usernameColumn = new TableColumn<>("By");
    final TableColumn<Pair<WineReview, Wine>, Double> ratingsColumn = new TableColumn<>("Score");
    final TableColumn<Pair<WineReview, Wine>, Date> dateColumn = new TableColumn<>("Date");

    titleColumn.setCellValueFactory(cellData ->
        cellData.getValue().getSecond().titleProperty());
    usernameColumn.setCellValueFactory(cellData ->
        cellData.getValue().getFirst().usernameProperty());
    ratingsColumn.setCellValueFactory(cellData ->
        cellData.getValue().getFirst().ratingProperty().asObject());
    dateColumn.setCellValueFactory(cellData ->
        cellData.getValue().getFirst().dateProperty().orElse(null));

    ratingsColumn.setCellFactory(column -> new TableCell<>() {
      private final HBox starBox = new HBox(5);

      @Override
      protected void updateItem(Double rating, boolean empty) {
        super.updateItem(rating, empty);

        if (empty || rating == null) {
          setGraphic(null);
        } else {
          starBox.getChildren().clear();
          Rating ratingStars = createRating(rating);
          starBox.getChildren().addFirst(ratingStars);
          setGraphic(starBox);
        }
      }
    });

    reviewTableView.getColumns().add(titleColumn);
    reviewTableView.getColumns().add(usernameColumn);
    reviewTableView.getColumns().add(ratingsColumn);
    reviewTableView.getColumns().add(dateColumn);
  }

  /**
   * Creates the rating for a given rating.
   *
   * @param rating rating
   * @return rating object
   */
  private Rating createRating(double rating) {
    Rating ratingStars = new Rating();
    ratingStars.setUpdateOnHover(false);
    ratingStars.setMouseTransparent(true);
    ratingStars.setOnMouseClicked(Event::consume);
    ratingStars.setOnMouseDragEntered(Event::consume);
    ratingStars.setPartialRating(true);
    ratingStars.setRating(rating);
    return ratingStars;
  }

  /**
   * Opens a review when clicked.
   *
   * @param event mouse event
   */
  @FXML
  public void openReviewOnClick(MouseEvent event) {
    if (event.getClickCount() != 2) {
      return;
    }

    Pair<WineReview, Wine> selectedReview = reviewTableView.getSelectionModel().getSelectedItem();
    if (selectedReview == null) {
      return;
    }

    String reviewerUsername = selectedReview.getFirst().getUsername();
    Wine selectedWine = selectedReview.getSecond();
    User reviewer = managerContext.getDatabaseManager().getUserDao().get(reviewerUsername);
    WineReviewsService wineReviewsService = new WineReviewsService(
        managerContext.getAuthenticationManager(),
        managerContext.getDatabaseManager(),
        selectedWine);
    managerContext
        .getGuiManager()
        .openPopupReviewView(wineReviewsService, reviewer, selectedReview.getFirst(), selectedWine);
  }

}
