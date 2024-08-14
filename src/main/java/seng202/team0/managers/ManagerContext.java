package seng202.team0.managers;

/**
 * ManagerContext is simply a bag of managers. Members are public because of this
 * @author Angus McDougall
 */
public class ManagerContext {

  // Managers
  final public DatabaseManager databaseManager;

  final public AuthenticationManager authenticationManager;

  final public MapManager mapManager;

  final public InterfaceManager interfaceManager;

  /**
   * Constructor for ManagerContext
   * @param databaseManager database manager
   * @param authenticationManager authentication manager
   * @param mapManager map manager
   * @param interfaceManager interface manager
   */
  public ManagerContext(
      DatabaseManager databaseManager,
      AuthenticationManager authenticationManager,
      MapManager mapManager,
      InterfaceManager interfaceManager) {
    this.databaseManager = databaseManager;
    this.authenticationManager = authenticationManager;
    this.mapManager = mapManager;
    this.interfaceManager = interfaceManager;
  }
}
