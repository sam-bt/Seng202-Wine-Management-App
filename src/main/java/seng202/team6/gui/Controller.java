package seng202.team6.gui;

import seng202.team6.managers.ManagerContext;

/**
 * Generic controller class parent
 */
public abstract class Controller {
  ManagerContext managerContext;

  /**
   * Constructor
   * @param context Manager context (MORE DETAIL REQUIRED HERE!)
   */
  public Controller(ManagerContext context) {
    this.managerContext = context;
  }

  /**
   * Called after the constructor for when fxml is loaded
   */
  public void init(){}
  /**
   * Called to get a return value when this controller is destroyed
   * @return a value
   */
  public Object onPopped() {
    return null;
  }

  /**
   * Called to provide the value from onPopped on the successor controller
   * @param obj object from 'called' controller
   */
  public void onChildContextPopped(Object obj) {

  }

}
