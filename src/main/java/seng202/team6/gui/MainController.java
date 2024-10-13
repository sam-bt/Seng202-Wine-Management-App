package seng202.team6.gui;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import seng202.team6.managers.AuthenticationManager;
import seng202.team6.managers.GuiManager;
import seng202.team6.managers.ManagerContext;

/**
 * Main controller from where other scenes are embedded.
 */
public class MainController extends Controller {
  // page content
  @FXML
  private AnchorPane pageContent;
  @FXML
  private AnchorPane popupActionBlocker;
  @FXML
  private AnchorPane popupContent;
  @FXML
  private BorderPane loadingSpinnerPane;

  @FXML
  private MenuBar menuBar;

  // wines submenu
  @FXML
  private VBox winesMenuGraphic;
  @FXML
  private VBox winesSubmenuContainer;
  @FXML
  private HBox viewWinesButton;
  @FXML
  private HBox compareWinesButton;

  // profile submenu
  @FXML
  private Menu profileMenu;
  @FXML
  private VBox profileMenuGraphic;
  @FXML
  private VBox profileSubmenuContainer;
  @FXML
  private HBox listsButton;
  @FXML
  private HBox notesButton;
  @FXML
  private HBox consumptionButton;

  // vineyard submenu
  @FXML
  private Menu vineyardsDropdownMenu;
  @FXML
  private VBox vineyardsMenuGraphic;
  @FXML
  private VBox vineyardsSubmenuContainer;
  @FXML
  private HBox viewVineyardsButton;
  @FXML
  private HBox planTourButton;

  // menu buttons
  @FXML
  private VBox vineyardsButton;
  @FXML
  private VBox socialButton;
  @FXML
  private VBox adminButton;

  // menu buttons that toggle between being on the screen and not
  @FXML
  private Menu vineyardsMenu;
  @FXML
  private Menu adminMenu;

  // authentication buttons
  @FXML
  private Button loginButton;
  @FXML
  private Button registerButton;

  private String currentScreenFxml;

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
    // mouse enter events for the wines, vineyards and profile which shows the corresponding submenu
    winesMenuGraphic.setOnMouseEntered(event ->
        showSubmenu(winesSubmenuContainer, winesMenuGraphic));
    profileMenuGraphic.setOnMouseEntered(event ->
        showSubmenu(profileSubmenuContainer, profileMenuGraphic));
    vineyardsMenuGraphic.setOnMouseEntered(event -> {
      // only show the submenu if they are authenticated
      if (managerContext.getAuthenticationManager().isAuthenticated()) {
        showSubmenu(vineyardsSubmenuContainer, vineyardsMenuGraphic);
      }
    });

    // mouse exit events which checks if the cursor has left either the button or the buttons
    // submenu
    EventHandler<MouseEvent> profileExitEvent = event ->
        hideSubmenuIfNotInside(profileSubmenuContainer, profileMenuGraphic, event.getScreenX(),
            event.getScreenY());
    profileMenuGraphic.setOnMouseExited(profileExitEvent);
    profileSubmenuContainer.setOnMouseExited(profileExitEvent);

    EventHandler<MouseEvent> vineyardsExitEvent = event ->
        hideSubmenuIfNotInside(vineyardsSubmenuContainer, vineyardsMenuGraphic, event.getScreenX(),
            event.getScreenY());
    vineyardsMenuGraphic.setOnMouseExited(vineyardsExitEvent);
    vineyardsSubmenuContainer.setOnMouseExited(vineyardsExitEvent);

    EventHandler<MouseEvent> winesExitEvent = event ->
        hideSubmenuIfNotInside(winesSubmenuContainer, winesMenuGraphic, event.getScreenX(),
            event.getScreenY());
    winesMenuGraphic.setOnMouseExited(winesExitEvent);
    winesSubmenuContainer.setOnMouseExited(winesExitEvent);

    // disable the submenus when the navigation is first loaded
    hideSubmenu(winesSubmenuContainer);
    hideSubmenu(profileSubmenuContainer);
    hideSubmenu(vineyardsSubmenuContainer);
    initButtons();

