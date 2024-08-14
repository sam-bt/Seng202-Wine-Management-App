package seng202.team0.gui;

import java.net.URL;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team0.service.JavaScriptBridge;

public class LeafletOSMController {

  private static final Logger log = LogManager.getLogger(LeafletOSMController.class);

  @FXML
  private WebView webView;
  private WebEngine webEngine;
  private JavaScriptBridge javaScriptBridge;
  private JSObject javaScriptConnector;

  LeafletOSMController(Stage stage) {
    initMap();
  }

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
