package seng202.team6.gui.wrapper;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import seng202.team6.util.GeolocationResolver;
import seng202.team6.util.ImageReader;

/**
 * Class starts the javaFX application window.
 *
 * @author seng202 teaching team
 */
public class FxWindow extends Application {

  /**
   * Launches the FXML application, this must be called from another class (in this cass App.java)
   * otherwise JavaFX errors out and does not run.
   *
   * @param args command line arguments
   */
  public static void launchWrapper(String[] args) {
    launch(args);
  }

  /**
   * Opens the gui with the fxml content specified in resources/fxml/main.fxml.
   *
   * @param primaryStage The current fxml stage, handled by javaFX Application class
   * @throws IOException if there is an issue loading fxml file
   */
  @Override
  public void start(Stage primaryStage) throws IOException {
    FXMLLoader baseLoader = new FXMLLoader(getClass().getResource("/fxml/fx_wrapper.fxml"));
    Parent root = baseLoader.load();

    FxWrapper baseController = baseLoader.getController();
    baseController.init(primaryStage);

    primaryStage.setTitle("WINO App [DEV]");
    Scene scene = new Scene(root);
    primaryStage.setResizable(false);
    primaryStage.setMinHeight(900);
    primaryStage.setMinWidth(1400);
    primaryStage.setScene(scene);
    primaryStage.sizeToScene();
    primaryStage.setMinHeight(900);
    primaryStage.setMinWidth(1400);
    primaryStage.getIcons().add(ImageReader.loadImage("/img/icon.png"));
    primaryStage.show();
  }
}
