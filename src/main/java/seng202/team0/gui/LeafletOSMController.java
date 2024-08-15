package seng202.team0.gui;

import java.net.URL;

import com.sun.javafx.webkit.WebConsoleListener;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team0.service.JavaScriptBridge;

/**
 * Map controller which is responsible for loading the map, JavaScript bridge
 * and controlling interaction
 *
 * @author Corey Hines
 */
public class LeafletOSMController {
  private static final Logger log = LogManager.getLogger(LeafletOSMController.class);

  @FXML
  private WebView webView;

  /**
   * Web engine with allows loading and rendering of a single web page
   */
  private WebEngine webEngine;

  /**
   * Bridge controller which allows calling JavaScript code
   */
  private JavaScriptBridge javaScriptBridge;

  /**
   * Holds the JavaScript program which is created during the Java run time
   */
  private JSObject javaScriptConnector;

  /**
   * Constructs the Leaflet OSM controller
   * @param stage The primary stage
   */
  LeafletOSMController(Stage stage) {
    initMap();
  }

  /**
   * Load the HTML map code and send it to the web engine. A state listener is created
   * which listens for a successful state change. On successful state change, the
   * JavaScript bridge is added.
   */
  private void initMap() {
    URL resource = getClass().getClassLoader().getResource("map/leaflet_osm_map.html");
    if (resource == null) {
      // failed to find the resource holding html for leaflet map
      log.error("Missing resource 'map/leaflet_osm_map.html'");
      return;
    }

    // make the web engine load the leaflet map as raw HTML
    String externalForm = resource.toExternalForm();
    webEngine = webView.getEngine();
    webEngine.setJavaScriptEnabled(true);
    webEngine.load(externalForm);

    // add hook to allow JavaScript logs to be logged Java side
    WebConsoleListener.setDefaultListener((view, message, lineNumber, sourceId) ->
            log.info(String.format("JavaScript Bridge Log: %d, message : %s", lineNumber, message)));

    // wait until the web engine has successfully loaded the leaflet map HTML
    webEngine.getLoadWorker().stateProperty().addListener(
        (ov, oldState, newState) -> {
          if (newState == Worker.State.SUCCEEDED) {
            // set the javascript bridge
            JSObject window = (JSObject) webEngine.executeScript("window");
            window.setMember("javaScriptBridge", javaScriptBridge);

            // calls the initMap function defined in leaflet_osm_map.js
            javaScriptConnector = (JSObject) webEngine.executeScript("jsConnector");
            javaScriptConnector.call("initMap");
          }
        });
  }
}
