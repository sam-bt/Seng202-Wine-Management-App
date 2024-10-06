package seng202.team6.gui.popup;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import seng202.team6.gui.Controller;
import seng202.team6.gui.controls.container.AddRemoveCardsContainer;
import seng202.team6.managers.ManagerContext;
import seng202.team6.model.Wine;
import seng202.team6.model.WineList;
import seng202.team6.service.WineListService;

/**
 * Controller for the adding to list popup.
 */
public class AddToListPopupController extends Controller {

  @FXML
  ScrollPane wineListsContainer;

  private final WineListService wineListService;
  private final ObservableMap<WineList, VBox> wineListWrappers = FXCollections.observableHashMap();
  private final Wine wine;
  private AddRemoveCardsContainer<WineList> addRemoveCardsContainer;

  /**
   * Constructor.
   *
   * @param context context
   * @param wine wine
   */
  public AddToListPopupController(ManagerContext context, Wine wine) {
    super(context);
    this.wine = wine;
    this.wineListService = new WineListService(managerContext.getAuthenticationManager(),
        context.getDatabaseManager());
    bindToWineListService();
  }

  /**
   * Initializes the controller.
   */
  @Override
  public void init() {
    addRemoveCardsContainer = new AddRemoveCardsContainer<>(
        wineListsContainer.viewportBoundsProperty(),
        wineListsContainer.widthProperty());
    wineListsContainer.setContent(addRemoveCardsContainer);
    wineListService.init();
  }

  @FXML
  void onBackButtonClick() {
    managerContext.getGuiManager().mainController.closePopup();
  }

<<<<<<< HEAD
=======
  private void onAddButtonClick(WineList wineList, Button button) {
    updateWineListButton(button, wineList, true);
    managerContext.getDatabaseManager().getWineListDao().addWine(wineList, wine);
  }

  private void onRemoveButtonClick(WineList wineList, Button button) {
    updateWineListButton(button, wineList, false);
    managerContext.getDatabaseManager().getWineListDao().removeWine(wineList, wine);
  }

>>>>>>> Development
  private void bindToWineListService() {
    wineListService.getWineLists().addListener((ListChangeListener<WineList>) change -> {
      while (change.next()) {
        if (change.wasAdded()) {
          change.getAddedSubList().forEach(wineList -> {
            addRemoveCardsContainer.add(wineList, new SimpleStringProperty(wineList.name()),
                !wineListService.isWineInList(wineList, wine),
                () ->  managerContext.databaseManager.getWineListDAO().addWine(wineList, wine),
                () -> managerContext.databaseManager.getWineListDAO().removeWine(wineList, wine));
          });
        }
      }
    });
  }
<<<<<<< HEAD
=======

  private void createWineListElement(WineList wineList) {
    final boolean listContainsWine = wineListService.isWineInList(wineList, wine);
    final GridPane wrapper = new GridPane();
    final RowConstraints firstRow = new RowConstraints();
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
    button.setOnMouseClicked(listContainsWine
        ?
        (event) -> onRemoveButtonClick(wineList, button) :
        (event) -> onAddButtonClick(wineList, button));
  }
>>>>>>> Development
}
