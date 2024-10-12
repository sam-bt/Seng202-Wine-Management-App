package seng202.team6.managers;

import java.io.IOException;
import java.util.function.Supplier;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Builder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team6.enums.PopupType;
import seng202.team6.gui.AdminController;
import seng202.team6.gui.ConsumptionController;
import seng202.team6.gui.Controller;
import seng202.team6.gui.DetailedVineyardViewController;
import seng202.team6.gui.DetailedWineViewController;
import seng202.team6.gui.ListScreenController;
import seng202.team6.gui.LoginController;
import seng202.team6.gui.MainController;
import seng202.team6.gui.NotesController;
import seng202.team6.gui.RegisterController;
import seng202.team6.gui.SettingsController;
import seng202.team6.gui.SocialController;
import seng202.team6.gui.TourPlanningController;
import seng202.team6.gui.UpdatePasswordController;
import seng202.team6.gui.VineyardsController;
import seng202.team6.gui.WineCompareController;
import seng202.team6.gui.WineImportController;
import seng202.team6.gui.WineScreenController;
import seng202.team6.gui.popup.AddToListPopupController;
import seng202.team6.gui.popup.AddToTourPopupController;
import seng202.team6.gui.popup.CreateListPopupController;
import seng202.team6.gui.popup.DeleteListPopupController;
import seng202.team6.gui.popup.GeneralPopupController;
import seng202.team6.gui.popup.ReviewViewPopupController;
import seng202.team6.gui.popup.UserSearchPopupController;
import seng202.team6.gui.popup.UserViewPopupController;
import seng202.team6.gui.popup.WineReviewPopupController;
import seng202.team6.gui.wrapper.FxWrapper;
import seng202.team6.model.User;
import seng202.team6.model.Vineyard;
import seng202.team6.model.Wine;
import seng202.team6.model.WineList;
import seng202.team6.model.WineReview;
import seng202.team6.service.WineListService;
import seng202.team6.service.WineReviewsService;
import seng202.team6.util.WineState;

/**
 * Manager for interacting with the GUI.
 */
public class GuiManager {

  private final Logger log = LogManager.getLogger(getClass());
  private final FxWrapper wrapper;
  private MainController mainController;
  private String currentScreenFxml;
  private ManagerContext managerContext;

  /**
   * Constructs a GUIManager.
   *
   * @param wrapper fxwrapper
   */
  public GuiManager(FxWrapper wrapper) {
    this.wrapper = wrapper;
  }

  /**
   * Sets the main controller.
   * <p>
   * This class requires main controller and main controller requires this. Resolve the circular
   * dependency.
   * </p>
   *
   * @param mainController main controller
   */
  public void setMainController(MainController mainController) {
    this.mainController = mainController;
  }

  /**
   * Sets the manager context.
   */
  public void setManagerContext(ManagerContext managerContext) {
    this.managerContext = managerContext;
  }

  /**
   * Enables or disables the navigation menu bar.
   *
   * @param disable true to disable the menu bar, false to enable it
   */
  public void disableNavigation(boolean disable) {
    mainController.disableNavigation(disable);
  }

  /**
   * Updates the navigation menu based on the current authentication state of the user. If the user
   * is authenticated, the profile menu and settings options are shown. If the user is not
   * authenticated, the login and registration options are displayed.
   */
  public void updateNavigation() {
    mainController.updateNavigation();
  }

  /**
   * Gets if screen is disabled.
   *
   * @return if currently disabled
   */
  public boolean isDisabled() {
    return mainController.isDisabled();
  }

  /**
   * Switches the current scene.
   *
   *<p>
   *   Scenes with fxml paths equal to the currently loaded one are skipped
   *</p>
   *
   * @param fxml    fxml resource path
   * @param title   window title
   * @param builder controller builder
   */
  public void switchScene(String fxml, String title, Builder<?> builder) {
    if (currentScreenFxml != null && currentScreenFxml.equals(fxml)) {
      log.info("Skipped loading {} as it is already open", fxml);
      return;
    }

    Parent parent = loadFxml(fxml, builder, mainController.getPageContent());
    if (parent != null) {
      wrapper.setWindowTitle(title);
      currentScreenFxml = fxml;
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
      mainController.getPageContent().getChildren().add(parent);
      if (loader.getController() instanceof Controller controller) {
        controller.init();
      }
      AnchorPane popupContent = mainController.getPopupContent();
      popupContent.getChildren().add(parent);
      popupContent.setVisible(true);
      popupContent.setDisable(false);

      AnchorPane popupActionBlocker = mainController.getPopupActionBlocker();
      popupActionBlocker.setVisible(true);
      popupActionBlocker.setDisable(false);
    } catch (IOException e) {
      log.error("Failed to load screen {}", fxml, e);
    }
  }

  /**
   * Launches the wine screen.
   */
  public void openWineScreen() {
    switchScene("/fxml/wine_screen.fxml", "Wine Information",
        () -> new WineScreenController(managerContext));
  }

