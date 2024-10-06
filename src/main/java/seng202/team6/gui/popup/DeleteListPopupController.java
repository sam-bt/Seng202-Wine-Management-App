package seng202.team6.gui.popup;

import javafx.fxml.FXML;
import seng202.team6.gui.Controller;
import seng202.team6.managers.ManagerContext;
import seng202.team6.gui.ListScreenController;

import static com.sun.scenario.effect.impl.prism.PrEffectHelper.render;

public class DeleteListPopupController extends Controller {


    public DeleteListPopupController(ManagerContext managerContext) {
        super(managerContext);
    }
    @FXML
    public void onBackButtonClick() {
        managerContext.GUIManager.mainController.closePopup();
    }

    @FXML
    public void onDeleteListConfirmClick() {
        managerContext.GUIManager.mainController.closePopup();
    }


}