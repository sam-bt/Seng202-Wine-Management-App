package seng202.team6.gui;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.util.Builder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team6.gui.popup.AddToListPopupController;
import seng202.team6.gui.popup.ReviewViewPopupController;
import seng202.team6.gui.popup.WineReviewPopupController;
import seng202.team6.managers.ManagerContext;
import seng202.team6.model.User;
import seng202.team6.model.Vineyard;
import seng202.team6.model.Wine;
import seng202.team6.model.WineReview;
import seng202.team6.service.WineReviewsService;

/**
 * Main controller from where other scenes are embedded
 */
public class MainController extends Controller {

  private final Logger log = LogManager.getLogger(getClass());
  @FXML
  private AnchorPane page;

  @FXML
  private AnchorPane pageContent;

  @FXML
  private Button wineScreenButton;

  @FXML
  private Button listScreenButton;
  @FXML
  private Button adminScreenButton;

  @FXML
  private Button noteScreenButton;

  @FXML
  private Button consumptionScreenButton;

  @FXML
  private Button loginButton;

  @FXML
  private Button registerButton;

  @FXML
  private HBox navBarBox;

  @FXML
  private AnchorPane popupActionBlocker;

  @FXML
  private AnchorPane popupContent;

  private boolean disabled = false;

  /**
   * Constructor
   *
   * @param managerContext manager context
   */
  public MainController(ManagerContext managerContext) {
    super(managerContext);

    // This is an ugly circular dependency. It is easier to resolve here
    managerContext.GUIManager.setMainController(this);
  }

  public void initialize() {
    adminScreenButton.setVisible(false);
    navBarBox.getChildren().remove(listScreenButton);
    navBarBox.getChildren().add(3, listScreenButton);
    listScreenButton.setVisible(false);
    noteScreenButton.setVisible(false);
    consumptionScreenButton.setVisible(false);

    openWineScreen();
  }

  public void onLogin() {
    if (managerContext.authenticationManager.isAuthenticated()) {
      adminScreenButton.setVisible(managerContext.authenticationManager.isAdmin());
      noteScreenButton.setVisible(true);
      loginButton.setText("Settings");
      registerButton.setText("Logout");

      navBarBox.getChildren().remove(listScreenButton);
      navBarBox.getChildren().add(1, listScreenButton);
      listScreenButton.setVisible(true);
      consumptionScreenButton.setVisible(true);

      loginButton.setOnMouseClicked(event -> openSettingsScreen());
      registerButton.setOnMouseClicked(event -> logout());
    } else {
      adminScreenButton.setVisible(false);
    }
  }

  public void setDisable(Boolean status) {
    disabled = status;
    wineScreenButton.setDisable(status);
    listScreenButton.setDisable(status);
    adminScreenButton.setDisable(status);
    consumptionScreenButton.setVisible(status);
  }

  public void switchScene(String fxml, String title, Builder<?> builder) {
    Parent parent = loadFXML(fxml, builder, pageContent);
    if (parent != null) {
      managerContext.GUIManager.setWindowTitle(title);
    }
  }

