package seng202.team6.managers;

import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import seng202.team6.gui.MainController;
import seng202.team6.gui.popup.GeneralPopupController;
import seng202.team6.gui.wrapper.FxWrapper;
import seng202.team6.model.User;
import seng202.team6.model.Vineyard;
import seng202.team6.model.Wine;
import seng202.team6.model.WineList;
import seng202.team6.model.WineReview;
import seng202.team6.service.WineListService;
import seng202.team6.service.WineReviewsService;

/**
 * Manager for interacting with the GUI.
 */
public class GuiManager {

  private final FxWrapper wrapper;
  private MainController mainController;

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
   * Sets the window title.
   *
   * @param title title to set
   */
  public void setWindowTitle(String title) {
    wrapper.setWindowTitle(title);
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
   * Launches the wine screen.
   */
  public void openWineScreen() {
    mainController.openWineScreen();
  }

  /**
   * Launches the list screen.
   */
  public void openListScreen() {
    mainController.openListScreen();
  }

  /**
   * Launches the login screen.
   */
  public void openLoginScreen() {
    mainController.openLoginScreen();
  }

  /**
   * Launches the register screen.
   */
  public void openRegisterScreen() {
    mainController.openRegisterScreen();
  }

  /**
   * Launches the admin screen.
   */
  public void openAdminScreen() {
    mainController.openAdminScreen();
  }

  /**
   * Launches the settings screen.
   */
  public void openSettingsScreen() {
    mainController.openSettingsScreen();
  }

  /**
   * Launches the update password screen.
   */
  public void openUpdatePasswordScreen() {
    mainController.openUpdatePasswordScreen();
  }

  /**
   * Launches the notes screen.
   */
  public void openNotesScreen() {
    mainController.openNotesScreen();
  }

  /**
   * Launches the detailed wine view.
   *
   * @param wine             wine
   * @param backButtonAction action to run when back button is pressed
   */
  public void openDetailedWineView(Wine wine, Runnable backButtonAction) {
    mainController.openDetailedWineView(wine, backButtonAction);
  }


  /**
   * Launches the detailed wine view.
   *
   * @param vineyard         the vineyard to view
   * @param backButtonAction action to run when back button is pressed
   */
  public void openDetailedVineyardView(Vineyard vineyard, Runnable backButtonAction) {
    mainController.openDetailedVineyardView(vineyard, backButtonAction);
  }

  /**
   * Launches the user profile popup.
   *
   * @param user the users profile to open
   */
  public void openUserProfilePopup(User user) {
    mainController.openUserProfilePopup(user);
  }

  /**
   * Launches the social screen.
   */
  public void openSocialScreen() {
    mainController.openSocialScreen();
  }

  /**
   * Launches the vineyards screen.
   */
  public void openVineyardsScreen() {
    mainController.openVineyardsScreen();
  }

  /**
   * Launches the tour planning screen.
   */
  public void openTourPlanningScreen() {
    mainController.openTourPlanningScreen();
  }

  /**
   * Launches the popup wine view.
   *
   * @param wineReviewsService wine reviews service
   */
  public void openPopupWineReview(WineReviewsService wineReviewsService) {
    mainController.openPopupWineReview(wineReviewsService);
  }

  /**
   * Launches the popup create list.
   *
   * @param wineListService service class for wine lists.
   */
  public void openCreateListPopUp(WineListService wineListService) {
    mainController.openCreateListPopUp(wineListService);
  }

  /**
   * Launches the popup delete list.
   *
   * @param wineList        the wineList to delete.
   * @param wineListService service class for wine lists.
   */
  public void openDeleteListPopUp(WineList wineList, WineListService wineListService) {
    mainController.openDeleteListPopUp(wineList, wineListService);
  }

  /**
   * Launches the add to list popup.
   *
   * @param wine wine
   */
  public void openAddToListPopup(Wine wine) {
    mainController.openAddToListPopup(wine);
  }

  /**
   * Launches the consumption screen.
   */
  public void openConsumptionScreen() {
    mainController.openConsumptionScreen();
  }

  /**
   * Launches the popup to add the specified vineyard to a tour.
   *
   * @param vineyard The vineyard to be added to a tour.
   */
  public void openAddToTourPopup(Vineyard vineyard) {
    mainController.openAddToTourPopup(vineyard);
  }

  /**
   * Launches the wine compare screen.
   */
  public void openWineCompareScreen() {
    mainController.openWineCompareScreen();
  }

  /**
   * Launches the wine compare screen with the specified wine.
   *
   * @param leftWine The wine to be shown on the left side of the wine compare.
   * @param rightWine The wine to be shown on the right side of the wine compare.
   */
  public void openWineCompareScreen(Wine leftWine, Wine rightWine) {
    mainController.openWineCompareScreen(leftWine, rightWine);
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
    mainController.openPopupReviewView(wineReviewsService, reviewer, selectedReview, wine);
  }

  /**
   * Launches the load import screen under a given node.
   *
   * @param parent node to add to
   * @return node that was added
   */
  public Parent loadImportWineScreen(Pane parent) {
    return mainController.loadImportWineScreen(parent);
  }


  /**
   * Closes the popup.
   */
  public void closePopup() {
    mainController.closePopup();
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
   * Updates the navigation menu based on the current authentication state of the user.
   * If the user is authenticated, the profile menu and settings options are shown.
   * If the user is not authenticated, the login and registration options are displayed.
   */
  public void updateNavigation() {
    mainController.updateNavigation();
  }

  /**
   * Displays a popup of the error type. The popup is displayed on the screen, and the controller
   * for the popup is returned to the caller for further customization.
   *
   * @return The ErrorPopupController associated with the displayed error popup.
   */
  public GeneralPopupController showErrorPopup() {
    return mainController.showErrorPopup();
  }

  /**
   * Displays a popup of the none type.
   *
   * @return The GeneralPopupController associated with the displayed popup
   */
  public GeneralPopupController showPopup() {
    return mainController.showPopup();
  }

  /**
   * Launches the user search popup.
   */
  public void openUserSearchPopup() {
    mainController.openUserSearchPopup();
  }


}
