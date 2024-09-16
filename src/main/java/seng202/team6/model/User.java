package seng202.team6.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * User represents the users in the database
 */
public class User {

  /**
   * username
   */
  private final StringProperty username;
  /**
   * password
   */
  private final StringProperty password;
  /**
   * role
   */
  private final StringProperty role;

  /**
   * salt
   */
  private final StringProperty salt;

  /**
   * Constructor
   *
   * @param username username
   * @param password password
   * @param role     role
   * @param salt     salt
   */
  public User(
      String username,
      String password,
      String role,
      String salt
  ) {
    this.username = new SimpleStringProperty(this, "username", username);
    this.password = new SimpleStringProperty(this, "password", password);
    this.role = new SimpleStringProperty(this, "role", role);
    this.salt = new SimpleStringProperty(this, "salt", salt);
  }

  /**
   * Default constructor
   */
  public User() {
    this.username = new SimpleStringProperty(this, "username");
    this.password = new SimpleStringProperty(this, "password");
    this.role = new SimpleStringProperty(this, "role");
    this.salt = new SimpleStringProperty(this, "salt");
  }

  /**
   * Gets the username
   *
   * @return username
   */
  public String getUsername() {
    return username.get();
  }

  /**
   * Sets the username
   *
   * @param username username
   */
  public void setUsername(String username) {
    this.username.set(username);
  }

  /**
   * Gets the username property
   *
   * @return username property
   */
  public StringProperty usernameProperty() {
    return username;
  }

  /**
   * Gets the password
   *
   * @return password
   */
  public String getPassword() {
    return password.get();
  }

  /**
   * Sets the password
   *
   * @param password password
   */
  public void setPassword(String password) {
    this.password.set(password);
  }

  /**
   * Gets the password property
   *
   * @return password property
   */
  public StringProperty passwordProperty() {
    return password;
  }

  /**
   * Gets the role
   *
   * @return role
   */
  public String getRole() {
    return role.get();
  }

  /**
   * Sets the role
   *
   * @param role role
   */
  public void setRole(String role) {
    this.role.set(role);
  }

  /**
   * Gets the role property
   *
   * @return role property
   */
  public StringProperty roleProperty() {
    return role;
  }

/**
 * Gets the salt
 *
 * @return salt
 */
public String getSalt() {
  return salt.get();
}

/**
 * Sets the salt
 *
 * @param salt salt
 */
public void setSalt(String salt) {
  this.salt.set(salt);
}

/**
 * Gets the salt property
 *
 * @return salt property
 */
public StringProperty saltProperty() {
  return salt;
}

}
