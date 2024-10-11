package seng202.team6.gui;

import java.util.Set;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.controlsfx.control.Rating;
import seng202.team6.gui.controls.AutoCompletionTextField;
import seng202.team6.gui.controls.CircularScoreIndicator;
import seng202.team6.gui.controls.UnmodifiableRating;
import seng202.team6.gui.controls.cardcontent.WineCardContent;
import seng202.team6.managers.ManagerContext;
import seng202.team6.model.Wine;

public class WineCompareController extends Controller {

  @FXML
  private VBox leftWineContainer;

  @FXML
  private VBox rightWineContainer;

  private WineSide leftSide;
  private WineSide rightSide;

  @FXML
  private AutoCompletionTextField leftWineSearch;
  private AutoCompletionTextField rightWineSearch;

  private Wine leftWine;
  private Wine rightWine;

  /**
   * Constructs a new WineCompareController.
   *
   * @param context the manager context
   */
  public WineCompareController(ManagerContext context, Wine wine) {
    super(context);
    this.leftWine = wine;
  }

  @Override
  public void init() {
    leftSide = new WineSide(leftWineContainer);
    rightSide = new WineSide(rightWineContainer);
  }

  class WineSide {

    private final VBox container;
    private GridPane header;
    private VBox description;
    private GridPane attributesGrid;
    private VBox buttons;
    private AutoCompletionTextField searchTextField;
    private Wine wine;

    WineSide(VBox container) {
      this.container = container;
      container.setPadding(new Insets(10));
      container.setStyle("-fx-background-color: #f3f4f6; -fx-background-radius: 10px;");
      setup();
    }

    private void setup() {
      Set<String> uniqueTitles = managerContext.getDatabaseManager().getWineDataStatService()
          .getUniqueTitles();
      searchTextField = new AutoCompletionTextField();
      searchTextField.setPrefWidth(300);
      searchTextField.getEntries().addAll(uniqueTitles);
      searchTextField.setOnSelectionAction(match -> {
        Wine wine = managerContext.getDatabaseManager().getWineDao().getByExactTitle(match);
        if (wine != null) {
          setWine(wine);
        }
      });

      Label searchLabel = new Label("Search for a Wine");
      searchLabel.setFont(Font.font(18));

      Separator separator = new Separator();
      HBox wrapper = new HBox(searchLabel, searchTextField);
      wrapper.setAlignment(Pos.CENTER);
      wrapper.setSpacing(10);
      container.getChildren().addAll(wrapper, separator);
    }

    private void setWine(Wine wine) {
      // clear the previous components then we add them with new wine
      if (this.wine != null) {
        container.getChildren().remove(header);
        container.getChildren().remove(description);
        container.getChildren().remove(attributesGrid);
        container.getChildren().remove(buttons);
      }
      this.wine = wine;
      createHeader();
      createDescription();
      createAttributes();
      createButtons();
    }

    private void createHeader() {
      header = new GridPane();
      header.setOpaqueInsets(new Insets(0, 10, 0, 10));

      ColumnConstraints firstColumn = new ColumnConstraints();
      ColumnConstraints secondColumn = new ColumnConstraints();
      ColumnConstraints thirdColumn = new ColumnConstraints();
      firstColumn.setPrefWidth(150);
      thirdColumn.setPrefWidth(150);
      header.getRowConstraints().add(new RowConstraints());
      header.getColumnConstraints().addAll(firstColumn, secondColumn, thirdColumn);

      Image wineImage = WineCardContent.WINE_IMAGES.getOrDefault(wine.getColor().toLowerCase(),
          WineCardContent.DEFAULT_WINE_IMAGE);
      ImageView wineImageView = new ImageView(wineImage);
      wineImageView.setFitHeight(150);
      wineImageView.setFitWidth(150);
      wineImageView.setPreserveRatio(true);

      BorderPane wineImageWrapper = new BorderPane();
      wineImageWrapper.setPrefHeight(150);
      wineImageWrapper.setPrefWidth(150);
      wineImageWrapper.setCenter(wineImageView);
      header.add(wineImageWrapper, 0, 0);

      Label titleLabel = new Label();
      titleLabel.textProperty().bind(wine.titleProperty());
      titleLabel.setFont(Font.font(18));
      titleLabel.setTextAlignment(TextAlignment.CENTER);
      titleLabel.setWrapText(true);

      Rating rating = new UnmodifiableRating();
      rating.ratingProperty().bind(wine.averageRatingProperty());

      VBox titleRatingBox = new VBox(titleLabel, rating);
      titleRatingBox.setAlignment(Pos.CENTER);
      header.add(titleRatingBox, 1, 0);

      CircularScoreIndicator scoreIndicator = new CircularScoreIndicator();
      scoreIndicator.scoreProperty().set(wine.getScorePercent());
      scoreIndicator.setMaxSize(100, 100);
      scoreIndicator.setMinSize(100, 100);
      scoreIndicator.setPrefSize(100, 100);
      header.add(scoreIndicator, 2, 0);
      container.getChildren().add(header);

      GridPane.setHgrow(titleRatingBox, Priority.ALWAYS);
      GridPane.setHalignment(wineImageWrapper, HPos.CENTER);
      GridPane.setValignment(wineImageWrapper, VPos.CENTER);
      GridPane.setHalignment(titleRatingBox, HPos.CENTER);
      GridPane.setValignment(titleRatingBox, VPos.CENTER);
      GridPane.setHalignment(scoreIndicator, HPos.CENTER);
      GridPane.setValignment(scoreIndicator, VPos.CENTER);
    }

