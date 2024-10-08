package seng202.team6.gui;

import java.io.IOException;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Builder;
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
  private VBox vineyardsMenuGraphic;

  @FXML
  private VBox profileSubmenu;

  @FXML
  private VBox vineyardsSubmenu;

  @FXML
  private Button loginButton;

  @FXML
  private Button registerButton;

  private Screen currentScreen;

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
    // mouse enter event for the profile button which shows the profile sub menu
    profileMenuGraphic.setOnMouseEntered(event ->
        showSubmenu(profileSubmenu, profileMenuGraphic));

    // move exit events for when the profile button is open which checks if the cursor has
    // left either the profile button or the submenu
    EventHandler<MouseEvent> profileExitEvent = event ->
        hideSubmenuIfNotInside(profileSubmenu, profileMenuGraphic, event.getScreenX(),
            event.getScreenY());
    profileMenuGraphic.setOnMouseExited(profileExitEvent);
    profileSubmenu.setOnMouseExited(profileExitEvent);

    // mouse enter event for the vineyard button which shows the vineyard sub menu
    // only show the submenu if they are authenticated
    vineyardsMenuGraphic.setOnMouseEntered(event -> {
      if (managerContext.getAuthenticationManager().isAuthenticated()) {
        showSubmenu(vineyardsSubmenu, vineyardsMenuGraphic);
      }
    });

    // move exit events for when the vineyards button is open which checks if the cursor has
    // left either the profile button or the submenu. only run if they are authenticated as the menu
    // should only open when they are authenticated
    EventHandler<MouseEvent> vineyardsExitEvent = event ->
        hideSubmenuIfNotInside(vineyardsSubmenu, vineyardsMenuGraphic, event.getScreenX(),
            event.getScreenY());
    vineyardsMenuGraphic.setOnMouseExited(vineyardsExitEvent);
    vineyardsSubmenu.setOnMouseExited(vineyardsExitEvent);

    // when the menu button or a submenu button is clicked, close the menu
    profileSubmenu.getChildren().forEach(submenuItem ->
        submenuItem.addEventFilter(MouseEvent.MOUSE_CLICKED, event ->
            hideSubmenu(profileSubmenu)));
    vineyardsMenuGraphic.addEventFilter(MouseEvent.MOUSE_CLICKED, event ->
        hideSubmenu(vineyardsSubmenu));
    vineyardsSubmenu.getChildren().forEach(submenuItem ->
        submenuItem.addEventFilter(MouseEvent.MOUSE_CLICKED, event ->
            hideSubmenu(vineyardsSubmenu)));
    // disable the profile and vineyards submenu when the navigation is first loaded
    profileSubmenu.setDisable(true);
    profileSubmenu.setVisible(false);
    vineyardsSubmenu.setDisable(true);
    vineyardsSubmenu.setVisible(false);

    updateNavigation();
    openWineScreen();
  }

  /**
   * Shows the submenu and positioning it directly below the parent menu graphic.
   *
   * @param submenu the submenu to be shown
   * @param parentMenuGraphic the menu graphic that triggers the submenu
   */
  private void showSubmenu(VBox submenu, VBox parentMenuGraphic) {
    Point2D position = parentMenuGraphic.localToScene(0, parentMenuGraphic.getLayoutBounds()
        .getMaxY());
    submenu.setLayoutX(position.getX());
    submenu.setLayoutY(position.getY());
    submenu.setPrefWidth(parentMenuGraphic.getWidth());
    submenu.setDisable(false);
    submenu.setVisible(true);
  }

  /**
   * Hides the submenu if the mouse cursor is not within the bounds of either the parent menu
   * graphic or the submenu itself.
   *
   * @param submenu the submenu to be hidden
   * @param parentMenuGraphic the parent menu graphic associated with the submenu
   * @param mouseX the current X coordinate of the mouse
   * @param mouseY the current Y coordinate of the mouse
   */
  private void hideSubmenuIfNotInside(VBox submenu, VBox parentMenuGraphic, double mouseX,
      double mouseY) {
    if (!isMouseInsideSubmenu(submenu, parentMenuGraphic, mouseX, mouseY)) {
      submenu.setDisable(true);
      submenu.setVisible(false);
    }
  }

  /**
   * Hides the submenu by disabling and making it invisible.
   *
   * @param submenu the submenu to be hidden
   */
  private void hideSubmenu(VBox submenu) {
    submenu.setDisable(true);
    submenu.setVisible(false);
  }

  /**
   * Checks if the mouse cursor is inside the bounds of the submenu or the parent menu graphic,
   * accounting for some padding to avoid edge cases.
   *
   * @param submenu the submenu to check
   * @param parentMenuGraphic the parent menu graphic to check
   * @param mouseX the current X coordinate of the mouse
   * @param mouseY the current Y coordinate of the mouse
   * @return true if the mouse is inside the submenu or parent menu graphic bounds, false otherwise
   */
  private boolean isMouseInsideSubmenu(VBox submenu, VBox parentMenuGraphic, double mouseX,
      double mouseY) {
    Point2D position = parentMenuGraphic.localToScreen(0, 0);
    // add padding to each of the bounds to resolve issues with edge cases
    int padding = 10;
    double minX = position.getX() + padding;
    double maxX = minX + parentMenuGraphic.getWidth() - padding;
    double minY = position.getY() + padding;
    double maxY = minY + parentMenuGraphic.getHeight() + submenu.getHeight() - padding;

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
   * @param screen  the screen that is being switched to
   */
  public void switchScene(String fxml, String title, Builder<?> builder, Screen screen) {
    if (currentScreen != null && screen != null && currentScreen == screen) {
      log.warn("Skipped loading {} as it is already open", screen.name());
      return;
    }

    Parent parent = loadFxml(fxml, builder, pageContent);
    if (parent != null) {
      managerContext.getGuiManager().setWindowTitle(title);
      currentScreen = screen;
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
        () -> new WineScreenController(managerContext), Screen.WINE_SCREEN);
  }

  /**
   * Launches the list screen.
   */
  @FXML
  public void openListScreen() {
    switchScene("/fxml/list_screen.fxml", "My Lists",
        () -> new ListScreenController(managerContext), Screen.LISTS_SCREEN);
  }

  /**
   * Launches the login screen.
   */
  @FXML
  public void openLoginScreen() {
    switchScene("/fxml/login_screen.fxml", "Login",
        () -> new LoginController(managerContext), Screen.LOGIN_SCREEN);
  }

  /**
   * Launches the register screen.
   */
  @FXML
  public void openRegisterScreen() {
    switchScene("/fxml/register_screen.fxml", "Register",
        () -> new RegisterController(managerContext), Screen.REGISTER_SCREEN);
  }

  /**
   * Launches the admin screen.
   */
  @FXML
  public void openAdminScreen() {
    switchScene("/fxml/admin_screen.fxml", "Register",
        () -> new AdminController(managerContext), Screen.ADMIN_SCREEN);
  }

  /**
   * Launches the settings screen.
   */
  @FXML
  public void openSettingsScreen() {
    switchScene("/fxml/settings_screen.fxml", "Register",
        () -> new SettingsController(managerContext), Screen.SETTINGS_SCREEN);
  }

  /**
   * Launches the update password screen.
   */
  @FXML
  public void openUpdatePasswordScreen() {
    switchScene("/fxml/update_password_screen.fxml", "Register",
        () -> new UpdatePasswordController(managerContext), Screen.UPDATE_PASSWORD_SCREEN);
  }

  /**
   * Launches the notes screen.
   */
  @FXML
  void openNotesScreen() {
    switchScene("/fxml/notes_screen.fxml", "Notes",
        () -> new NotesController(managerContext), Screen.NOTES_SCREEN);
  }

  /**
   * Launches the detailed wine view.
   *
   * @param wine             wine
   * @param backButtonAction action to run when back button is pressed
   */
  public void openDetailedWineView(Wine wine, Runnable backButtonAction) {
    switchScene("/fxml/detailed_wine_view.fxml", "Detailed Wine View",
        () -> new DetailedWineViewController(managerContext, wine, backButtonAction), null);
  }

  /**
   * Launches the detailed wine view.
   *
   * @param vineyard         the vineyard to view
   * @param backButtonAction action to run when back button is pressed
   */
  public void openDetailedVineyardView(Vineyard vineyard, Runnable backButtonAction) {
    switchScene("/fxml/detailed_vineyard_view.fxml", "Detailed Wine View",
        () -> new DetailedVineyardViewController(managerContext, vineyard, backButtonAction),
        null);
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
        () -> new SocialController(managerContext), Screen.SOCIAL_SCREEN);
  }

  /**
   * Launches the vineyards screen.
   */
  @FXML
  public void openVineyardsScreen() {
    switchScene("/fxml/vineyards_screen.fxml", "Vineyards",
        () -> new VineyardsController(managerContext), Screen.VINEYARDS_SCREEN);
  }

  /**
   * Launches the tour planning screen.
   */
  @FXML
  public void openTourPlanningScreen() {
    switchScene("/fxml/tour_planning_screen.fxml", "Tour Planning",
        () -> new TourPlanningController(managerContext), Screen.TOUR_PLANNING_SCREEN);
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
   * @param wineList        the wineList to delete.
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
        () -> new ConsumptionController(managerContext), Screen.CONSUMPTION_SCREEN);
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
