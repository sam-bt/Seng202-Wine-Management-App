package seng202.team6.gui.controls;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Bounds;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.Map;

public class CardsContainer<T> extends VBox {
    /**
     * Stores a mapping of elements to their corresponding buttons.
     */
    private final Map<T, CardContainer> cards = new HashMap<>();

    public CardsContainer(ReadOnlyObjectProperty<Bounds> viewportBounds, ReadOnlyDoubleProperty scrollPaneWidth) {
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

    public void addCard(T element, CardContainer cardContainer) {
        cards.put(element, cardContainer);
        getChildren().add(cardContainer);
    }
}
