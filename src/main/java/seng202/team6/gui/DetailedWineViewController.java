package seng202.team6.gui;

import java.util.HashMap;
import java.util.Map;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.controlsfx.control.Rating;
import seng202.team6.managers.ManagerContext;
import seng202.team6.model.Note;
import seng202.team6.model.User;
import seng202.team6.model.Wine;
import seng202.team6.model.WineReview;
import seng202.team6.service.WineReviewsService;
import seng202.team6.util.DateFormatter;
import seng202.team6.util.ImageReader;

public class DetailedWineViewController extends Controller {

  @FXML
  private Button saveNotes;
  @FXML
  private Label noteLabel;

  @FXML
  private Button addReviewButton;
  @FXML
  private Button backButton;
  @FXML
  private TextField colourTextbox;
  @FXML
  private TextField countryTextbox;
  @FXML
  private TextArea descriptionArea;
  @FXML
  private ImageView imageView;
  @FXML
  private TextArea notesTextbox;
  @FXML
  private TextField priceTextbox;
  @FXML
  private TextField regionTextbox;
  @FXML
  private TextField varietyTextbox;
  @FXML
  private TitledPane viewingWineTitledPane;
  @FXML
  private TextField vintageTextbox;
  @FXML
  private HBox ratingsContainer;
  @FXML
  private VBox reviewsBox;
  @FXML
  private Label ratingsLabel;
  @FXML
  private Label loginToReviewLabel;
  private Rating ratingStars;

  private static final Image RED_WINE_IMAGE = ImageReader.loadImage("/img/red_wine.png");
  private static final Image WHITE_WINE_IMAGE = ImageReader.loadImage("/img/white_wine.png");
  private static final Image ROSE_WINE_IMAGE = ImageReader.loadImage("/img/rose_wine.png");
  private static final Image DEFAULT_WINE_IMAGE = ImageReader.loadImage("/img/default_wine.png");
  private static final Map<String, Image> wineImages = new HashMap<>() {{
    put("red", RED_WINE_IMAGE);
    put("white", WHITE_WINE_IMAGE);
    put("rose", ROSE_WINE_IMAGE);
    put("rosé", ROSE_WINE_IMAGE);
  }};
  private final WineReviewsService wineReviewsService;
  private final Wine viewedWine;
  private final Runnable backButtonAction;
  private final ObservableMap<WineReview, VBox> wineReviewWrappers = FXCollections.observableHashMap();

  public DetailedWineViewController(ManagerContext managerContext, Wine viewedWine,
      Runnable backButtonAction) {
    super(managerContext);
    this.wineReviewsService = new WineReviewsService(managerContext.authenticationManager,
        managerContext.databaseManager, viewedWine);
    this.viewedWine = viewedWine;
    this.backButtonAction = backButtonAction;
    this.ratingStars = new Rating();
    bindToWineReviewsService();
  }

  @Override
  public void init() {
    varietyTextbox.setText(getOrDefault(viewedWine.getVariety()));
    colourTextbox.setText(getOrDefault(viewedWine.getColor()));
    countryTextbox.setText(getOrDefault(viewedWine.getCountry()));
    regionTextbox.setText(getOrDefault(viewedWine.getRegion()));
    vintageTextbox.setText(
        viewedWine.getVintage() <= 0 ? "N/A" : Integer.toString(viewedWine.getVintage()));
    priceTextbox.setText(
        viewedWine.getPrice() <= 0 ? "N/A" : "%f.2".formatted(viewedWine.getPrice()));
    viewingWineTitledPane.setText("Viewing Wine: " + viewedWine.getTitle());

    descriptionArea.setText(getOrDefault(viewedWine.getDescription()));
    if (managerContext.authenticationManager.isAuthenticated()) {
      setNotesVisible(true);
      User user = managerContext.authenticationManager.getAuthenticatedUser();
      Note note = managerContext.databaseManager.getWineNotesDAO().get(user, viewedWine);
      notesTextbox.setText(note == null ? "" : note.getNote());
    } else {
      setNotesVisible(false);
    }

    Image wineImage = wineImages.getOrDefault(colourTextbox.getText().toLowerCase(),
        DEFAULT_WINE_IMAGE);
    imageView.setImage(wineImage);

    // create the rating control and disable it being edited by consuming mouse events
    ratingStars.setUpdateOnHover(false);
    ratingStars.setMouseTransparent(true);
    ratingStars.setOnMouseClicked(Event::consume);
    ratingStars.setOnMouseDragEntered(Event::consume);
    ratingStars.setPartialRating(true);
    ratingsContainer.getChildren().addFirst(ratingStars);

    if (!managerContext.authenticationManager.isAuthenticated()) {
      addReviewButton.setDisable(true);
      addReviewButton.setVisible(false);
      loginToReviewLabel.setVisible(true);
      loginToReviewLabel.setDisable(false);
    }

    // everything is ready so now the wine reviews can be loaded
    wineReviewsService.init();
  }

