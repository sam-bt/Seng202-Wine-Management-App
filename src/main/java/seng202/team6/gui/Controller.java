package seng202.team6.gui;

import seng202.team6.managers.ManagerContext;

/**
 * Generic controller base class.
 */
public abstract class Controller {

  /**
   * Shared state between controllers.
   */
  protected ManagerContext managerContext;

  /**
   * Constructor.
   *
   * @param context Manager context
   */
  public Controller(ManagerContext context) {
    this.managerContext = context;
  }

  /**
   * Called after the constructor for when fxml is loaded.
   */
  public void init() {
  }

}