    private void createDescription() {
      Label label = new Label("Description");
      TextArea textArea = new TextArea();
      textArea.setText((wine.getDescription() == null || wine.getDescription().isEmpty()) ? "N/A"
          : wine.getDescription());
      textArea.setPrefHeight(100);
      textArea.setEditable(false);
      textArea.setWrapText(true);

      description = new VBox(label, textArea);
      description.setOpaqueInsets(new Insets(0, 10, 0, 10));
      container.getChildren().add(description);
    }

    private void createAttributes() {
      attributesGrid = new GridPane();
      attributesGrid.setOpaqueInsets(new Insets(0, 10, 0, 10));
      attributesGrid.setHgap(10);
      attributesGrid.setVgap(10);
      attributesGrid.setMaxWidth(Double.MAX_VALUE);
      attributesGrid.getRowConstraints().addAll(new RowConstraints(),
          new RowConstraints(), new RowConstraints());

      ColumnConstraints labelColumn1 = new ColumnConstraints();
      ColumnConstraints valueColumn1 = new ColumnConstraints();
      labelColumn1.setPercentWidth(15);
      valueColumn1.setPercentWidth(32);
      valueColumn1.setHgrow(Priority.ALWAYS);

      ColumnConstraints spacingColumn = new ColumnConstraints();
      spacingColumn.setPercentWidth(6);

      ColumnConstraints labelColumn2 = new ColumnConstraints();
      ColumnConstraints valueColumn2 = new ColumnConstraints();
      labelColumn2.setPercentWidth(15);
      valueColumn2.setPercentWidth(32);
      valueColumn2.setHgrow(Priority.ALWAYS);

      attributesGrid.getColumnConstraints().addAll(labelColumn1, valueColumn1, spacingColumn,
          labelColumn2, valueColumn2);

      // Adjust column indices to account for the new spacing column
      addAttributeToGrid("Variety", wine.getVariety(), 0, 0);
      addAttributeToGrid("Winery", wine.getWinery(), 3, 0);
      addAttributeToGrid("Country", wine.getCountry(), 0, 1);
      addAttributeToGrid("Region", wine.getRegion(), 3, 1);
      addAttributeToGrid("Vintage", wine.getVintage() > 0 ? "%d".formatted(wine.getVintage())
          : "N/A", 0, 2);
      addAttributeToGrid("Colour", wine.getColor(), 3, 2);
      addAttributeToGrid("Abv", wine.getAbv() > 0 ? "%.2f%"
          : "N/A", 0, 3);
      addAttributeToGrid("Price", wine.getPrice() > 0 ? "$%.2f"
          : "N/A", 3, 3);

      container.getChildren().add(attributesGrid);
    }

    private void createButtons() {
      // space so buttons will be at the bottom
      HBox buttonsWrapper = new HBox();
      buttonsWrapper.setSpacing(20);
      buttonsWrapper.setAlignment(Pos.CENTER);

      Button detailedViewButton = new Button("Open Detailed View");
      detailedViewButton.setPrefWidth(200);
      detailedViewButton.getStyleClass().add("secondary-button");
      detailedViewButton.setOnMouseClicked((event) ->
          managerContext.getGuiManager().mainController.openDetailedWineView(wine,
              () -> managerContext.getGuiManager().mainController.openWineCompareScreen(wine)));
      buttonsWrapper.getChildren().add(detailedViewButton);

      // only add the open lists button is they are loggied in
      if (managerContext.getAuthenticationManager().isAuthenticated()) {
        Button openListsButton = new Button("Open Lists");
        openListsButton.setPrefWidth(200);
        openListsButton.getStyleClass().add("secondary-button");
        openListsButton.setOnMouseClicked((event) ->
            managerContext.getGuiManager().mainController.openAddToListPopup(wine));
        buttonsWrapper.getChildren().add(openListsButton);
      }

      buttons = new VBox(buttonsWrapper);
      buttons.setAlignment(Pos.BOTTOM_CENTER);
      VBox.setVgrow(buttons, Priority.ALWAYS);
      container.getChildren().add(buttons);
    }

    private void addAttributeToGrid(String text, String value, int column, int row) {
      Label label = new Label();
      label.setText(text);
      label.setMaxWidth(Double.MAX_VALUE);
      attributesGrid.add(label, column, row);

      TextField textField = new TextField();
      textField.setText((value == null || value.isEmpty()) ? "N/A" :  value);
      textField.setMaxWidth(Double.MAX_VALUE);
      textField.setEditable(false);
      attributesGrid.add(textField, column + 1, row);
    }
  }
}
