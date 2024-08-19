package seng202.team0.gui;

import java.util.ArrayList;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableListValue;
import javafx.beans.value.ObservableStringValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import seng202.team0.database.DataTable;
import seng202.team0.database.Record;
import seng202.team0.database.Value;
import seng202.team0.managers.ManagerContext;

/**
 * Controller for the screen that displays wines
 */

public class WineScreenController extends Controller{

  @FXML
  TableView<Record> tableView;
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
   */
  @Override
  public void init() {
    // Would get wines table here

    ArrayList<ArrayList<Value>> columns = new ArrayList<>();
    columns.add(new ArrayList<>());
    columns.add(new ArrayList<>());
    columns.add(new ArrayList<>());
    columns.get(0).add(Value.make("Good wine"));
    columns.get(0).add(Value.make("Joe's Better Wine"));

    columns.get(1).add(Value.make("Bob"));
    columns.get(1).add(Value.make("Joe"));

    columns.get(2).add(Value.make(90.0));
    columns.get(2).add(Value.make(100.0));

    DataTable table = new DataTable(new String[]{"Name", "Producer", "Score"}, columns);

    ObservableList<Record> records = FXCollections.observableArrayList(
        table.getRecordForIndex(0),
        table.getRecordForIndex(1)
    );

    tableView.setItems(records);
    tableView.setEditable(false);
    for(int i=0; i < table.columnSize(); i++){
      String name = table.getColumnName(i);
      TableColumn<Record, String> column = new TableColumn<>(name);
      column.setCellValueFactory(recordStringCellDataFeatures -> {
        return new ObservableStringValue() {
          @Override
          public String get() {
            return "joe";
          }

          @Override
          public void addListener(ChangeListener<? super String> changeListener) {

          }

          @Override
          public void removeListener(ChangeListener<? super String> changeListener) {

          }

          @Override
          public String getValue() {
            return "joe";
          }

          @Override
          public void addListener(InvalidationListener invalidationListener) {

          }

          @Override
          public void removeListener(InvalidationListener invalidationListener) {

          }
        };
      });
      tableView.getColumns().add(column);
    }


    System.out.println("Hi");
  }

}
