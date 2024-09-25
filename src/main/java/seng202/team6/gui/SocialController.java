package seng202.team6.gui;

import java.util.Date;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import org.controlsfx.control.Rating;
import seng202.team6.managers.ManagerContext;
import seng202.team6.model.Wine;
import seng202.team6.model.WineReview;
import seng202.team6.service.AuthenticationService;
import seng202.team6.util.ImageReader;

public class SocialController extends Controller{

  @FXML
  TableView<Wine> wineTableView;

  @FXML
  TableView<WineReview> reviewTableView;

  private final AuthenticationService authenticationService;

  /**
   * Constructor
   *
   * @param managerContext manager context
   */
  public SocialController(ManagerContext managerContext, AuthenticationService authenticationService) {
    super(managerContext);
    this.authenticationService = authenticationService; // TODO allow upvotes/downvotes/user search/review search
  }
  public void init() {
    setupWineTableColumns();
    openWineRange(0, 100);

    setupReviewTableColumns();
    openReviewInRange(0, 100);

    reviewTableView.setOnMouseClicked(this::openReviewOnClick);
    wineTableView.setOnMouseClicked(this::openReviewOnClick);
  }

  /**
   * Opens a page of wines from the database according to filters
   *
   * @param begin   first element
   * @param end     last element + 1
   */
  private void openWineRange(int begin, int end) { // TODO, add filtering by num reviews/rating
    // Clear existing data
    wineTableView.getItems().clear();

    ObservableList<Wine> wines;

    wines = managerContext.databaseManager.getWinesInRangeWithReviewInfo(begin, end);
    System.out.println(wines);
    // Set fetched data to the table
    wineTableView.setItems(wines);

    }

  /**
   * Opens a page of reviews from the database according to filters
   *
   * @param begin   first element
   * @param end     last element + 1
   */
  private void openReviewInRange(int begin, int end) { // TODO, add filtering
    // Clear existing data
    reviewTableView.getItems().clear();

    ObservableList<WineReview> reviews;

    reviews = managerContext.databaseManager.getReviewsInRange(begin, end);

    // Set fetched data to the table
    reviewTableView.setItems(reviews);

  }


  public void setupWineTableColumns() {
    // Clear any existing cols
    wineTableView.getColumns().clear();

    // Create and config cols
    wineTableView.setEditable(true);

    TableColumn<Wine, String> titleColumn = new TableColumn<>("Title");
    TableColumn<Wine, String> regionColumn = new TableColumn<>("Region");
    TableColumn<Wine, String> colorColumn = new TableColumn<>("Color");
    TableColumn<Wine, Integer> scoreColumn = new TableColumn<>("Critic Score");
    TableColumn<Wine, Integer> reviewsColumn = new TableColumn<>("Reviews");
    TableColumn<Wine, Double> ratingsColumn = new TableColumn<>("Average User Rating");

    titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
    regionColumn.setCellValueFactory(new PropertyValueFactory<>("region"));
    colorColumn.setCellValueFactory(new PropertyValueFactory<>("color"));
    scoreColumn.setCellValueFactory(new PropertyValueFactory<>("scorePercent"));
    reviewsColumn.setCellValueFactory(cellData -> cellData.getValue().getReviewCount().asObject());
    ratingsColumn.setCellValueFactory(cellData -> cellData.getValue().getRating().asObject());
    ratingsColumn.setCellFactory(column -> new TableCell<Wine, Double>() {
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

    wineTableView.getColumns().add(titleColumn);
    wineTableView.getColumns().add(regionColumn);
    wineTableView.getColumns().add(colorColumn);
    wineTableView.getColumns().add(scoreColumn);
    wineTableView.getColumns().add(reviewsColumn);
    wineTableView.getColumns().add(ratingsColumn);

  }

  public void setupReviewTableColumns() {
    // Clear any existing cols
    reviewTableView.getColumns().clear();

    // Create and config cols
    reviewTableView.setEditable(true);

    TableColumn<WineReview, String> titleColumn = new TableColumn<>("Wine Name");
    TableColumn<WineReview, String> usernameColumn = new TableColumn<>("By");
    TableColumn<WineReview, Double> ratingsColumn = new TableColumn<>("Score");
    TableColumn<WineReview, Date> dateColumn = new TableColumn<>("Date");

    titleColumn.setCellValueFactory(cellData -> cellData.getValue().getWineName());
    usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
    ratingsColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));
    dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

    ratingsColumn.setCellFactory(column -> new TableCell<WineReview, Double>() {
      private final HBox starBox = new HBox(5);

      @Override
      protected void updateItem(Double rating, boolean empty) {
        super.updateItem(rating, empty);

        if (empty || rating == null) {
          setGraphic(null);
        } else {
          starBox.getChildren().clear();
          System.out.println("Adding stars for rating: " + rating);
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
  public void openReviewOnClick(MouseEvent event) { //TODO take to review screen (upvote/downvote reviews)
    if (event.getClickCount() != 2)
      return;

    Wine selectedWine = wineTableView.getSelectionModel().getSelectedItem();
    if (selectedWine == null)
      return;

    Runnable backAction = () -> managerContext.GUIManager.mainController.openSocialScreen();
    managerContext.GUIManager.mainController.openDetailedWineView(selectedWine, backAction);
  }

}
