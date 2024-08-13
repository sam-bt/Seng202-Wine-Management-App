package seng202.team0.gui;

import seng202.team0.managers.ManagerContext;

/**
 * Generic controller class parent
 */
public abstract class Controller {
  //FIXME ManagerContext needs to reference something
  ManagerContext managerContext;

  /**
   * Constructor
   * @param context Manager context (MORE DETAIL REQUIRED HERE!)
   */
  //FIXME ManagerContext needs to reference something
  public Controller(ManagerContext context){
    this.managerContext = context;
    //TODO Implement me!
  }

  /**
   * Should be called whenever a controller is popped from the stack
   * @return
   */
  public abstract Object onPopped();

  /**
   * Should be called when the child of the controller is popped
   */
  public abstract void onChildContextPopped(Object obj);

}
