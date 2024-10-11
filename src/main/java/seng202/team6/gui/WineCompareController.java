package seng202.team6.gui;

import java.util.Set;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
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
      container.setPadding(new Insets(10));
      container.setStyle("-fx-background-color: #f3f4f6; -fx-background-radius: 10px;");
      setup();
    }

    private void setup() {
      Set<String> uniqueTitles = managerContext.getDatabaseManager().getWineDataStatService()
          .getUniqueTitles();
      searchTextField = new AutoCompletionTextField();
      searchTextField.setPrefWidth(300);
      searchTextField.getEntries().addAll(uniqueTitles);

      Label searchLabel = new Label("Search for a Wine");
      searchLabel.setFont(Font.font(18));

      Separator separator = new Separator();
      HBox wrapper = new HBox(searchLabel, searchTextField);
      wrapper.setAlignment(Pos.CENTER);
      wrapper.setSpacing(10);
      container.getChildren().addAll(wrapper, separator);
    }
  }
}
