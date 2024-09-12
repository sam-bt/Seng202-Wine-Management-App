package seng202.team0.database;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Wine represents the wine record in the database
 */
public class Login {

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
   * Constructor
   *
   * @param username username
   * @param password password
   * @param role     role
   */
  public Login(
      String username,
      String password,
      String role
  ) {
    this.username = new SimpleStringProperty(this, "username", username);
    this.password = new SimpleStringProperty(this, "password", password);
    this.role = new SimpleStringProperty(this, "role", role);
  }

  /**
   * Default constructor
   */
  public Login() {
    this.username = new SimpleStringProperty(this, "username");
    this.password = new SimpleStringProperty(this, "password");
    this.role = new SimpleStringProperty(this, "role");

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

}
