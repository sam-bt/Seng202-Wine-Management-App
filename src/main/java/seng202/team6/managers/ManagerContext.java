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

  final public AuthenticationManager authenticationManager;

  /**
   * Constructor for ManagerContext
   *
   * @param databaseManager       database manager
   * @param GUIManager            interface manager
   * @param authenticationManager authentication manager
   */
  public ManagerContext(
      DatabaseManager databaseManager,
      GUIManager GUIManager,
      AuthenticationManager authenticationManager
  ) {
    this.databaseManager = databaseManager;
    this.GUIManager = GUIManager;
    this.authenticationManager = authenticationManager;
  }
}
