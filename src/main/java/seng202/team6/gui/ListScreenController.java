package seng202.team6.gui;

import java.util.List;
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
import seng202.team6.service.WineListService;

/**
 * Controller to display the user defined lists of wines.
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
  public Tab tabCreating;
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
  private int selected = 0;

  /**
   * Constructor.
   *
   * @param managerContext manager context
   */
  public ListScreenController(ManagerContext managerContext) {
    super(managerContext);
    this.wineListService = new WineListService(managerContext.getAuthenticationManager(),
        managerContext.getDatabaseManager());
  }

  /**
   * Updates all the buttons.
   *
   * @param wineLists list of wine lists
   */
  public void updateButtons(List<WineList> wineLists) {
    buttonList.getChildren().clear();
    int i = 0;
    for (WineList wineList : wineLists) {
      Button button = new Button();
      button.setText(wineList.name());
      button.setMinSize(220, 70);
      button.setPrefWidth(100000);
      button.getStyleClass().add("primary-button");
      button.setFont(new Font("System Bold", 18));
      button.setDisable(false);
      int listIndex = i++;
      button.setOnAction(actionEvent -> {
        selected = listIndex;
        render();
      });

      buttonList.getChildren().add(button);
    }

    deleteListRequestButton.setDisable(!wineListService.canRemove(wineLists.get(selected)));

    createListRequestButton.setDisable(false);
  }

  /**
   * Refreshes all the buttons & UI state when there is an update.
   */
  public void render() {

    List<WineList> wineLists = wineListService.getWineLists();
    selected = Math.min(selected, wineLists.size() - 1);
    updateButtons(wineLists);
    tabViewing.setText("VIEWING: " + wineLists.get(selected));
    changeSelected();
  }


  /**
   * Initializes the page making sure the tab for creating lists is hidden.
   */
  public void initialize() {
    listScreenTabs.getTabs().remove(tabCreating);
    wineListService.init();
    selected = 0;
    render();
  }

  /**
   * Opens the tab for creating lists and hides the tab for viewing lists.
   *
   * @param actionEvent triggers this function when on action.
   */
  @FXML
  public void onCreateListRequestButton(ActionEvent actionEvent) {
    listScreenTabs.getTabs().add(tabCreating);
    listScreenTabs.getTabs().remove(tabViewing);
    createListRequestButton.setDisable(true);
    deleteListRequestButton.setDisable(true);
  }

  /**
   * Opens the tab for viewing lists and hides the tab for creating lists.
   *
   * @param actionEvent triggers this function when on action.
   */
  @FXML
  public void onBackButton(ActionEvent actionEvent) {
    listScreenTabs.getTabs().add(tabViewing);
    listScreenTabs.getTabs().remove(tabCreating);
    render();

  }

  /**
   * Creates the lists, adding it to the array and updates relevant information on screen.
   *
   * @param actionEvent triggers this function when on action.
   */
  @FXML
  public void onCreateListConfirmButton(ActionEvent actionEvent) {
    String name = listName.getText();
    List<WineList> wineLists = wineListService.getWineLists();
    if (wineLists.stream().anyMatch(wineList -> wineList.name().equals(name))) {
      errorText.setText("List Already Exists");
      errorText.setVisible(true);
    } else {

      if (name.length() < 3 || name.length() > 10 || !name.matches("[a-zA-Z0-9_]+")) {
        errorText.setText("Invalid List Name");
        errorText.setVisible(true);
      } else {
        errorText.setVisible(false);

        User user = managerContext.getAuthenticationManager().getAuthenticatedUser();
        wineListService.createWineList(user, name);

        listName.setText("");
        selected = wineLists.size() - 1;
        render();
        onBackButton(actionEvent);
      }
    }
  }

  /**
   * Deletes the selected list. Cannot delete the favourites or history list.
   *
   * @param actionEvent triggers this function when on action.
   */
  public void onDeleteListRequestButton(ActionEvent actionEvent) {
    WineList wineList = wineListService.getWineLists().get(selected);
    if (!wineListService.canRemove(wineList)) {
      return;
    }

    wineListService.deleteWineList(wineList);
    managerContext.getDatabaseManager().getWineListDao().delete(wineList);
    render();
  }

  /**
   * Changes the selected list.
   */
  public void changeSelected() {
    WineList selectedWineList = wineListService.getWineLists().get(selected);
    ObservableList<Wine> observableList = managerContext.getDatabaseManager().getAggregatedDao()
        .getWinesInList(selectedWineList);
    setupTableView(observableList);
  }

  /**
   * Sets up the table of wines.
   *
   * @param wines list of wines
   */
  @FXML
  public void setupTableView(ObservableList<Wine> wines) {
    tableView.getColumns().clear();

    tableView.setEditable(false);

    final TableColumn<Wine, String> titleColumn = new TableColumn<>("Title");

    final TableColumn<Wine, String> varietyColumn = new TableColumn<>("Variety");

    final TableColumn<Wine, String> wineryColumn = new TableColumn<>("Winery");

    final TableColumn<Wine, String> regionColumn = new TableColumn<>("Region");

    final TableColumn<Wine, String> colorColumn = new TableColumn<>("Color");

    final TableColumn<Wine, Integer> vintageColumn = new TableColumn<>("Vintage");

    final TableColumn<Wine, String> descriptionColumn = new TableColumn<>("Description");

    final TableColumn<Wine, Integer> scoreColumn = new TableColumn<>("Score");

    final TableColumn<Wine, Float> abvColumn = new TableColumn<>("ABV%");

    final TableColumn<Wine, Float> priceColumn = new TableColumn<>("NZD");

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
