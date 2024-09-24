package seng202.team6.gui;

import seng202.team6.managers.ManagerContext;

/**
 * Generic controller class parent
 */
public abstract class Controller {

  protected ManagerContext managerContext;

  /**
   * Constructor
   *
   * @param context Manager context (MORE DETAIL REQUIRED HERE!)
   */
  public Controller(ManagerContext context) {
    this.managerContext = context;
  }

  /**
   * Called after the constructor for when fxml is loaded
   */
  public void init() {
  }

}
