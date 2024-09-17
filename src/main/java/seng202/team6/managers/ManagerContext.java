package seng202.team6.managers;

/**
 * ManagerContext is simply a bag of managers. Members are public because of this
 *
 * @author Angus McDougall
 */
public class ManagerContext {

  // Managers
  final public DatabaseManager databaseManager;

  final public GUIManager GUIManager;

  /**
   * Constructor for ManagerContext
   *
   * @param databaseManager       database manager
   * @param GUIManager            interface manager
   */
  public ManagerContext(
      DatabaseManager databaseManager,
      GUIManager GUIManager) {
    this.databaseManager = databaseManager;
    this.GUIManager = GUIManager;
  }
}
