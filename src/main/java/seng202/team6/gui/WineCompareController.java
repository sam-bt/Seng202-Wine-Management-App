package seng202.team6.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import seng202.team6.gui.controls.AutoCompletionTextField;
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
    private AutoCompletionTextField searchTextField;
    private Wine wine;

    WineSide(VBox container) {
      this.container = container;
      setup();
    }

    void setup() {
      searchTextField = new AutoCompletionTextField();
      searchTextField.setMaxWidth(Double.MAX_VALUE);

      Label searchLabel = new Label("Search for a Wine");
      HBox wrapper = new HBox(searchLabel, searchTextField);
      HBox.setHgrow(searchTextField, Priority.ALWAYS);
      container.getChildren().add(wrapper);
    }
  }
}
