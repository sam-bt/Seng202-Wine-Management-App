package seng202.team0.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import seng202.team0.database.Wine;
import seng202.team0.managers.ManagerContext;

/**
 * Controller for the screen that displays wines
 */

public class WineScreenController extends Controller{

  @FXML
  TableView<Wine> tableView;
  /**
   * Constructor
   *
   * @param managerContext manager context
   */
  public WineScreenController(ManagerContext managerContext) {
    super(managerContext);

  }

  /**
   * Called after the constructor for when fxml is loaded
   * <p>
   *   Gets, loads, and displays a table from a list of wines from the controller layer
   * </p>
   */
  @Override
  public void init() {

    ObservableList<Wine> wines = FXCollections.observableArrayList(
        new Wine("Joe Wine", "Jeaux", "New Zealand", "Joetown Wines", "Hints of joe", 100, 15.0f,
            10.0f),
        new Wine("Joe Wine2", "Jeaux", "New Zealand", "Joetown Wines", "Hints of joe", 50, 10.0f,
            12.0f)

    );

    tableView.setItems(wines);
    tableView.setEditable(false);
    TableColumn<Wine, String> titleColumn = new TableColumn<>("Title");
    titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
    tableView.getColumns().add(titleColumn);

    TableColumn<Wine, String> varietyColumn = new TableColumn<>("Variety");
    varietyColumn.setCellValueFactory(new PropertyValueFactory<>("variety"));
    tableView.getColumns().add(varietyColumn);

    TableColumn<Wine, String> wineryColumn = new TableColumn<>("Winery");
    wineryColumn.setCellValueFactory(new PropertyValueFactory<>("winery"));
    tableView.getColumns().add(wineryColumn);

    TableColumn<Wine, String> descriptionColumn = new TableColumn<>("Description");
    descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
    tableView.getColumns().add(descriptionColumn);

    TableColumn<Wine, String> scoreColumn = new TableColumn<>("Score");
    scoreColumn.setCellValueFactory(new PropertyValueFactory<>("scorePercent"));
    tableView.getColumns().add(scoreColumn);

    TableColumn<Wine, Float> abvColumn = new TableColumn<>("ABV%");
    abvColumn.setCellValueFactory(new PropertyValueFactory<>("abv"));
    tableView.getColumns().add(abvColumn);

    TableColumn<Wine, Float> priceColumn = new TableColumn<>("NZD");
    priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
    tableView.getColumns().add(priceColumn);

  }

}
