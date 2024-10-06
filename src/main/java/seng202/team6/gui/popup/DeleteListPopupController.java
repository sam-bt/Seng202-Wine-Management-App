package seng202.team6.gui.popup;

import javafx.fxml.FXML;
import seng202.team6.gui.Controller;
import seng202.team6.managers.ManagerContext;
import seng202.team6.model.WineList;
import seng202.team6.service.WineListService;
public class DeleteListPopupController extends Controller {
    private WineList wineListToDelete;
    private WineListService  wineListService;

    public DeleteListPopupController(ManagerContext managerContext, WineList wineList, WineListService wineListService) {
        super(managerContext);
        this.wineListToDelete = wineList;
        this.wineListService = wineListService;
    }
    @FXML
    public void onBackButtonClick() {
        managerContext.GUIManager.mainController.closePopup();
    }
    @FXML
    public void onDeleteListConfirmClick() {
        wineListService.deleteWineList(wineListToDelete);
        managerContext.GUIManager.mainController.closePopup();
    }
}