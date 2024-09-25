package seng202.team6.gui;

import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
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
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;
import javafx.util.converter.FloatStringConverter;
import javafx.util.converter.IntegerStringConverter;
import seng202.team6.managers.ManagerContext;
import seng202.team6.model.Wine;
import seng202.team6.model.WineList;
import seng202.team6.service.AuthenticationService;

/**
 * List Screen Controller (MORE DETAIL HERE!)
 */
public class ListScreenController extends Controller {

  private final AuthenticationService authenticationService;

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
  public VBox buttonList;
  @FXML
  public Button deleteListRequestButton;
  @FXML
  public TableView<Wine> tableView;

  private int selected = 0;

  /**
   * Constructor
   *
   * @param managerContext manager context
   */
  public ListScreenController(ManagerContext managerContext,
      AuthenticationService authenticationService) {
    super(managerContext);
    this.authenticationService = authenticationService;
  }
  List<WineList> getWineLists() {
    String user = authenticationService.getAuthenticatedUsername();
    return managerContext.databaseManager.getUserLists(user);
  }
  public void updateButtons() {
    buttonList.getChildren().clear();
    int i=0;
    List<WineList> wineLists = getWineLists();
    for(WineList wineList : wineLists) {
      Button button = new Button();
      button.setText(wineList.name());
      button.setMinSize(220, 70);
      button.getStyleClass().add("primary-button");
      button.setFont(new Font("System Bold", 18));
      button.setDisable(false);
      int iCopy = i++;
      button.setOnAction(actionEvent -> {
        selected = iCopy;
        render();
      });

      buttonList.getChildren().add(button);
    }
    createListRequestButton.setDisable(false);
    deleteListRequestButton.setDisable(wineLists.size() <= 2);
  }

  public void render() {
    updateButtons();
    List<WineList> wineLists = managerContext.databaseManager.getUserLists(authenticationService.getAuthenticatedUsername());
    tabViewing.setText("VIEWING: " + wineLists.get(selected));
    changeSelected();

  }


  /**
   * Initializes the page making sure the tab for creating lists is hidden.
   */
  public void initialize() {
    listScreenTabs.getTabs().remove(tabCreating);
    selected = 0;
    render();
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
    render();

  }

  /**
   * creates the lists, adding it to the array and updates relevant information on screen
   *
   * @param actionEvent triggers this function when on action.
   */
  @FXML
  public void onCreateListConfirmButton(ActionEvent actionEvent) {
    String name = listName.getText();
    List<WineList> wineLists = managerContext.databaseManager.getUserLists(authenticationService.getAuthenticatedUsername());
    if (wineLists.stream().anyMatch(wineList -> wineList.name().equals(name))) {
      errorText.setText("List Already Exists");
      errorText.setVisible(true);
    } else {

      if (name.length() < 3 || name.length() > 10 || !name.matches("[a-zA-Z0-9_]+")) {
        errorText.setText("Invalid List Name");
        errorText.setVisible(true);
      } else {
        errorText.setVisible(false);

        String user = authenticationService.getAuthenticatedUsername();

        managerContext.databaseManager.createList(user, name);

        listName.setText("");
        selected = wineLists.size() - 1;
        render();
        onBackButton(actionEvent);
      }
    }
  }

  /**
   * deletes the selected list. Cannot delete the favourites list.
   *
   * @param actionEvent triggers this function when on action.
   */
  public void onDeleteListRequestButton(ActionEvent actionEvent) {
    if (selected != 0) {
      WineList wineList = getWineLists().get(selected);
      managerContext.databaseManager.deleteList(wineList);
      render();
    }
  }

  /**
   * Changes the selected list.
   */
  public void changeSelected() {
    tableView.getItems().clear();
;
    List<Wine> list = managerContext.databaseManager.getWinesInList(getWineLists().get(selected));
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
      WineList selectedList = getWineLists().get(selected);
      managerContext.databaseManager.deleteWineFromList(selectedList, wine);
      tableView.getItems().remove(wine);
    }
  }
}
