package seng202.team6.managers;

import seng202.team6.model.Role;

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

  public boolean isAuthenticated() {
    return authenticated;
  }

  public void setAuthenticated(boolean authenticated) {
    this.authenticated = authenticated;
  }

  public boolean isAdmin() {
    return admin;
  }

  public void setAdmin(boolean admin) {
    this.admin = admin;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public void logout() {
    authenticated = false;
    admin = false;
    username = "";
  }

  // Getters and Setters
  public Role getRole() {
    return null;
  } // TODO Implement me!

  public String getUserID() {
    return null;
  } // TODO Implement me!
}
