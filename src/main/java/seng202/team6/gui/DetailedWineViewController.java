package seng202.team6.gui;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.controlsfx.control.Rating;
import seng202.team6.managers.ManagerContext;
import seng202.team6.model.Wine;
import seng202.team6.model.WineReview;
import seng202.team6.util.DateFormatter;
import seng202.team6.util.ImageReader;

public class DetailedWineViewController extends Controller {
  @FXML
  private Button addReviewButton;

  @FXML
  private Button backButton;

  @FXML
  private TextField colourTextbox;

  @FXML
  private TextField countryTextbox;

  @FXML
  private TextArea descriptionTextbox;

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
  private Button viewOnMapButton;

  @FXML
  private TitledPane viewingWineTitledPane;

  @FXML
  private TextField vintageTextbox;

  @FXML
  private Pane averageReviewPane;

  @FXML
  private VBox reviewsBox;

  private final Wine viewedWine;

  private final Runnable backButtonAction;

  private static final Image RED_WINE_IMAGE = ImageReader.loadImage("/img/red_wine.png");
  private static final Image WHITE_WINE_IMAGE = ImageReader.loadImage("/img/white_wine.png");

  private static final Map<String, Image> wineImages = new HashMap<>() {{
    put("red", ImageReader.loadImage("/img/red_wine.png"));
    put("white", ImageReader.loadImage("/img/white_wine.png"));
  }};

  public DetailedWineViewController(ManagerContext managerContext, Wine viewedWine, Runnable backButtonAction) {
    super(managerContext);
    this.viewedWine = viewedWine;
    this.backButtonAction = backButtonAction;
  }

  @Override
  public void init() {
    varietyTextbox.setText(getOrDefault(viewedWine.getVariety()));
    colourTextbox.setText(getOrDefault(viewedWine.getColor()));
    countryTextbox.setText(getOrDefault(viewedWine.getCountry()));
    regionTextbox.setText(getOrDefault(viewedWine.getRegion()));
    vintageTextbox.setText(viewedWine.getVintage() <= 0 ? "N/A" : Integer.toString(viewedWine.getVintage()));
    priceTextbox.setText(viewedWine.getPrice() <= 0 ? "N/A" : "%f.2".formatted(viewedWine.getPrice()));
    viewingWineTitledPane.setText("Viewing Wine: " + viewedWine.getTitle());

    // todo - add a default image
    Image wineImage = wineImages.get(colourTextbox.getText().toLowerCase());
    if (wineImage != null)
      imageView.setImage(wineImage);

    // create the rating control and disable it being edited by consuming mouse events
    Rating rating = new Rating();
    rating.setUpdateOnHover(false);
    rating.setMouseTransparent(true);
    rating.setOnMouseClicked(Event::consume);
    rating.setOnMouseDragEntered(Event::consume);
    averageReviewPane.getChildren().add(rating);
  }

  @FXML
  void onBackButtonClick() {
    backButtonAction.run();
  }

  private void addReview(WineReview wineReview) {
    String formattedDate = DateFormatter.DATE_FORMAT.format(wineReview.getDate());
    VBox wrapper = new VBox();
    wrapper.setMaxWidth(reviewsBox.getMaxWidth());
    wrapper.setMaxHeight(Double.MAX_VALUE);
    wrapper.setPadding(new Insets(10));
    wrapper.setStyle("-fx-border-width: 1; "
        + "-fx-border-color: black; "
        + "-fx-border-insets: 10;");

    Rating rating = new Rating();
    rating.setRating(wineReview.getRating()); 

    Label reviewCaptionLabel = new Label("From " + wineReview.getUsername() + " on " + formattedDate);
    reviewCaptionLabel.setMaxWidth(wrapper.getMaxWidth());
    reviewCaptionLabel.setWrapText(true);

    Label descriptionLabel = new Label(wineReview.getDescription());
    descriptionLabel.setMaxWidth(wrapper.getMaxWidth());
    descriptionLabel.setWrapText(true);
    descriptionLabel.setStyle("-fx-padding: 10 0 0 0;");

    wrapper.getChildren().addAll(rating, reviewCaptionLabel, descriptionLabel);
    reviewsBox.getChildren().add(wrapper);
  }

  private String getOrDefault(String property) {
    return (property == null || property.isEmpty()) ? "N/A" : property;
  }
}
