package seng202.team0.gui;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.sun.javafx.webkit.WebConsoleListener;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;
import netscape.javascript.JSObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team0.database.GeoLocation;
import seng202.team0.database.Wine;

/**
 * Map controller which is responsible for loading the map, JavaScript bridge
 * and controlling interaction
 */
public class LeafletOSMController {
  private static final Logger log = LogManager.getLogger(LeafletOSMController.class);
  /**
   * Web engine with allows loading and rendering of a single web page
   */
  private final WebEngine webEngine;

  /**
   * Holds the JavaScript program which is created during the Java run time
   */
  private JSObject javaScriptConnector;

  private Runnable onReadyAction;

  LeafletOSMController( WebEngine webEngine) {
    this.webEngine = webEngine;
  }

  /**
   * Load the HTML map code and send it to the web engine. A state listener is created
   * which listens for a successful state change. On successful state change, the
   * JavaScript bridge is added.
   */
  public void initMap() {
    InputStream input = getClass().getResourceAsStream("/map/leaflet_osm_map.html");
    if (input == null) {
      log.error("Failed to read contents of \"map/leaflet_osm_map.html\"");
      return;
    }
    String content = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))
        .lines()
        .collect(Collectors.joining("\n"));
    webEngine.setJavaScriptEnabled(true);
    webEngine.loadContent(content);

    WebConsoleListener.setDefaultListener((view, message, lineNumber, sourceId) ->
        log.info(String.format("Map WebView console log line: %d, message : %s", lineNumber, message)));

    // wait until the web engine has successfully loaded the HTML
    webEngine.getLoadWorker().stateProperty().addListener(
        (ov, oldState, newState) -> {
          if (newState == Worker.State.SUCCEEDED) {
            // set the javascript bridge and call the initMap function in JavaScript
            // to display the map
            JSObject window = (JSObject) webEngine.executeScript("window");
            javaScriptConnector = (JSObject) webEngine.executeScript("jsConnector");
            javaScriptConnector.call("initMap");

            if (onReadyAction != null) {
              onReadyAction.run();
              onReadyAction = null;
            }
          }
        });
  }

  public void clearWineMarkers() {
    javaScriptConnector.call("clearMarkers");
  }
  public void addWineMarker(Wine wine) {
    GeoLocation geoLocation = wine.getGeoLocation();
    javaScriptConnector.call("addWineMarker", wine.getTitle(), wine.getRegion(), wine.getColor(), geoLocation.getLatitude(), geoLocation.getLongitude());
  }

  public void setOnReadyAction(Runnable runnable) {
    if (javaScriptConnector != null) {
      runnable.run();
      return;
    }
    onReadyAction = runnable;
  }
}
