package seng202.team6.gui;

import java.io.IOException;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import javafx.util.Builder;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team6.gui.popup.AddToListPopupController;
import seng202.team6.gui.popup.AddToTourPopupController;
import seng202.team6.gui.popup.CreateListPopupController;
import seng202.team6.gui.popup.DeleteListPopupController;
import seng202.team6.gui.popup.ReviewViewPopupController;
import seng202.team6.gui.popup.UserViewPopupController;
import seng202.team6.gui.popup.VineyardTourPopupController;
import seng202.team6.gui.popup.WineReviewPopupController;
import seng202.team6.managers.ManagerContext;
import seng202.team6.model.User;
import seng202.team6.model.Vineyard;
import seng202.team6.model.VineyardTour;
import seng202.team6.model.Wine;
import seng202.team6.model.WineList;
import seng202.team6.model.WineReview;
import seng202.team6.service.VineyardToursService;
import seng202.team6.service.WineListService;
import seng202.team6.service.WineReviewsService;

/**
 * Main controller from where other scenes are embedded.
 */
public class MainController extends Controller {

  private final Logger log = LogManager.getLogger(getClass());

  @FXML
  private AnchorPane pageContent;

  @FXML
  private AnchorPane popupActionBlocker;

  @FXML
  private AnchorPane popupContent;

  @FXML
  private MenuBar menuBar;

  @FXML
  private Menu profileMenu;

  @FXML
  private Menu adminMenu;

  @FXML
  private VBox profileMenuGraphic;

  @FXML
  private VBox profileContextMenu;

  @FXML
  private Button loginButton;

  @FXML
  private Button registerButton;

  private boolean disabled = false;

  /**
   * Constructor.
   *
   * @param managerContext manager context
   */
  public MainController(ManagerContext managerContext) {
    super(managerContext);

    // This is an ugly circular dependency. It is easier to resolve here
    managerContext.getGuiManager().setMainController(this);
  }

  /**
   * Called to init this controller after set up.
   */
  @Override
  public void init() {
    profileMenuGraphic.setOnMouseEntered(event -> showProfileMenuContext());
    profileMenuGraphic.setOnMouseExited(event -> {
      hideProfileMenuContextIfNotInside(event.getScreenX(), event.getScreenY());
    });
    profileContextMenu.setOnMouseExited(event -> {
      hideProfileMenuContextIfNotInside(event.getScreenX(), event.getScreenY());
    });
    profileContextMenu.setDisable(true);
    profileContextMenu.setVisible(false);

    updateNavigation();
    openWineScreen();
  }

  /**
   * Displays the profile context menu near the profile graphic node. The position of the context
   * menu is determined based on the layout of the graphic node.
   */
  private void showProfileMenuContext() {
    VBox graphicNode = (VBox) profileMenuGraphic;
    Point2D position = graphicNode.localToScene(0, graphicNode.getLayoutBounds().getMaxY());

    profileContextMenu.setLayoutX(position.getX());
    profileContextMenu.setLayoutY(position.getY());
    profileContextMenu.setPrefWidth(graphicNode.getWidth());
    profileContextMenu.setDisable(false);
    profileContextMenu.setVisible(true);
  }

  /**
   * Hides the profile context menu if the mouse is not inside the menu or the graphic node.
   *
   * @param mouseX the X coordinate of the mouse cursor
   * @param mouseY the Y coordinate of the mouse cursor
   */
  private void hideProfileMenuContextIfNotInside(double mouseX, double mouseY) {
    if (!isMouseInsideComponents(mouseX, mouseY)) {
      profileContextMenu.setDisable(true);
      profileContextMenu.setVisible(false);
    }
  }

  /**
   * Checks if the mouse cursor is inside the graphic node or the profile context menu.
   *
   * @param mouseX the X coordinate of the mouse cursor
   * @param mouseY the Y coordinate of the mouse cursor
   * @return true if the mouse is inside the components, false otherwise
   */
  private boolean isMouseInsideComponents(double mouseX, double mouseY) {
    VBox graphicNode = (VBox) profileMenuGraphic;
    Point2D graphicScreenPos = graphicNode.localToScreen(0, 0);
    double minX = graphicScreenPos.getX();
    double maxX = minX + profileMenuGraphic.getWidth();
    double minY = graphicScreenPos.getY();
    double maxY = minY + profileMenuGraphic.getHeight() + profileContextMenu.getHeight();

    return mouseX > minX && mouseX < maxX && mouseY > minY && mouseY < maxY;
  }

