package com.letta.entities;

import static java.util.Objects.*;

/**
 * An entity that represents a user.
 *
 * @author DRM
 */
public class SimpleUser {
	private String username;
	private String profileAbout;
	private String publicContact;
	private String nickname;

	private int userID;

	// Constructor needed for the JSON conversion
	SimpleUser() {}

	/**
	 * Constructs a new instance of {@link SimpleUser}.
	 *
	 * @param Username login that identifies the user in the system.
	 * "salt" prefix added.
	 */
	public SimpleUser(String Username,  int userID) {

		this.setUsername(Username);

		this.userID = userID;
	}

	public SimpleUser(String username, int userID, String profileAbout, String publicContact, String nickname) {
		this.username = username;
		this.profileAbout = profileAbout;
		this.publicContact = publicContact;
		this.nickname = nickname;
		this.userID = userID;
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

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	/**
	 * Returns the password of the user.
	 *
	 * @return the password of the user.
	 */




	public String getProfileAbout() {
		return profileAbout;
	}

	public void setProfileAbout(String profileAbout) {
		this.profileAbout = profileAbout;
	}

	public String getPublicContact() {
		return publicContact;
	}

	public void setPublicContact(String publicContact) {
		this.publicContact = publicContact;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

}
