package seng202.team6.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebView;
import seng202.team6.managers.ManagerContext;

public class HelpController extends Controller{

    @FXML
    private WebView webView;

    @FXML
    private AnchorPane rootPane;

    public HelpController(ManagerContext managerContext) {
        super(managerContext);
    }

    @FXML
    private void initialize() {
        String url = getClass().getResource("/html/ManualIndex.html").toExternalForm();
        webView.getEngine().setUserStyleSheetLocation(getClass().getResource("/css/helppages.css").toExternalForm());
        webView.getEngine().load(url);

    }
}
