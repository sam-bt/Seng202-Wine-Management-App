package seng202.team6.managers;

import seng202.team6.model.Role;

/**
 * Authentication Manager (MORE DETAIL HERE!)
 */
public class AuthenticationManager {

  private boolean authenticated;
  private boolean admin;
  private boolean adminFirstLogin;
  private String username;

  public AuthenticationManager() {
    this.authenticated = false;
    this.adminFirstLogin = false;
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

  public boolean isAdminFirstLogin() {
    return adminFirstLogin;
  }

  public void setAdminFirstLogin(boolean adminFirstLogin) {
    this.adminFirstLogin = adminFirstLogin;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  // Getters and Setters
  public Role getRole() {
    return null;
  } // TODO Implement me!

  public String getUserID() {
    return null;
  } // TODO Implement me!
}
