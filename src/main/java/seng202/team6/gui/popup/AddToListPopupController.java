package seng202.team6.gui.popup;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import seng202.team6.gui.Controller;
import seng202.team6.gui.controls.container.AddRemoveCardsContainer;
import seng202.team6.managers.ManagerContext;
import seng202.team6.model.Wine;
import seng202.team6.model.WineList;
import seng202.team6.service.WineListService;

/**
 * Controller for the add to list popup.
 */
public class AddToListPopupController extends Controller {

  private final WineListService wineListService;
  private final Wine wine;
  @FXML
  ScrollPane wineListsContainer;
  private AddRemoveCardsContainer<WineList> addRemoveCardsContainer;

  /**
   * Constructs an AddToListPopupController.
   *
   * @param context the manager context.
   * @param wine    the wine to be added to a list.
   */
  public AddToListPopupController(ManagerContext context, Wine wine) {
    super(context);
    this.wine = wine;
    this.wineListService = new WineListService(getManagerContext().getAuthenticationManager(),
        context.getDatabaseManager());
    bindToWineListService();
  }

  @Override
  public void init() {
    addRemoveCardsContainer = new AddRemoveCardsContainer<>(
        wineListsContainer.viewportBoundsProperty(),
        wineListsContainer.widthProperty());
    wineListsContainer.setContent(addRemoveCardsContainer);
    wineListService.init();
  }

  @FXML
  void onBackButtonClick() {
    getManagerContext().getGuiManager().closePopup();
  }

  private void bindToWineListService() {
    wineListService.getWineLists().addListener((ListChangeListener<WineList>) change -> {
      while (change.next()) {
        if (change.wasAdded()) {
          change.getAddedSubList().forEach(wineList -> {
            addRemoveCardsContainer.add(wineList, new SimpleStringProperty(wineList.name()),
                !wineListService.isWineInList(wineList, wine),
                () -> getManagerContext().getDatabaseManager().getWineListDao()
                    .addWine(wineList, wine),
                () -> getManagerContext().getDatabaseManager().getWineListDao()
                    .removeWine(wineList, wine));
          });
        }
      }
    });
  }
}