  /**
   * Updates the navigation menu based on the current authentication state of the user.
   * If the user is authenticated, the profile menu and settings options are shown.
   * If the user is not authenticated, the login and registration options are displayed.
   */
  public void updateNavigation() {
    if (managerContext.getAuthenticationManager().isAuthenticated()) {
      addIfNotPresent(profileMenu);
      loginButton.setText("Settings");
      registerButton.setText("Logout");
      loginButton.setOnMouseClicked(event -> openSettingsScreen());
      registerButton.setOnMouseClicked(event -> logout());
    } else {
      menuBar.getMenus().remove(profileMenu);
      loginButton.setText("Login");
      registerButton.setText("Register");
      loginButton.setOnMouseClicked(event -> openLoginScreen());
      registerButton.setOnMouseClicked(event -> openRegisterScreen());
    }

    if (managerContext.getAuthenticationManager().isAdmin()) {
      addIfNotPresent(adminMenu);
    } else {
      menuBar.getMenus().remove(adminMenu);
    }
  }

  /**
   * Adds a menu to the menu bar if it is not already present.
   *
   * @param menu the menu to be added
   */
  private void addIfNotPresent(Menu menu) {
    if (!menuBar.getMenus().contains(menu)) {
      menuBar.getMenus().add(menu);
    }
  }

  /**
   * Enables or disables the navigation menu bar.
   *
   * @param disable true to disable the menu bar, false to enable it
   */
  public void disableNavigation(boolean disable) {
    menuBar.setDisable(disable);
  }

  /**
   * Switches the current scene.
   *
   * @param fxml    fxml resource path
   * @param title   window title
   * @param builder controller builder
   */
  public void switchScene(String fxml, String title, Builder<?> builder) {
    Parent parent = loadFxml(fxml, builder, pageContent);
    if (parent != null) {
      managerContext.getGuiManager().setWindowTitle(title);
    }
  }

