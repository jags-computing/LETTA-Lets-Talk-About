package com.letta.entities;

import static java.util.Objects.*;

/**
 * An entity that represents a user.
 * 
 * @author DRM
 */
public class User {
	private String username;
	private String email;
	private String password;
	private String role;

	// Constructor needed for the JSON conversion
	User() {}
	
	/**
	 * Constructs a new instance of {@link User}.
	 *
	 * @param Username login that identifies the user in the system.
	 * @param password password of the user encoded using SHA-256 and with the
	 * "salt" prefix added.
	 */
	public User(String Username, String password, String role, String email) {

		this.setUsername(Username);
		this.setPassword(password);
		this.setEmail(email);
		this.setRole(role);
	}

	/**
	 * Returns the username of the user.
	 * 
	 * @return the username of the user.
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Sets the login of the user.
	 * 
	 * @param Username the login that identifies the user in the system.
	 */
	public void setUsername(String Username) {
		this.username = requireNonNull(Username, "Username can't be null");
	}
	
	/**
	 * Returns the password of the user.
	 * 
	 * @return the password of the user.
	 */
	public String getPassword() {
		return password;
	}
	
	/**
	 * Sets the users password.
	 * @param password the password of the user encoded using SHA-256 and with
	 * the "salt" prefix added.
	 */
	public void setPassword(String password) {
		requireNonNull(password, "Password can't be null");
		if (!password.matches("[a-zA-Z0-9]{64}"))
			throw new IllegalArgumentException("Password must be a valid SHA-256");
		
		this.password = password;
	}

	/**
	 * Returns the email of the user.
	 *
	 * @return the email of the user.
	 */
	public String getEmail() { return email; }

	/**
	 * Sets the email of the user.
	 *
	 * @param email the email of the user.
	 */
	public void setEmail(String email) { this.email = requireNonNull(email, "email can't be null"); }

	/**
	 * Returns the role of the user.
	 * 
	 * @return the role of the user.
	 */
	public String getRole() {
		return role;
	}

	/**
	 * Sets the role of the user.
	 * 
	 * @param role the role of the user
	 */
	public void setRole(String role) {
		this.role = requireNonNull(role, "Role can't be null");
	}

}
