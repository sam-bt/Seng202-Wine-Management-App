package seng202.team6.gui.popup;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import seng202.team6.gui.Controller;
import seng202.team6.managers.ManagerContext;
import seng202.team6.model.User;


/**
 * Controller for the user search popup.
 */
public class UserSearchPopupController extends Controller {

  @FXML
  private TextField searchTextField;

  @FXML
  private TableView<User> userTableView;

  @FXML
  private TableColumn<User, String> userTableColumn;

  /**
   * Constructor for the user search popup.
   */
  public UserSearchPopupController(ManagerContext context) {
    super(context);
  }

  /**
   * Called to init this controller after set up.
   */
  public void init() {

    userTableView.setOnMouseClicked(this::openUserOnClick);

    searchTextField.setOnKeyPressed(event -> {
      if (event.getCode() == KeyCode.ENTER) {
        onSearchButtonClick();
        event.consume();
      }
    });

    Platform.runLater(() -> searchTextField.requestFocus());

  }

  /**
   * Opens a wine when mouse is clicked.
   *
   * @param event event
   */
  @FXML
  public void openUserOnClick(MouseEvent event) {
    if (event.getClickCount() == 2) {
      User user = userTableView.getSelectionModel().getSelectedItem();
      if (user != null) {
        managerContext.getGuiManager().openUserProfilePopup(user);
      }
    }
  }

  @FXML
  void onBackButtonClick() {
    managerContext.getGuiManager().closePopup();
  }

  @FXML
  void onSearchButtonClick() {

    userTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
    userTableView.getItems().clear();

    String searchName = searchTextField.getText();
    ObservableList<User> results = managerContext.getDatabaseManager().getUserDao()
        .getAllFromSearch(searchName);

    userTableColumn.setCellValueFactory(cellData ->
        new SimpleStringProperty(cellData.getValue().getUsername())
    );

    if (results != null && !results.isEmpty()) {
      userTableView.setItems(results);
    } else {
      Label placeholderLabel = new Label("No results found");
      placeholderLabel.setStyle("-fx-font-weight: bold;");
      userTableView.setPlaceholder(placeholderLabel);
    }


  }


}
