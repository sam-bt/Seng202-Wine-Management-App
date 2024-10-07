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

/**
 * A class that represents a container for managing a collection of cards with add and remove
 * functionality.
 *
 * @param <T> the type of elements associated with the cards in this container
 */
public class AddRemoveCardsContainer<T> extends VBox {

  /**
   * Stores a mapping of elements to their corresponding buttons.
   */
  private final Map<T, GridPane> buttons = new HashMap<>();

  /**
   * Constructs an AddRemoveCardsContainer that adjusts its width based on the specified viewport
   * bounds and scroll pane width.
   *
   * @param viewportBounds a ReadOnlyObjectProperty representing the bounds of the viewport
   * @param scrollPaneWidth a ReadOnlyDoubleProperty representing the width of the scroll pane
   */
  public AddRemoveCardsContainer(ReadOnlyObjectProperty<Bounds> viewportBounds,
      ReadOnlyDoubleProperty scrollPaneWidth) {
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

  /**
   * Adds a card associated with a specified element to the container.
   *
   * @param element the element associated with the card
   * @param text a StringProperty representing the text to be displayed on the card
   * @param shouldAdd a boolean indicating whether the button is for adding or removing
   * @param addClickAction a Runnable to be executed when the add button is clicked
   * @param removeClickAction a Runnable to be executed when the remove button is clicked
   */
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

    final CardWrapper wrapper = new CardWrapper(button, shouldAdd, addClickAction,
            removeClickAction);
    final RowConstraints firstRow = new RowConstraints();
    final ColumnConstraints firstColumn = new ColumnConstraints();
    final ColumnConstraints secondColumn = new ColumnConstraints();
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

  /**
   * A static inner class that wraps a button in a GridPane for managing its action and appearance.
   */
  static class CardWrapper extends GridPane {

    protected final Runnable addClickRunnable;
    protected final Runnable removeClickRunnable;
    private final Button button;

    /**
     * Constructs a CardWrapper with specified button and actions.
     *
     * @param button the Button to be wrapped
     * @param shouldAdd a boolean indicating if the button is for adding
     * @param addClickRunnable a Runnable to be executed when the add action is triggered
     * @param removeClickRunnable a Runnable to be executed when the remove action is triggered
     */
    CardWrapper(Button button, boolean shouldAdd, Runnable addClickRunnable,
        Runnable removeClickRunnable) {
      this.button = button;
      this.addClickRunnable = addClickRunnable;
      this.removeClickRunnable = removeClickRunnable;
      updateActionIcon(shouldAdd);
    }

    /**
     * Updates the button icon based on whether it is in add or remove mode.
     *
     * @param shouldAdd a boolean indicating if the button is for adding
     */
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