  private void bindToWineReviewsService() {
    ObservableList<WineReview> wineReviews = wineReviewsService.getWineReviews();
    wineReviews.addListener((ListChangeListener<WineReview>) change -> {
      while (change.next()) {
        if (change.wasAdded())
          change.getAddedSubList().forEach(wineReview -> {
            VBox reviewWrapper = createWineReviewElement(wineReview);
            wineReviewWrappers.put(wineReview, reviewWrapper);
            reviewsBox.getChildren().add(reviewWrapper);
          });
        if (change.wasRemoved())
          change.getRemoved().forEach(wineReview -> {
            VBox reviewWrapper = wineReviewWrappers.get(wineReview);
            reviewsBox.getChildren().remove(reviewWrapper);
            wineReviewWrappers.remove(wineReview);
          });
      }
    });
    wineReviewsService.usersReviewProperty().addListener((observableValue, oldValue, usersReview) -> {
      addReviewButton.setText((usersReview == null ? "Add" : "Modify") + " Review");
    });
    ratingStars.ratingProperty().bind(wineReviewsService.averageRatingProperty());
    ratingStars.ratingProperty().addListener((observableValue, oldValue, newAverageRating) -> {
      if (wineReviewsService.hasReviews()) {
        int numberOfRatings = wineReviewsService.getWineReviews().size();
        ratingsLabel.setText("Average %.2f From %d ratings".formatted(newAverageRating.doubleValue(), numberOfRatings));
      } else {
        ratingsLabel.setText("This wine has not been reviewed");
      }
    });
  }

  @FXML
  void onBackButtonClick() {
    backButtonAction.run();
  }

  @FXML
  void onAddReviewButtonClick() {
    managerContext.GUIManager.mainController.openPopupWineReview(wineReviewsService);
  }

  @FXML
  void onOpenListsButtonClick() {
    managerContext.GUIManager.mainController.openAddToListPopup(viewedWine);
  }

  private VBox createWineReviewElement(WineReview wineReview) {
    String formattedDate = DateFormatter.DATE_FORMAT.format(wineReview.getDate());
    VBox wrapper = new VBox();
    wrapper.setMaxWidth(reviewsBox.getMaxWidth());
    wrapper.setMaxHeight(Double.MAX_VALUE);
    wrapper.setPadding(new Insets(10));
    wrapper.setStyle("-fx-border-width: 1; "
        + "-fx-border-color: black; "
        + "-fx-border-insets: 10;");

    Rating rating = new Rating();
    rating.ratingProperty().bind(wineReview.ratingProperty());
    rating.setUpdateOnHover(false);
    rating.setMouseTransparent(true);
    rating.setOnMouseClicked(Event::consume);
    rating.setOnMouseDragEntered(Event::consume);

    Label reviewCaptionLabel = new Label(
        "From " + wineReview.getUsername() + " on " + formattedDate);
    reviewCaptionLabel.textProperty().bind(Bindings.createStringBinding(
        () ->
            "From " + wineReview.getUsername() + " on " + DateFormatter.DATE_FORMAT.format(wineReview.getDate()),
        wineReview.dateProperty()
    ));
    reviewCaptionLabel.setMaxWidth(wrapper.getMaxWidth());
    reviewCaptionLabel.setWrapText(true);

    Label descriptionLabel = new Label(wineReview.getDescription());
    descriptionLabel.textProperty().bind(wineReview.descriptionProperty());
    descriptionLabel.setMaxWidth(wrapper.getMaxWidth());
    descriptionLabel.setWrapText(true);
    descriptionLabel.setStyle("-fx-padding: 10 0 0 0;");

    wrapper.getChildren().addAll(rating, reviewCaptionLabel, descriptionLabel);
    return wrapper;
  }

  /**
   * Sets the visibilty of note-related elements based on the visibile parameter. Also changes
   * the text of the label, assuming that notes are only hidden when the user is not signed in.
   * @param visible Whether or not the elements should be visible
   */
  private void setNotesVisible(boolean visible) {
    if (!visible) {
      noteLabel.setText("Sign in to save notes");
    } else {
      noteLabel.setText("My Notes");
    }
    notesTextbox.setVisible(visible);
    saveNotes.setVisible(visible);
  }

  /**
   * Saves the note when clicked
   */
  @FXML
  public void onSaveClicked() {
    // todo - save this using beans
//    managerContext.databaseManager.saveNote(viewedWine.getKey(),
//        managerContext.authenticationManager.getAuthenticatedUsername(), notesTextbox.getText());
  }

  private String getOrDefault(String property) {
    return (property == null || property.isEmpty()) ? "N/A" : property;
  }
}
