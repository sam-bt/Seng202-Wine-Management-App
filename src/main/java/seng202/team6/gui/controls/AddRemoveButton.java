package seng202.team6.gui.controls;

import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.SVGPath;
import seng202.team6.util.IconPaths;

public class AddRemoveButton extends GridPane {
    private final Button button;
    protected final Runnable addClickRunnable;
    protected final Runnable removeClickRunnable;
    private final boolean white;

    public AddRemoveButton(boolean shouldAdd, Runnable addClickRunnable, Runnable removeClickRunnable, boolean white) {
        this.white = white;
        this.button = new Button();
        this.addClickRunnable = addClickRunnable;
        this.removeClickRunnable = removeClickRunnable;
        updateActionIcon(shouldAdd);
        button.setPrefSize(28, 32);
        button.setMaxSize(28, 32);
        button.setMinSize(28, 32);
        button.getStylesheets().add("css/add_remove_buttons.css");
        setPrefSize(28, 32);
        setMinSize(28, 32);
        setMaxSize(28, 32);
        add(button, 1, 0);
        GridPane.setHalignment(button, HPos.CENTER);
    }

    private void updateActionIcon(boolean shouldAdd) {
        SVGPath svgPath = new SVGPath();
        svgPath.getStyleClass().add(white ? "icon" : "icon-red");
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
