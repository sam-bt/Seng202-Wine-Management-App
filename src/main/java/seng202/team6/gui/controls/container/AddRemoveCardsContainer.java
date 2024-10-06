package seng202.team6.gui.controls.container;


import java.util.HashMap;
import java.util.Map;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import seng202.team6.util.IconPaths;

public class AddRemoveCardsContainer<T> extends VBox {
  /**
   * Stores a mapping of elements to their corresponding buttons.
   */
  private final Map<T, GridPane> buttons = new HashMap<>();

  public AddRemoveCardsContainer(ReadOnlyObjectProperty<Bounds> viewportBounds, ReadOnlyDoubleProperty scrollPaneWidth) {
    setSpacing(10);
    setFillWidth(true);

    // Listen to the viewport width and the scroll pane's width and adjust VBox width accordingly
    viewportBounds.addListener((obs, oldBounds, newBounds) -> {
      if (newBounds != null) {
        double newWidth = newBounds.getWidth();
        setPrefWidth(newWidth);
      }
    });

    scrollPaneWidth.addListener((obs, oldVal, newVal) -> {
      // Adjust the VBox width based on the ScrollPane width, accounting for possible scroll bars
      if (newVal != null) {
        setPrefWidth(newVal.doubleValue());
      }
    });
  }

  public void add(T element, StringProperty text, boolean shouldAdd, Runnable addClickAction,
      Runnable removeClickAction) {
    Label listNameLabel = new Label();
    listNameLabel.textProperty().bind(text);
    listNameLabel.setPadding(new Insets(10, 20, 10, 20));
    listNameLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");
    listNameLabel.setWrapText(true);

    Button button = new Button();
    button.setPrefSize(28, 32);
    button.setMaxSize(28, 32);
    button.setMinSize(28, 32);
    // remove default button style
    button.getStylesheets().add("css/add_remove_buttons.css");
    GridPane.setHalignment(button, HPos.CENTER);

    CardWrapper wrapper = new CardWrapper(button, shouldAdd, addClickAction, removeClickAction);
    RowConstraints firstRow = new RowConstraints();
    ColumnConstraints firstColumn = new ColumnConstraints();
    ColumnConstraints secondColumn = new ColumnConstraints();
    firstColumn.setPercentWidth(80);
    secondColumn.setPercentWidth(20);
    firstColumn.setHgrow(Priority.NEVER);
    secondColumn.setHgrow(Priority.NEVER);
    wrapper.getRowConstraints().add(firstRow);
    wrapper.getColumnConstraints().addAll(firstColumn, secondColumn);
    wrapper.setAlignment(Pos.CENTER);
    wrapper.getStyleClass().add("secondary-background");
    wrapper.maxWidthProperty().bind(widthProperty());
    wrapper.minWidthProperty().bind(widthProperty());
    wrapper.prefWidthProperty().bind(widthProperty());
    wrapper.add(listNameLabel, 0, 0);
    wrapper.add(button, 1, 0);
    buttons.put(element, wrapper);
    getChildren().add(wrapper);
  }

  static class CardWrapper extends GridPane {
    private final Button button;
    protected final Runnable addClickRunnable;
    protected final Runnable removeClickRunnable;

    CardWrapper(Button button, boolean shouldAdd, Runnable addClickRunnable, Runnable removeClickRunnable) {
      this.button = button;
      this.addClickRunnable = addClickRunnable;
      this.removeClickRunnable = removeClickRunnable;
      updateActionIcon(shouldAdd);
    }

    private void updateActionIcon(boolean shouldAdd) {
      SVGPath svgPath = new SVGPath();
      svgPath.getStyleClass().add("icon");
      svgPath.setContent(shouldAdd ? IconPaths.ADD_PATH : IconPaths.REMOVE_PATH);
      svgPath.setScaleX(0.05);
      svgPath.setScaleY(0.05);
      button.setGraphic(svgPath);
      button.setOnMouseClicked((event) -> {
        if (shouldAdd) {
          addClickRunnable.run();
        } else {
          removeClickRunnable.run();
        }
        updateActionIcon(!shouldAdd);
      });
    }
  }
}