  /**
   * Loads a scene from a fxml resource.
   *
   * @param fxml        fxml resource path
   * @param builder     controller builder
   * @param parentToAdd parent to add scene to
   * @return added node
   */
  private Parent loadFxml(String fxml, Builder<?> builder, Pane parentToAdd) {
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


  /**
   * Opens a popup.
   *
   * @param fxml    fxml resource path
   * @param builder controller builder
   */
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

  /**
   * Logout the current user.
   */
  @FXML
  public void logout() {
    managerContext.getAuthenticationManager().logout();
    updateNavigation();
    openWineScreen();
  }

  /**
   * Gets if screen is disabled.
   *
   * @return if currently disabled
   */
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
   * Launches the register screen.
   */
  @FXML
  public void openRegisterScreen() {
    switchScene("/fxml/register_screen.fxml", "Register",
        () -> new RegisterController(managerContext));
  }

  /**
   * Launches the admin screen.
   */
  @FXML
  public void openAdminScreen() {
    switchScene("/fxml/admin_screen.fxml", "Register", () -> new AdminController(managerContext));
  }

  /**
   * Launches the settings screen.
   */
  @FXML
  public void openSettingsScreen() {
    switchScene("/fxml/settings_screen.fxml", "Register",
        () -> new SettingsController(managerContext));
  }

  /**
   * Launches the update password screen.
   */
  @FXML
  public void openUpdatePasswordScreen() {
    switchScene("/fxml/update_password_screen.fxml", "Register",
        () -> new UpdatePasswordController(managerContext));
  }

  /**
   * Launches the notes screen.
   */
  @FXML
  void openNotesScreen() {
    switchScene("/fxml/notes_screen.fxml", "Notes",
        () -> new NotesController(managerContext));
  }

  /**
   * Launches the detailed wine view.
   *
   * @param wine             wine
   * @param backButtonAction action to run when back button is pressed
   */
  public void openDetailedWineView(Wine wine, Runnable backButtonAction) {
    switchScene("/fxml/detailed_wine_view.fxml", "Detailed Wine View",
        () -> new DetailedWineViewController(managerContext, wine, backButtonAction));
  }

  /**
   * Launches the detailed wine view.
   *
   * @param vineyard         the vineyard to view
   * @param backButtonAction action to run when back button is pressed
   */
  public void openDetailedVineyardView(Vineyard vineyard, Runnable backButtonAction) {
    switchScene("/fxml/detailed_vineyard_view.fxml", "Detailed Wine View",
        () -> new DetailedVineyardViewController(managerContext, vineyard, backButtonAction));
  }

  /**
   * Launches the user profile popup.
   *
   * @param user the users profile to open
   */
  public void openUserProfilePopup(User user) {
    openPopup("/fxml/popup/user_view_popup.fxml",
        () -> new UserViewPopupController(managerContext, user));
  }

  /**
   * Launches the social screen.
   */
  @FXML
  public void openSocialScreen() {
    switchScene("/fxml/social_screen.fxml", "Social",
        () -> new SocialController(managerContext));
  }

  /**
   * Launches the vineyards screen.
   */
  @FXML
  public void openVineyardsScreen() {
    switchScene("/fxml/vineyards_screen.fxml", "Vineyards",
        () -> new VineyardsController(managerContext));
  }

  /**
   * Launches the tour planning screen.
   */
  @FXML
  public void openTourPlanningScreen() {
    switchScene("/fxml/tour_planning_screen.fxml", "Tour Planning",
        () -> new TourPlanningController(managerContext));
  }

  /**
   * Launches the popup wine view.
   *
   * @param wineReviewsService wine reviews service
   */
  public void openPopupWineReview(WineReviewsService wineReviewsService) {
    openPopup("/fxml/popup/review_popup.fxml",
        () -> new WineReviewPopupController(managerContext, wineReviewsService));
  }

  /**
   * Launches the popup create list.
   *
   * @param wineListService service class for wine lists.
   */
  public void openCreateListPopUp(WineListService wineListService) {
    openPopup("/fxml/popup/create_list_popup.fxml",
        () -> new CreateListPopupController(managerContext, wineListService));
  }

  /**
   * Launches the popup delete list.
   *
   * @param wineList the wineList to delete.
   * @param wineListService service class for wine lists.
   */
  public void openDeleteListPopUp(WineList wineList, WineListService wineListService) {
    openPopup("/fxml/popup/delete_list_popup.fxml",
        () -> new DeleteListPopupController(managerContext, wineList, wineListService));
  }

  /**
   * Launches the add to list popup.
   *
   * @param wine wine
   */
  public void openAddToListPopup(Wine wine) {
    openPopup("/fxml/popup/add_to_list_popup.fxml",
        () -> new AddToListPopupController(managerContext, wine));
  }

  /**
   * Launches the consumption screen.
   */

  @FXML
  public void openConsumptionScreen() {
    switchScene("/fxml/consumption_screen.fxml", "Consumption",
        () -> new ConsumptionController(managerContext));
  }

  /**
   * Launches the popup to add the specified vineyard to a tour.
   *
   * @param vineyard The vineyard to be added to a tour.
   */
  public void openAddToTourPopup(Vineyard vineyard) {
    openPopup("/fxml/popup/add_to_tour_popup.fxml",
        () -> new AddToTourPopupController(managerContext, vineyard));
  }

  /**
   * Launches the popup to review a wine.
   *
   * @param wineReviewsService the service used for managing wine reviews.
   * @param reviewer           the user reviewing the wine.
   * @param selectedReview     the currently selected wine review, or null if creating a new
   *                           review.
   * @param wine               the wine being reviewed.
   */
  public void openPopupReviewView(WineReviewsService wineReviewsService, User reviewer,
      WineReview selectedReview, Wine wine) {
    openPopup("/fxml/popup/view_review_popup.fxml",
        () -> new ReviewViewPopupController(managerContext, wineReviewsService, reviewer,
            selectedReview, wine));
  }

  /**
   * Launches the popup to create a vineyard tour.
   *
   * @param vineyardToursService  the service used for managing vineyard tours.
   * @param modifyingVineyardTour the vineyard tour to be modified, or null if creating a new tour.
   */
  public void openVineyardTourPopup(VineyardToursService vineyardToursService,
      VineyardTour modifyingVineyardTour) {
    openPopup("/fxml/popup/create_vineyard_tour_popup.fxml",
        () -> new VineyardTourPopupController(managerContext, vineyardToursService,
            modifyingVineyardTour));
  }

  /**
   * Closes the popup.
   */
  public void closePopup() {
    popupActionBlocker.setVisible(false);
    popupActionBlocker.setDisable(true);
    popupContent.setVisible(false);
    popupContent.setDisable(true);
    popupContent.getChildren().clear();
  }

  /**
   * Launches the load import screen under a given node.
   *
   * @param parent node to add to
   * @return node that was added
   */
  public Parent loadImportWineScreen(Pane parent) {
    return loadFxml("/fxml/wine_import_screen.fxml",
        () -> new WineImportController(managerContext), parent);
  }
}
