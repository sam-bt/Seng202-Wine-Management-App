package seng202.team0.managers;

/**
 * Authentication Manager (MORE DETAIL HERE!)
 */
public class AuthenticationManager {
  private boolean authenticated;
  private boolean admin;
  private String username;
  public AuthenticationManager() {
    this.authenticated = false;
    this.admin = false;
  }
  public void setAuthenticated(boolean authenticated) {
    this.authenticated = authenticated;
  }

  public boolean isAuthenticated() {
    return authenticated;
  }
  public void setAdmin(boolean admin) {
    this.admin = admin;
  }

  public boolean isAdmin() {
    return admin;
  }

  public void getUser(String username) {
    this.username = username;
  }

  public String getUsername() {
    return username;
  }

  /*
  public createNewUser(Database database, String username, String password) throws AuthenticationManagerInitializationException{
    // TODO Implement me!
  }



  // Pretty sure the "new" variables are somewhat optional, so we need a way to deal with all the possible cases
  public void editUser(Database database, String username, String newUsername, String newPassword, Role newRole){
    // TODO Implement me!
  }


  public removeUser(Database database, String username) throws InvalidUserException, NoPermissionException {
    // TODO Implement me!
  }
  */

  // Getters and Setters
  public Role getRole(){return null;} // TODO Implement me!
  public String getUserID(){return null;} // TODO Implement me!
}
