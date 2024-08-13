package seng202.team0.managers;

/**
 * Manager Context (MORE DETAIL HERE!)
 */
public class ManagerContext {

  // Managers
  final DatabaseManager databaseManager;

  final AuthenticationManager authenticationManager;

  final MapManager mapManager;

  final InterfaceManager interfaceManager;

  /**
   * Constructor (MORE DETAIL HERE!)
   * @param databaseManager (ADD DESCRIPTION!)
   * @param authenticationManager (ADD DESCRIPTION!)
   * @param mapManager (ADD DESCRIPTION!)
   * @param interfaceManager (ADD DESCRIPTION!)
   */
  public ManagerContext(DatabaseManager databaseManager, AuthenticationManager authenticationManager, MapManager mapManager, InterfaceManager interfaceManager) {
    this.databaseManager = databaseManager;
    this.authenticationManager = authenticationManager;
    this.mapManager = mapManager;
    this.interfaceManager = interfaceManager;
  }
}
