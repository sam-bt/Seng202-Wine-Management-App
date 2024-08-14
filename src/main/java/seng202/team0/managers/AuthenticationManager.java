package seng202.team0.managers;

import seng202.team0.managers.exceptions.AuthenticationManagerInitializationException;
import seng202.team0.managers.exceptions.InvalidUserException;
import seng202.team0.managers.exceptions.NoPermissionException;

/**
 * Authentication Manager (MORE DETAIL HERE!)
 */
public class AuthenticationManager {

  /* UNCOMMENT IF DATABASE HAS A DEFINITION!
  public void loginUser(Database database, String username, String password) throws InvalidUserException, AuthenticationManagerInitializationException {
    // TODO Implement me!
  }


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
  public String getUsername(){return null;} // TODO Implement me!
}
