package seng202.team6.gui.popup;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import seng202.team6.gui.Controller;
import seng202.team6.managers.ManagerContext;
import seng202.team6.model.Wine;
import seng202.team6.model.WineList;
import seng202.team6.service.WineListService;
import seng202.team6.util.IconPaths;

public class AddToListPopupController extends Controller {

  private final WineListService wineListService;
  private final ObservableMap<WineList, VBox> wineListWrappers = FXCollections.observableHashMap();
  private final Wine wine;
  @FXML
  private VBox listsContainer;

  public AddToListPopupController(ManagerContext context, Wine wine) {
    super(context);
    this.wine = wine;
    this.wineListService = new WineListService(context.authenticationManager,
        context.databaseManager);
    bindToWineListService();
  }

  @Override
  public void init() {
    wineListService.init();
  }

  @FXML
  void onBackButtonClick() {
    managerContext.GUIManager.mainController.closePopup();
  }

  private void onAddButtonClick(WineList wineList, Button button) {
    updateWineListButton(button, wineList, true);
    managerContext.databaseManager.addWineToList(wineList, wine);
  }

  private void onRemoveButtonClick(WineList wineList, Button button) {
    updateWineListButton(button, wineList, false);
    managerContext.databaseManager.deleteWineFromList(wineList, wine);
  }

  private void bindToWineListService() {
    wineListService.getWineLists().addListener((ListChangeListener<WineList>) change -> {
      while (change.next()) {
        if (change.wasAdded()) {
          change.getAddedSubList().forEach(this::createWineListElement);
        }
      }
    });
  }

  private void createWineListElement(WineList wineList) {
    boolean listContainsWine = wineListService.isWineInList(wineList, wine);
    GridPane wrapper = new GridPane();
    RowConstraints firstRow = new RowConstraints();
    ColumnConstraints firstColumn = new ColumnConstraints();
    ColumnConstraints secondColumn = new ColumnConstraints();
    firstColumn.setPercentWidth(80);
    secondColumn.setPercentWidth(20);
    firstColumn.setHgrow(Priority.NEVER);
    secondColumn.setHgrow(Priority.NEVER);
    wrapper.setPrefWidth(listsContainer.getPrefWidth());
    wrapper.setMaxWidth(listsContainer.getMaxWidth());
    wrapper.getRowConstraints().add(firstRow);
    wrapper.getColumnConstraints().addAll(firstColumn, secondColumn);
    wrapper.setAlignment(Pos.CENTER);
    wrapper.getStylesheets().add("css/global.css");
    wrapper.getStyleClass().add("secondary-background");

    Label listNameLabel = new Label(wineList.name());
    listNameLabel.setPadding(new Insets(10, 20, 10, 20));
    listNameLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");
    listNameLabel.setWrapText(true);
    wrapper.add(listNameLabel, 0, 0);

    Button button = new Button();
    button.setPrefSize(28, 32);
    button.setMaxSize(28, 32);
    button.setMinSize(28, 32);
    // remove default button style
    button.getStylesheets().add("css/add_remove_buttons.css");
    wrapper.add(button, 1, 0);
    GridPane.setHalignment(button, HPos.CENTER);
    updateWineListButton(button, wineList, listContainsWine);

    listsContainer.getChildren().add(wrapper);
  }

  private void updateWineListButton(Button button, WineList wineList, boolean listContainsWine) {
    SVGPath svgPath = new SVGPath();
    svgPath.getStyleClass().add("icon");
    svgPath.setContent(listContainsWine ? IconPaths.REMOVE_PATH : IconPaths.ADD_PATH);
    svgPath.setScaleX(0.05);
    svgPath.setScaleY(0.05);
    button.setGraphic(svgPath);
    button.setOnMouseClicked(listContainsWine ?
        (event) -> onRemoveButtonClick(wineList, button) :
        (event) -> onAddButtonClick(wineList, button));
  }
}
