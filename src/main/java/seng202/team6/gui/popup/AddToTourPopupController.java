package seng202.team6.gui.popup;

import seng202.team6.gui.Controller;
import seng202.team6.managers.ManagerContext;
import seng202.team6.service.VineyardService;

public class AddToTourPopupController extends Controller {

  private final VineyardService vineyardService;

  /**
   * Constructs a new AddToTourPopupController
   *
   * @param context The manager context
   */
  public AddToTourPopupController(ManagerContext context) {
    super(context);
    vineyardService = new VineyardService(context.databaseManager);
  }

  @Override
  public void init() {
    vineyardService.init();
  }
}
