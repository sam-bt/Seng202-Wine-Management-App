package seng202.team6.gui.popup;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import org.controlsfx.control.Rating;
import seng202.team6.gui.Controller;
import seng202.team6.gui.controls.UnmodifiableRating;
import seng202.team6.managers.ManagerContext;
import seng202.team6.model.User;
import seng202.team6.model.Wine;
import seng202.team6.model.WineReview;
import seng202.team6.service.SocialService;
import seng202.team6.service.WineReviewsService;
import seng202.team6.util.DateFormatter;

public class UserViewPopupController extends Controller {

  @FXML
  private TitledPane userTitlePane;
  @FXML
  private FlowPane reviewsBox;
  private final SocialService socialService;
  private final User user;
  private final ObservableMap<WineReview, VBox> wineReviewCards = FXCollections.observableHashMap();

  public UserViewPopupController(ManagerContext context, User user) {
    super(context);
    this.socialService = new SocialService(managerContext.getAuthenticationManager(),
        managerContext.getDatabaseManager(), user);
    this.user = user;
    bindToSocialService();
  }

  @Override
  public void init() {
    userTitlePane.setText("Viewing " + user.getUsername() + "'s Reviews");

    reviewsBox.setHgap(10);
    reviewsBox.setVgap(10);
    reviewsBox.setPrefWrapLength(600);
    reviewsBox.setAlignment(Pos.CENTER_LEFT);

    socialService.init();
  }


  /**
   * Binds the social service to the UI. The bindings ensure changes to the reviews are
   * reflected in the UI. The listeners will graphically display or remove reviews upon change in
   * the social service list.
   */
  private void bindToSocialService() {
    ObservableList<WineReview> wineReviews = socialService.getUserReviews();
    wineReviews.addListener((ListChangeListener<WineReview>) change -> {
      while (change.next()) {
        if (change.wasAdded()) {
          change.getAddedSubList().forEach(wineReview -> {
            VBox reviewWrapper = createWineReviewElement(wineReview);
            wineReviewCards.put(wineReview, reviewWrapper);
            reviewsBox.getChildren().add(reviewWrapper);
          });
        }
        if (change.wasRemoved()) {
          change.getRemoved().forEach(wineReview -> {
            VBox reviewWrapper = wineReviewCards.get(wineReview);
            reviewsBox.getChildren().remove(reviewWrapper);
            wineReviewCards.remove(wineReview);
          });
        }
      }
    });
  }

  /**
   * Creates a new VBox layout element which represents a single wine. Attributes in the wine review
   * are bind to the UI elements to ensure any changes are reflected in the UI.
   *
   * @param wineReview The wine review object representing the review
   * @return A VBox containing the wine review UI
   */
  private VBox createWineReviewElement(WineReview wineReview) { //TODO maybe factor out as duplicate
    VBox wrapper = new VBox();
    wrapper.setMaxWidth(reviewsBox.getMaxWidth());
    wrapper.setMinWidth(320);
    wrapper.setMaxHeight(Double.MAX_VALUE);
    wrapper.setPadding(new Insets(10));
    wrapper.setStyle("-fx-border-width: 1; "
        + "-fx-border-color: black; "
        + "-fx-border-insets: 10;");

    Rating rating = new UnmodifiableRating();

    Wine wine = managerContext.getDatabaseManager().getWineDao().get(wineReview.getWineId());
    Label titleLabel = new Label(wine.getTitle());
    titleLabel.textProperty().bind(wine.titleProperty());
    titleLabel.setMaxWidth(wrapper.getMaxWidth());
    titleLabel.setWrapText(true);
    titleLabel.setStyle("-fx-padding: 10 0 0 0; -fx-font-size: 14px; -fx-font-weight: bold;");

    VBox.setMargin(titleLabel, new Insets(0, 0, 5, 0));

    rating.setRating(wineReview.getRating());

    wrapper.setOnMouseClicked(event -> {
      if (event.getClickCount() == 2) {
        openReviewView(wineReview, wine);
      }
    });

    wrapper.getChildren().addAll(titleLabel, rating);
    return wrapper;
  }

  @FXML
  void onBackButtonClick() {
    managerContext.getGuiManager().mainController.closePopup();
  }

  private void openReviewView(WineReview review, Wine wine) {
    managerContext.getGuiManager().mainController.closePopup();
    User user = managerContext.getDatabaseManager().getUserDao().get(review.getUsername());
    managerContext.getGuiManager().mainController.openPopupReviewView(new WineReviewsService(managerContext.getAuthenticationManager(), managerContext.getDatabaseManager(), wine), user, review, wine);
  }

}