  /**
   * Launches the wine screen based on a previous state.
   *
   * @param state previous state
   */
  public void openWineScreen(WineState state) {
    switchScene("/fxml/wine_screen.fxml", "Wine Information",
        () -> new WineScreenController(managerContext, state));
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
   * Launches the wine compare screen.
   */
  public void openWineCompareScreen() {
    switchScene("/fxml/wine_compare.fxml", "Wine Compare",
        () -> new WineCompareController(managerContext, null, null));
  }

  /**
   * Launches the wine compare screen with the specified wine.
   *
   * @param leftWine  The wine to be shown on the left side of the wine compare.
   * @param rightWine The wine to be shown on the right side of the wine compare.
   */
  public void openWineCompareScreen(Wine leftWine, Wine rightWine) {
    switchScene("/fxml/wine_compare.fxml", "Wine Compare",
        () -> new WineCompareController(managerContext, leftWine, rightWine));
  }

  /**
   * Launches the list screen.
   */
  public void openListScreen() {
    switchScene("/fxml/list_screen.fxml", "My Lists",
        () -> new ListScreenController(managerContext));
  }

  /**
   * Launches the notes screen.
   */
  public void openNotesScreen() {
    switchScene("/fxml/notes_screen.fxml", "My Notes",
        () -> new NotesController(managerContext));
  }

  /**
   * Launches the consumption screen.
   */
  public void openConsumptionScreen() {
    switchScene("/fxml/consumption_screen.fxml", "Consumption",
        () -> new ConsumptionController(managerContext));
  }

  /**
   * Launches the vineyards screen.
   */
  public void openVineyardsScreen() {
    switchScene("/fxml/vineyards_screen.fxml", "Vineyards",
        () -> new VineyardsController(managerContext));
  }

  /**
   * Launches the detailed wine view.
   *
   * @param vineyard         the vineyard to view
   * @param backButtonAction action to run when back button is pressed
   */
  public void openDetailedVineyardView(Vineyard vineyard, Runnable backButtonAction) {
    switchScene("/fxml/detailed_vineyard_view.fxml", "Detailed Wine View",
        () -> new DetailedVineyardViewController(managerContext, vineyard,
            backButtonAction));
  }

  /**
   * Launches the tour planning screen.
   */
  public void openTourPlanningScreen() {
    switchScene("/fxml/tour_planning_screen.fxml", "Tour Planning",
        () -> new TourPlanningController(managerContext));
  }

  /**
   * Launches the social screen.
   */
  public void openSocialScreen() {
    switchScene("/fxml/social_screen.fxml", "Social",
        () -> new SocialController(managerContext));
  }

  /**
   * Launches the admin screen.
   */
  public void openAdminScreen() {
    switchScene("/fxml/admin_screen.fxml", "Register",
        () -> new AdminController(managerContext));
  }

  /**
   * Launches the login screen.
   */
  public void openLoginScreen() {
    switchScene("/fxml/login_screen.fxml", "Login",
        () -> new LoginController(managerContext));
  }

  /**
   * Launches the register screen.
   */
  public void openRegisterScreen() {
    switchScene("/fxml/register_screen.fxml", "Register",
        () -> new RegisterController(managerContext));
  }

  /**
   * Launches the settings screen.
   */
  public void openSettingsScreen() {
    switchScene("/fxml/settings_screen.fxml", "Settings",
        () -> new SettingsController(managerContext));
  }

  /**
   * Launches the update password screen.
   */
  public void openUpdatePasswordScreen() {
    switchScene("/fxml/update_password_screen.fxml", "Update Password",
        () -> new UpdatePasswordController(managerContext));
  }

  /**
   * Displays a popup of the error type. The popup is displayed on the screen, and the controller
   * for the popup is returned to the caller for further customization.
   *
   * @return The ErrorPopupController associated with the displayed error popup.
   */
  public GeneralPopupController showErrorPopup() {
    return showPopup(PopupType.ERROR);
  }

  /**
   * Displays a popup of the none type.
   *
   * @return The GeneralPopupController associated with the displayed popup
   */
  public GeneralPopupController showPopup() {
    return showPopup(PopupType.NONE);
  }

  /**
   * Displays a popup of the specified type. The popup is displayed on the screen, and the
   * controller for the popup is returned to the caller for further customization.
   *
   * @param popupType The type of the popup
   * @return The GeneralPopupController associated with the displayed popup
   */
  private GeneralPopupController showPopup(PopupType popupType) {
    GeneralPopupController popupController = new GeneralPopupController(managerContext, popupType);
    openPopup("/fxml/popup/general_popup.fxml", () -> popupController);
    return popupController;
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
   * Launches the user search popup.
   */
  public void openUserSearchPopup() {
    openPopup("/fxml/popup/user_search_popup.fxml",
        () -> new UserSearchPopupController(managerContext));
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
   * Launches the load import screen under a given node.
   *
   * @param parent node to add to
   * @return node that was added
   */
  public Parent loadImportWineScreen(Pane parent) {
    return loadFxml("/fxml/wine_import_screen.fxml",
        () -> new WineImportController(managerContext), parent);
  }

  /**
   * Closes the popup.
   */
  public void closePopup() {
    mainController.closePopup();
  }
}
