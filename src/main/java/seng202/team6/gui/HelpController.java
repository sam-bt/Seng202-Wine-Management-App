package seng202.team6.gui;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team6.managers.DatabaseManager;
import seng202.team6.managers.ManagerContext;

/**
 * Controller for the user manual page. Loads a different index if you are admin.
 */
public class HelpController extends Controller {

  private static final Logger log = LogManager.getLogger(HelpController.class);
  @FXML
  private WebView webView;

  /**
   * Constructor.
   *
   * @param managerContext the manager context
   */
  public HelpController(ManagerContext managerContext) {
    super(managerContext);
  }

  @FXML
  private void initialize() {
    try {
      String url = getClass().getResource("/html/ManualIndex.html").toExternalForm();
      if (managerContext.getAuthenticationManager().isAdmin()) {
        url = getClass().getResource("/html/AdminIndex.html").toExternalForm();
      }

      webView.getEngine()
          .setUserStyleSheetLocation(
              getClass().getResource("/css/helppages.css").toExternalForm());
      webView.getEngine().load(url);
    } catch (NullPointerException e) {
      log.info("Failed to load HTML index file");
      log.info(e.getMessage());
    }
  }
}
