package seng202.team6.gui;

import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;
import javafx.util.converter.FloatStringConverter;
import javafx.util.converter.IntegerStringConverter;
import seng202.team6.managers.ManagerContext;
import seng202.team6.model.User;
import seng202.team6.model.Wine;
import seng202.team6.model.WineList;

/**
 * List Screen Controller (MORE DETAIL HERE!)
 */
public class ListScreenController extends Controller {


  @FXML
  public Button createListRequestButton;
  @FXML
  public Button backButton;
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
  public Button listOneButton, listTwoButton, listThreeButton, listFourButton, listFiveButton;
  @FXML
  public Button deleteListRequestButton;
  @FXML
  public TableView<Wine> tableView;

  public List<WineList> wineLists;
  private int selected = 1;

  /**
   * Constructor
   *
   * @param managerContext manager context
   */
  public ListScreenController(ManagerContext managerContext) {
    super(managerContext);
  }

  /**
   * Initializes the page making sure the tab for creating lists is hidden.
   */
  public void initialize() {
    listScreenTabs.getTabs().remove(tabCreating);
    updateListOptions();
    tabViewing.setText("VIEWING: " + wineLists.getFirst());
    selected = 1;
    changeSelected();
    if (wineLists.size() == 1) {
      deleteListRequestButton.setDisable(true);
    }
  }

  /**
   * opens the tab for creating lists and hides the tab for viewing lists.
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
   * opens the tab for viewing lists and hides the tab for creating lists.
   *
   * @param actionEvent triggers this function when on action.
   */
  @FXML
  public void onBackButton(ActionEvent actionEvent) {
    listScreenTabs.getTabs().add(tabViewing);
    listScreenTabs.getTabs().remove(tabCreating);
    createListRequestButton.setDisable(false);
    deleteListRequestButton.setDisable(false);
    if (wineLists.size() == 5) {
      createListRequestButton.setDisable(true);
    } else if (wineLists.size() == 1) {
      deleteListRequestButton.setDisable(true);
    }
    listName.setText("");
    errorText.setVisible(false);

  }

  /**
   * creates the lists, adding it to the array and updates relevant information on screen
   *
   * @param actionEvent triggers this function when on action.
   */
  @FXML
  public void onCreateListConfirmButton(ActionEvent actionEvent) {
    String name = listName.getText();
    if (wineLists.stream().anyMatch(wineList -> wineList.name().equals(name))) {
      errorText.setText("List Already Exists");
      errorText.setVisible(true);
    } else {

      if (name.length() < 3 || name.length() > 10 || !name.matches("[a-zA-Z0-9_]+")) {
        errorText.setText("Invalid List Name");
        errorText.setVisible(true);
      } else {
        errorText.setVisible(false);

        User user = managerContext.authenticationManager.getAuthenticatedUser();
        managerContext.databaseManager.getWineListDAO().create(user, name);

        listName.setText("");
        updateListOptions();
        deleteListRequestButton.setDisable(false);
        onBackButton(actionEvent);
        selected = wineLists.size();
        changeSelected();
      }
    }
  }

  /**
   * deletes the selected list. Cannot delete the favourites list.
   *
   * @param actionEvent triggers this function when on action.
   */
  public void onDeleteListRequestButton(ActionEvent actionEvent) {
    if (selected != 1) {
      WineList wineList = wineLists.get(selected - 1);
      managerContext.databaseManager.getWineListDAO().delete(wineList);
      updateListOptions();
      selected -= 1;
      changeSelected();
      createListRequestButton.setDisable(false);
      if (wineLists.size() == 1) {
        deleteListRequestButton.setDisable(true);
      }
    }
  }

  /**
   * updates the information displayed on the screen
   **/

  @FXML
  public void updateListOptions() {
    Button[] buttons = {listOneButton, listTwoButton, listThreeButton, listFourButton,
        listFiveButton};
    String user = managerContext.authenticationManager.getAuthenticatedUsername();
    wineLists = managerContext.databaseManager.getUserLists(user);
    for (int i = 0; i < buttons.length; i++) {
      if (i < wineLists.size()) {
        buttons[i].setText(wineLists.get(i).name());
        buttons[i].setDisable(false);
      } else {
        buttons[i].setText("Empty List");
        buttons[i].setDisable(true);
      }
    }
  }

  /**
   * Selects List One.
   *
   * @param actionEvent triggers this function when on action.
   */
  public void onListOneButton(ActionEvent actionEvent) {
    selected = 1;
    changeSelected();
  }

  /**
   * Selects List Two.
   *
   * @param actionEvent triggers this function when on action.
   */
  public void onListTwoButton(ActionEvent actionEvent) {
    selected = 2;
    changeSelected();

  }

  /**
   * Selects List Three.
   *
   * @param actionEvent triggers this function when on action.
   */
  public void onListThreeButton(ActionEvent actionEvent) {
    selected = 3;
    changeSelected();

  }

  /**
   * Selects List Four.
   *
   * @param actionEvent triggers this function when on action.
   */
  public void onListFourButton(ActionEvent actionEvent) {
    selected = 4;
    changeSelected();

  }

  /**
   * Selects List Five;
   *
   * @param actionEvent triggers this function when on action.
   */
  public void onListFiveButton(ActionEvent actionEvent) {
    selected = 5;
    changeSelected();

  }

  /**
   * Changes the selected list.
   */
  @FXML
  public void changeSelected() {
    tabViewing.setText("VIEWING: " + wineLists.get(selected - 1).name());
    tableView.getItems().clear();

    String user = managerContext.authenticationManager.getAuthenticatedUsername();
    List<WineList> userLists = managerContext.databaseManager.getUserLists(user);
    WineList fromUserLists = userLists.get(selected-1);
    List<Wine> list = managerContext.databaseManager.getWinesInList(fromUserLists);
    ObservableList<Wine> observableList = FXCollections.observableList(list);
    setupTableView();
    tableView.setItems(observableList);
  }

  @FXML
  public void setupTableView() {
    tableView.getColumns().clear();

    StringConverter<String> stringConverter = new DefaultStringConverter();
    StringConverter<Integer> intConverter = new IntegerStringConverter();
    StringConverter<Float> floatConverter = new FloatStringConverter();


    tableView.setEditable(true);

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


    titleColumn.setCellValueFactory(new PropertyValueFactory<>("title") );
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

    tableView.setRowFactory((tableView) -> {
      TableRow<Wine> tableRow = new TableRow<>();
      tableRow.setOnMouseClicked(event -> {
        if (event.getClickCount() == 2 && !tableRow.isEmpty()) {
          Wine wine = tableRow.getItem();
          onWineInListClick(wine);
        }
      });
      return tableRow;
    });
  }

  public void onWineInListClick(Wine wine) {
    Alert alert = new Alert(AlertType.CONFIRMATION);
    alert.setTitle("Delete Wine from List");
    alert.setHeaderText("Would you like to remove " + wine.getTitle() + " from this list?");
    ButtonType buttonType = alert.showAndWait().orElse(null);
    if (buttonType == ButtonType.OK) {
      WineList selectedList = wineLists.get(selected - 1);
      managerContext.databaseManager.deleteWineFromList(selectedList, wine);
      tableView.getItems().remove(wine);
    }
  }
}
