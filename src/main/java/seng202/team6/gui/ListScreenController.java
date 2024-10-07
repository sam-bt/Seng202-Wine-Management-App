package seng202.team6.gui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import seng202.team6.managers.ManagerContext;
import seng202.team6.model.User;
import seng202.team6.model.Wine;
import seng202.team6.model.WineList;
import seng202.team6.model.WineReview;
import seng202.team6.service.WineListService;

/**
 * Controller to display the user defined lists of wines
 */
public class ListScreenController extends Controller {

  private final WineListService wineListService;
  @FXML
  public Button createListRequestButton;
  @FXML
  public TabPane listScreenTabs;
  @FXML
  public Tab tabViewing;
  @FXML
  public TextField listName;
  @FXML
  public Label errorText;
  @FXML
  public VBox buttonList;
  @FXML
  public Button deleteListRequestButton;
  @FXML
  public TableView<Wine> tableView;
  @FXML
  private Tab tabDeleting;
  @FXML
  private Label deleteListLabel;
  private WineList selectedWinelist;
  private final Map<WineList, Button> winelistButtons = new HashMap<>();

  /**
   * Constructor
   *
   * @param managerContext manager context.
   */
  public ListScreenController(ManagerContext managerContext) {
    super(managerContext);
    this.wineListService = new WineListService(managerContext.authenticationManager,
        managerContext.databaseManager);
    bindToWineListsService();
  }

  /**
   * Changes the buttons that are displayed when lists are created or deleted.
   */
  private void bindToWineListsService() {
    ObservableList<WineList> wineLists = wineListService.getWineLists();
    wineLists.addListener((ListChangeListener<WineList>) change -> {
      while (change.next()) {
        if (change.wasAdded()) {
          change.getAddedSubList().forEach(wineList -> {
            Button button = createWineListElement(wineList);
            buttonList.getChildren().add(button);
            winelistButtons.put(wineList, button);
            setSelected(wineLists.getLast());
          });
        }
        if (change.wasRemoved()) {
          change.getRemoved().forEach(wineList -> {
            Button button = winelistButtons.remove(wineList);
            if (button != null)
              buttonList.getChildren().remove(button);
          });
          setSelected(wineLists.getFirst());
          createListRequestButton.setDisable(wineLists.size() > 2);

        }
      }
    });
  }

  private Button createWineListElement(WineList wineList) {
    Button button = new Button();
    button.setText(wineList.name());
    button.setMinSize(220, 70);
    button.setPrefWidth(100000);
    button.getStyleClass().add("primary-button");
    button.setFont(new Font("System Bold", 18));
    button.setDisable(false);
    button.setOnAction(actionEvent -> setSelected(wineList));
    return button;
  }

  private void setSelected(WineList selectedWineList) {
    this.selectedWinelist = selectedWineList;

    boolean canRemoveSelected = wineListService.canRemove(selectedWineList);
    deleteListRequestButton.setDisable(!canRemoveSelected);
    tabViewing.setText("VIEWING: " + selectedWineList.name());
    ObservableList<Wine> observableList = managerContext.databaseManager.getAggregatedDAO()
        .getWinesInList(selectedWineList);
    setupTableView(observableList);
  }

  /**
   * Initializes the page making sure the tab for creating lists is hidden.
   */
  public void initialize() {
    wineListService.init();
  }

  /**
   * opens the tab for creating lists and hides the tab for viewing lists.
   *
   * @param actionEvent triggers this function when on action.
   */
  @FXML
  public void onCreateListRequestButton(ActionEvent actionEvent) {
    managerContext.GUIManager.mainController.openCreateListPopUp(wineListService);
  }

  @FXML
  void onDeleteListRequestClick(WineList wineList) {
    managerContext.GUIManager.mainController.openDeleteListPopUp(wineList, wineListService);
  }

  /**
   * Deletes the selected list. Cannot delete the favourites or history list.
   *
   * @param actionEvent triggers this function when on action.
   */
  public void onDeleteListRequestButton(ActionEvent actionEvent) {
    if (selectedWinelist == null)
      return;
    if (!wineListService.canRemove(selectedWinelist)) {
      return;
    }
    onDeleteListRequestClick(selectedWinelist);
  }

  /**
   * Sets up the table of wines
   *
   * @param wines list of wines
   */
  @FXML
  public void setupTableView(ObservableList<Wine> wines) {
    tableView.getColumns().clear();

    tableView.setEditable(false);

    TableColumn<Wine, String> titleColumn = new TableColumn<>("Title");

    TableColumn<Wine, String> varietyColumn = new TableColumn<>("Variety");

    TableColumn<Wine, String> wineryColumn = new TableColumn<>("Winery");

    TableColumn<Wine, String> regionColumn = new TableColumn<>("Region");

    TableColumn<Wine, String> colorColumn = new TableColumn<>("Color");

    TableColumn<Wine, Integer> vintageColumn = new TableColumn<>("Vintage");

    TableColumn<Wine, String> descriptionColumn = new TableColumn<>("Description");

    TableColumn<Wine, Integer> scoreColumn = new TableColumn<>("Score");

    TableColumn<Wine, Float> abvColumn = new TableColumn<>("ABV%");

    TableColumn<Wine, Float> priceColumn = new TableColumn<>("NZD");

    titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
    varietyColumn.setCellValueFactory(new PropertyValueFactory<>("variety"));
    wineryColumn.setCellValueFactory(new PropertyValueFactory<>("winery"));
    regionColumn.setCellValueFactory(new PropertyValueFactory<>("region"));
    colorColumn.setCellValueFactory(new PropertyValueFactory<>("color"));
    vintageColumn.setCellValueFactory(new PropertyValueFactory<>("vintage"));
    descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
    scoreColumn.setCellValueFactory(new PropertyValueFactory<>("scorePercent"));
    abvColumn.setCellValueFactory(new PropertyValueFactory<>("abv"));
    priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

    tableView.getColumns().add(titleColumn);
    tableView.getColumns().add(varietyColumn);
    tableView.getColumns().add(wineryColumn);
    tableView.getColumns().add(regionColumn);
    tableView.getColumns().add(colorColumn);
    tableView.getColumns().add(vintageColumn);
    tableView.getColumns().add(descriptionColumn);
    tableView.getColumns().add(scoreColumn);
    tableView.getColumns().add(abvColumn);
    tableView.getColumns().add(priceColumn);

    tableView.getItems().clear();
    tableView.setItems(wines);
  }
}