  private Parent loadFXML(String fxml, Builder<?> builder, Pane parentToAdd) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
      loader.setControllerFactory(param -> builder.build());
      Parent parent = loader.load();
      parentToAdd.getChildren().clear();
      parentToAdd.getChildren().add(parent);
      if (loader.getController() instanceof Controller controller) {
        controller.init();
      }
      return parent;
    } catch (IOException e) {
      log.error("Failed to load screen {}", fxml, e);
    }
    return null;
  }


  public void openPopup(String fxml, Builder<?> builder) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
      loader.setControllerFactory(param -> builder.build());

      Parent parent = loader.load();
      pageContent.getChildren().add(parent);
      if (loader.getController() instanceof Controller controller) {
        controller.init();
      }
      popupContent.getChildren().add(parent);
      popupContent.setVisible(true);
      popupContent.setDisable(false);
      popupActionBlocker.setVisible(true);
      popupActionBlocker.setDisable(false);
    } catch (IOException e) {
      log.error("Failed to load screen {}", fxml, e);
    }
  }

  public void logout() {
    managerContext.authenticationManager.logout();
    loginButton.setText("Login");
    registerButton.setText("Register");
    loginButton.setOnMouseClicked(event -> openLoginScreen());
    registerButton.setOnMouseClicked(event -> openRegisterScreen());
    adminScreenButton.setVisible(false);
    noteScreenButton.setVisible(false);
    consumptionScreenButton.setVisible(false);

    navBarBox.getChildren().remove(listScreenButton);
    navBarBox.getChildren().add(3, listScreenButton);
    listScreenButton.setVisible(false);


    openWineScreen();
  }

  public boolean isDisabled() {
    return disabled;
  }

  /**
   * Launches the wine screen.
   */
  @FXML
  public void openWineScreen() {
    switchScene("/fxml/wine_screen.fxml", "Wine Information",
        () -> new WineScreenController(managerContext));
  }

  /**
   * Launches the list screen.
   */
  @FXML
  public void openListScreen() {
    switchScene("/fxml/list_screen.fxml", "My Lists",
        () -> new ListScreenController(managerContext));
  }

  /**
   * Launches the login screen.
   */
  @FXML
  public void openLoginScreen() {
    switchScene("/fxml/login_screen.fxml", "Login", () -> new LoginController(managerContext));
  }

  /**
   * Launches the register screen
   */
  @FXML
  public void openRegisterScreen() {
    switchScene("/fxml/register_screen.fxml", "Register",
        () -> new RegisterController(managerContext));
  }

  @FXML
  public void openAdminScreen() {
    switchScene("/fxml/admin_screen.fxml", "Register", () -> new AdminController(managerContext));
  }

  @FXML
  public void openSettingsScreen() {
    switchScene("/fxml/settings_screen.fxml", "Register",
        () -> new SettingsController(managerContext));
  }

  @FXML
  public void openUpdatePasswordScreen() {
    switchScene("/fxml/update_password_screen.fxml", "Register",
        () -> new UpdatePasswordController(managerContext));
  }

  @FXML
  void openNotesScreen() {
    switchScene("/fxml/notes_screen.fxml", "Notes",
        () -> new NotesController(managerContext));
  }

  public void openDetailedWineView(Wine wine, Runnable backButtonAction) {
    switchScene("/fxml/detailed_wine_view.fxml", "Detailed Wine View",
        () -> new DetailedWineViewController(managerContext, wine, backButtonAction));
  }

  public void openDetailedVineyardView(Vineyard vineyard, Runnable backButtonAction) {
    switchScene("/fxml/detailed_vineyard_view.fxml", "Detailed Wine View",
        () -> new DetailedVineyardViewController(managerContext, vineyard, backButtonAction));
  }

  /**
   * Launches the social screen.
   */
  @FXML
  public void openSocialScreen() {
    switchScene("/fxml/social_screen.fxml", "Social",
        () -> new SocialController(managerContext));
  }

  @FXML
  public void openVineyardsScreen() {
    switchScene("/fxml/vineyards_screen.fxml", "Vineyards",
        () -> new VineyardsController(managerContext));
  }

  @FXML
  public void openTourPlanningScreen() {
    switchScene("/fxml/tour_planning_screen.fxml", "Tour Planning",
        () -> new TourPlanningController(managerContext));
  }

  public void openPopupWineReview(WineReviewsService wineReviewsService) {
    openPopup("/fxml/popup/review_popup.fxml",
        () -> new WineReviewPopupController(managerContext, wineReviewsService));
  }

  public void openAddToListPopup(Wine wine) {
    openPopup("/fxml/popup/add_to_list_popup.fxml",
        () -> new AddToListPopupController(managerContext, wine));
  }
  @FXML
  public void openConsumptionScreen() {
    switchScene("/fxml/consumption_screen.fxml", "Consumption",
        () -> new ConsumptionController(managerContext));
  }


  public void openPopupReviewView(WineReviewsService wineReviewsService, User reviewer,
      WineReview selectedReview) {
    openPopup("/fxml/popup/view_review_popup.fxml",
        () -> new ReviewViewPopupController(managerContext, wineReviewsService, reviewer,
            selectedReview));
  }

  public void closePopup() {
    popupActionBlocker.setVisible(false);
    popupActionBlocker.setDisable(true);
    popupContent.setVisible(false);
    popupContent.setDisable(true);
    popupContent.getChildren().clear();
  }

  public Parent loadImportWineScreen(Pane parent) {
    return loadFXML("/fxml/wine_import_screen.fxml",
        () -> new WineImportController(managerContext), parent);
  }

  public void setWholePageInteractable(boolean interactable) {
    page.setDisable(!interactable);
  }
}