    updateNavigation();
    managerContext.getGuiManager().openWineScreen();
  }

  /**
   * Shows the submenu and positioning it directly below the parent menu graphic.
   *
   * @param submenu           the submenu to be shown
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
   * @param submenu           the submenu to be hidden
   * @param parentMenuGraphic the parent menu graphic associated with the submenu
   * @param mouseX            the current X coordinate of the mouse
   * @param mouseY            the current Y coordinate of the mouse
   */
  private void hideSubmenuIfNotInside(VBox submenu, VBox parentMenuGraphic, double mouseX,
      double mouseY) {
    if (!isMouseInsideSubmenu(submenu, parentMenuGraphic, mouseX, mouseY)) {
      hideSubmenu(submenu);
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
   * @param submenu           the submenu to check
   * @param parentMenuGraphic the parent menu graphic to check
   * @param mouseX            the current X coordinate of the mouse
   * @param mouseY            the current Y coordinate of the mouse
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
   * Updates the navigation menu based on the current authentication state of the user. If the user
   * is authenticated, the profile menu and settings options are shown. If the user is not
   * authenticated, the login and registration options are displayed.
   */
  public void updateNavigation() {
    AuthenticationManager authenticationManager = managerContext.getAuthenticationManager();
    GuiManager guiManager = managerContext.getGuiManager();
    if (authenticationManager.isAuthenticated()) {
      addIfNotPresent(profileMenu, -1);
      loginButton.setText("Settings");
      registerButton.setText("Logout");
      loginButton.setOnMouseClicked(event -> guiManager.openSettingsScreen());
      registerButton.setOnMouseClicked(event -> logout());

      // replace the vineyards button with a dropdown
      menuBar.getMenus().remove(vineyardsMenu);
      addIfNotPresent(vineyardsDropdownMenu, 1);
    } else {
      menuBar.getMenus().remove(profileMenu);
      loginButton.setText("Login");
      registerButton.setText("Register");
      loginButton.setOnMouseClicked(event -> guiManager.openLoginScreen());
      registerButton.setOnMouseClicked(event -> guiManager.openRegisterScreen());
      menuBar.getMenus().remove(vineyardsDropdownMenu);
      addIfNotPresent(vineyardsMenu, 1);
    }

    if (authenticationManager.isAdmin()) {
      addIfNotPresent(adminMenu, -1);
    } else {
      menuBar.getMenus().remove(adminMenu);
    }
  }

  /**
   * Adds a menu to the menu bar if it is not already present.
   *
   * @param menu the menu to be added
   */
  private void addIfNotPresent(Menu menu, int index) {
    if (!menuBar.getMenus().contains(menu)) {
      if (index == -1) {
        menuBar.getMenus().add(menu);
      } else {
        menuBar.getMenus().add(index, menu);
      }
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

  public AnchorPane getPageContent() {
    return pageContent;
  }

  public AnchorPane getPopupContent() {
    return popupContent;
  }

  public AnchorPane getPopupActionBlocker() {
    return popupActionBlocker;
  }

  /**
   * Logout the current user.
   */
  @FXML
  public void logout() {
    managerContext.getAuthenticationManager().logout();
    updateNavigation();
    managerContext.getGuiManager().openWineScreen();
  }

  /**
   * Gets if screen is disabled.
   *
   * @return if currently disabled
   */
  public boolean isDisabled() {
    return disabled;
  }

  private void initButtons() {
    GuiManager guiManager = managerContext.getGuiManager();
    // wines submenu
    viewWinesButton.setOnMouseClicked(event -> guiManager.openWineScreen());
    compareWinesButton.setOnMouseClicked(event -> guiManager.openWineCompareScreen());

    // profile submenu
    listsButton.setOnMouseClicked(event -> guiManager.openListScreen());
    notesButton.setOnMouseClicked(event -> guiManager.openNotesScreen());
    consumptionButton.setOnMouseClicked(event -> guiManager.openConsumptionScreen());

    // vineyards submenu
    viewVineyardsButton.setOnMouseClicked(event -> guiManager.openVineyardsScreen());
    planTourButton.setOnMouseClicked(event -> guiManager.openTourPlanningScreen());

    // menu buttons
    vineyardsButton.setOnMouseClicked(event -> guiManager.openVineyardsScreen());
    socialButton.setOnMouseClicked(event -> guiManager.openSocialScreen());
    adminButton.setOnMouseClicked(event -> guiManager.openAdminScreen());
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
   * Toggles the loading overlay indicator.
   *
   * @param show if the loading indicator should be shown or not
   */
  public void showLoadingIndicator(boolean show) {
    popupActionBlocker.setDisable(!show);
    popupActionBlocker.setVisible(show);
    loadingSpinnerPane.setDisable(!show);
    loadingSpinnerPane.setVisible(show);
  }

}
