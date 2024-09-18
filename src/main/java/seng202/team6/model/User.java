package seng202.team6.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * User represents the users in the database
 */
public class User {

  /**
   * The username of the user
   */
  private final StringProperty username;

  /**
   * The password of the user
   */
  private final StringProperty password;

  /**
   * The role of the user
   */
  private final StringProperty role;

  /**
   * The salt used for password hashing for the user
   */
  private final StringProperty salt;

  /**
   * Constructs a new User with the specified details
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
   * Constructs a new User with default empty values
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
