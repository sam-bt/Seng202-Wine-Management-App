package seng202.team6.managers;

/**
 * ManagerContext is simply a bag of managers. Members are public because of this
 */
public class ManagerContext {

  private final DatabaseManager databaseManager;

  private final GuiManager guiManager;

  private final AuthenticationManager authenticationManager;

  private final TaskManager taskManager;

  /**
   * Constructor for ManagerContext.
   *
   * @param databaseManager       database manager
   * @param guiManager            interface manager
   * @param authenticationManager authentication manager
   */
  public ManagerContext(
      DatabaseManager databaseManager,
      GuiManager guiManager,
      AuthenticationManager authenticationManager, TaskManager taskManager
  ) {
    this.databaseManager = databaseManager;
    this.guiManager = guiManager;
    this.authenticationManager = authenticationManager;
    this.taskManager = taskManager;
  }

  public DatabaseManager getDatabaseManager() {
    return databaseManager;
  }

  public GuiManager getGuiManager() {
    return guiManager;
  }

  public AuthenticationManager getAuthenticationManager() {
    return authenticationManager;
  }

  public TaskManager getTaskManager() {
    return taskManager;
  }
}
