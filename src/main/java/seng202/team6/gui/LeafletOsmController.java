package seng202.team6.gui;

import com.sun.javafx.webkit.WebConsoleListener;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;
import netscape.javascript.JSObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team6.model.GeoLocation;
import seng202.team6.model.Vineyard;
import seng202.team6.model.Wine;

/**
 * Map controller which is responsible for loading the map and calling JavaScript functions which
 * add markers to the map.
 */
public class LeafletOsmController {

  private static final Logger log = LogManager.getLogger(LeafletOsmController.class);

  /**
   * Web engine with allows loading and rendering of a single web page.
   */
  private final WebEngine webEngine;
  /**
   * The actions to run when the map is ready.
   * <p>
   * This is used when the wine screen is opened for the first time and the map has not yet been
   * loaded
   * </p>
   */
  private final List<Runnable> onReadyActions = new ArrayList<>();
  /**
   * The JavaScript program which is created during the Java run time.
   */
  private JSObject javaScriptConnector;

  /**
   * Constructs the controller for the map.
   *
   * @param webEngine the web engine for the map view
   */
  LeafletOsmController(WebEngine webEngine) {
    this.webEngine = webEngine;
  }

  /**
   * Load the HTML map code and send it to the web engine. A state listener is created which listens
   * for a successful state change. On successful state change, the initMap command is send to
   * JavaScript to setup the map.
   */
  public void initMap() {
    // read the content from the HTML file
    InputStream input = getClass().getResourceAsStream("/html/leaflet_osm_map.html");
    if (input == null) {
      log.error("Failed to read contents of \"map/leaflet_osm_map.html\"");
      return;
    }
    String content = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))
        .lines()
        .collect(Collectors.joining("\n"));
    webEngine.setJavaScriptEnabled(true);
    webEngine.loadContent(content);

    // add a listener to the web console in order to view log messages which are sent in JavaScript
    WebConsoleListener.setDefaultListener((view, message, lineNumber, sourceId) ->
        log.info(
            String.format("Map WebView console log line: %d, message : %s", lineNumber, message)));

    // wait until the web engine has successfully loaded the HTML then call the JavaScript function
    // initMap using the JavaScript connector
    webEngine.getLoadWorker().stateProperty().addListener(
        (ov, oldState, newState) -> {
          if (newState == Worker.State.SUCCEEDED) {
            // set the javascript bridge and call the initMap function in JavaScript
            // to display the map
            webEngine.executeScript("window");
            javaScriptConnector = (JSObject) webEngine.executeScript("jsConnector");
            javaScriptConnector.call("initMap");

            if (!onReadyActions.isEmpty()) {
              onReadyActions.forEach(Runnable::run);
              onReadyActions.clear();
            }
          }
        });
  }

  /**
   * Calls the enableToggleButtons function in JavaScript.
   * <p>
   *     The JavaScript function will enable the buttons to toggle showing the heatmap and markers
   *     buttons.
   * </p>
   */
  public void enableToggleButtons() {
    javaScriptConnector.call("enableToggleButtons");
  }

  /**
   * Calls the clearMarkers function in JavaScript.
   * <p>
   * The JavaScript function will clear all of the markers on the map
   * </p>
   */
  public void clearWineMarkers() {
    javaScriptConnector.call("clearMarkers");
  }

  /**
   * Calls the clearHeatmap function in JavaScript.
   * <p>
   * The JavaScript function will clear the heatmap
   * </p>
   */
  public void clearHeatmap() {
    javaScriptConnector.call("clearHeatMap");
  }

  /**
   * Calls the addWineMarker function in JavaScript.
   * <p>
   * The JavaScript function will create a new marker of the wine and display it at the wines
   * geolocation.
   * </p>
   *
   * @param wine the wine for which the marker should represent.
   */
  public void addWineMarker(Wine wine) {
    GeoLocation geoLocation = wine.getGeoLocation();
    javaScriptConnector.call("addWineMarker", wine.getTitle(), wine.getRegion(), wine.getColor(),
        geoLocation.getLatitude(), geoLocation.getLongitude());
  }

  /**
   * Calls the addVineyardMarker function in JavaScript. If the focus parameter is true, the map
   * will zoom into this vineyard marker.
   * <p>
   * The JavaScript function will create a new marker of the vineyard and display it at the
   * vineyards geolocation.
   * </p>
   *
   * @param vineyard the vineyard for which the marker should represent.
   * @param focus    if the map should zoom and focus on the created vineyard marker.
   */
  public void addVineyardMaker(Vineyard vineyard, boolean focus) {
    GeoLocation geoLocation = vineyard.getGeoLocation();
    javaScriptConnector.call("addVineyardMarker", geoLocation.getLatitude(),
        geoLocation.getLongitude(), vineyard.getName(), vineyard.getAddress());
    if (focus) {
      javaScriptConnector.call("setView", geoLocation.getLatitude(),
          geoLocation.getLongitude());
    }
  }

  /**
   * Calls the addRoute function in JavaScript.
   * <p>
   * The JavaScript function will decode the geometry string into a polyline and display it on the
   * map.
   * </p>
   *
   * @param geometry The encoded polyline geometry.
   */
  public void addRoute(String geometry) {
    javaScriptConnector.call("displayRoute", geometry);
  }

  /**
   * Adds an on ready action.
   * <p>
   * If the map has loaded, the runnable param can be ran straight away. If the map has not loaded,
   * the runnable is stored and will be executed when the map has loaded
   * </p>
   *
   * @param runnable the action to be preformed when the map is loaded
   */
  public void runOrQueueWhenReady(Runnable runnable) {
    if (javaScriptConnector != null) {
      runnable.run();
      return;
    }
    onReadyActions.add(runnable);
  }
}
