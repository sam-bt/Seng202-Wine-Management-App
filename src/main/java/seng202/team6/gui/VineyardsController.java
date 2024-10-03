package seng202.team6.gui;

import javafx.fxml.FXML;
import javafx.scene.layout.TilePane;
import seng202.team6.managers.ManagerContext;
import seng202.team6.model.Vineyard;
import seng202.team6.service.VineyardService;

public class VineyardsController extends Controller {
  @FXML
  TilePane vineyardsViewContainer;

  private final VineyardService vineyardService;

  /**
   * Constructs the Vineyards Controller
   *
   * @param context The manager context
   */
  public VineyardsController(ManagerContext context) {
    super(context);
    this.vineyardService = new VineyardService(context.databaseManager);
  }

  @Override
  public void init() {
    super.init();
  }

  private void createVineyardCard(Vineyard vineyard) {

  }
}
