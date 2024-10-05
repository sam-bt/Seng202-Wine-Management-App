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

public class SocialController extends Controller {

  @FXML
  TableView<Pair<WineReview, Wine>> reviewTableView;

  /**
   * Constructor
   *
   * @param managerContext manager context
   */
  public SocialController(ManagerContext managerContext) {
    super(managerContext);// TODO allow upvotes/downvotes/user search/review search
  }

  public void init() {
    setupReviewTableColumns();
    openReviewInRange(0, 100);
    reviewTableView.setOnMouseClicked(this::openReviewOnClick);
  }

  /**
   * Opens a page of reviews from the database according to filters
   *
   * @param begin first element
   * @param end   last element + 1
   */
  private void openReviewInRange(int begin, int end) { // TODO, add filtering
    // Clear existing data
    reviewTableView.getItems().clear();

    ObservableList<Pair<WineReview, Wine>>  reviews = managerContext.databaseManager
        .getAggregatedDao().getWineReviewsAndWines(begin, end);

    // Set fetched data to the table
    reviewTableView.setItems(reviews);
  }

  public void setupReviewTableColumns() {
    // Clear any existing cols
    reviewTableView.getColumns().clear();

    // Create and config cols
    reviewTableView.setEditable(true);

    TableColumn<Pair<WineReview, Wine>, String> titleColumn = new TableColumn<>("Wine Name");
    TableColumn<Pair<WineReview, Wine>, String> usernameColumn = new TableColumn<>("By");
    TableColumn<Pair<WineReview, Wine>, Double> ratingsColumn = new TableColumn<>("Score");
    TableColumn<Pair<WineReview, Wine>, Date> dateColumn = new TableColumn<>("Date");

    titleColumn.setCellValueFactory(cellData -> cellData.getValue().getSecond().titleProperty());
    usernameColumn.setCellValueFactory(cellData -> cellData.getValue().getFirst().usernameProperty());
    ratingsColumn.setCellValueFactory(cellData -> cellData.getValue().getFirst().ratingProperty().asObject());
    dateColumn.setCellValueFactory(cellData -> cellData.getValue().getFirst().dateProperty().orElse(null));

    ratingsColumn.setCellFactory(column -> new TableCell<>() {
      private final HBox starBox = new HBox(5);

      @Override
      protected void updateItem(Double rating, boolean empty) {
        super.updateItem(rating, empty);

        if (empty || rating == null) {
          setGraphic(null);
        } else {
          starBox.getChildren().clear();
          Rating ratingStars = setRating(rating);
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

  private Rating setRating(double rating) {
    Rating ratingStars = new Rating();
    ratingStars.setUpdateOnHover(false);
    ratingStars.setMouseTransparent(true);
    ratingStars.setOnMouseClicked(Event::consume);
    ratingStars.setOnMouseDragEntered(Event::consume);
    ratingStars.setPartialRating(true);
    ratingStars.setRating(rating);
    return ratingStars;
  }

  @FXML
  public void openReviewOnClick(
      MouseEvent event) { //TODO take to review screen (upvote/downvote reviews)
    if (event.getClickCount() != 2) {
      return;
    }

    Pair<WineReview, Wine> selectedReview = reviewTableView.getSelectionModel().getSelectedItem();
    if (selectedReview == null) {
      return;
    }

    String reviewerUsername = selectedReview.getFirst().getUsername();
    Wine selectedWine = selectedReview.getSecond();
    User reviewer = managerContext.databaseManager.getUserDao().get(reviewerUsername);
    WineReviewsService wineReviewsService = new WineReviewsService(
        managerContext.authenticationManager, managerContext.databaseManager, selectedWine);
    managerContext.GUIManager.mainController.openPopupReviewView(wineReviewsService, reviewer, selectedReview.getFirst(), selectedWine);
  }

}
