package com.letta.dao;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.letta.entities.SimpleUser;
import com.letta.entities.User;

/**
 * DAO class for managing the users of the system.
 * 
 * @author DRM
 */
public class UsersDAO extends DAO {
	private final static Logger LOG = Logger.getLogger(UsersDAO.class.getName());
	
	/**
	 * Returns a user stored persisted in the system.
	 * 
	 * @param username the login of the user to be retrieved.
	 * @return a user with the provided login.
	 * @throws DAOException if an error happens while retrieving the user.
	 * @throws IllegalArgumentException if the provided login does not
	 * corresponds with any persisted user.
	 */
	public User get(String username) throws DAOException {
		try (final Connection conn = this.getConnection()) {
			final String query = "SELECT * FROM users WHERE username=?";
			
			try (final PreparedStatement statement = conn.prepareStatement(query)) {
				statement.setString(1, username);
				
				try (final ResultSet result = statement.executeQuery()) {
					if (result.next()) {
						return rowToEntity(result);
					} else {
						throw new IllegalArgumentException("Invalid id");
					}
				}
			}
		} catch (SQLException e) {
			LOG.log(Level.SEVERE, "Error checking login", e);
			throw new DAOException(e);
		}
	}
	

	/**
	 * Registers a new user in the system.
	 *
	 * <p>The password is stored in the system "salted" and encoded with the
	 * SHA-256 algorithm.</p>
	 *
	 * @param username the name/login of the user to be registered
	 * @param email the email of the user to be registered.
	 * @param password the password in plain text of news user
	 * @param role the role of the user to be registered.
	 * @param profilePicture the filename of the profile picture of the new user.
	 * @throws DAOException if an error happens while registering the user.
	 */


	/**
	 * Returns a userId stored persisted in the system.
	 *
	 * @param username the login of the user to be retrieved.
	 * @return a user with the provided login.
	 * @throws DAOException if an error happens while retrieving the user.
	 * @throws IllegalArgumentException if the provided login does not
	 * corresponds with any persisted user.
	 */
	public int getUserId(String username) throws DAOException {
		try (final Connection conn = this.getConnection()) {
			final String query = "SELECT user_id FROM users WHERE username=?";

			try (final PreparedStatement statement = conn.prepareStatement(query)) {
				statement.setString(1, username);

				try (final ResultSet result = statement.executeQuery()) {
					if (result.next()) {
						return result.getInt("user_id");
					} else {
						throw new IllegalArgumentException("Invalid username");
					}
				}
			}
		} catch (SQLException e) {
			LOG.log(Level.SEVERE, "Error getting the userId", e);
			throw new DAOException(e);
		}
	}

	public User register(String username, String email,  String password, String role) throws DAOException {
		try (final Connection conn = this.getConnection()) {

			final String query = "INSERT INTO users (username, email ,password, role) VALUES (?, ?, ?, ?)";

			try (final PreparedStatement statement = conn.prepareStatement(query)) {
				statement.setString(1, username);
				statement.setString(2, email);
				statement.setString(3, encodeSha256(password));
				statement.setString(4, role);

				statement.executeUpdate();
			}
		} catch (SQLException e) {
			LOG.log(Level.SEVERE, "Error registering user", e);
			throw new DAOException(e);
		}

		return new User(username, encodeSha256(password), role, email);

	}

	public void set_profile(String username, String about, String public_contact, String nickname) throws DAOException {
		try (final Connection conn = this.getConnection()) {

			final String query = "UPDATE users SET profile_about= ?, public_contact = ?, nickname = ? WHERE user_id = ?";

			try (final PreparedStatement statement = conn.prepareStatement(query)) {
				statement.setString(1, about);
				statement.setString(2, public_contact);
				statement.setString(3, nickname);
				statement.setInt(4, getUserId(username));

				statement.executeUpdate();
			}
		} catch (SQLException e) {
			LOG.log(Level.SEVERE, "Error updating user", e);
			throw new DAOException(e);
		}

	}
	public ArrayList<Integer> getJoinedGroups(int userID) throws DAOException {
		ArrayList<Integer> groupIds = new ArrayList<>();
		try (final Connection conn = this.getConnection()) {

			final String query = "SELECT group_id FROM user_groups WHERE user_id=?";

			try (final PreparedStatement statement = conn.prepareStatement(query)) {
				statement.setInt(1, userID);

				try (final ResultSet resultSet = statement.executeQuery()) {
					while (resultSet.next()) {
						groupIds.add(resultSet.getInt("group_id"));
					}
				}
			}
		} catch (SQLException e) {
			LOG.log(Level.SEVERE, "Error getting user groups", e);
			throw new DAOException(e);
		}

		return groupIds;
	}

	public List<SimpleUser> getByName(String user_name) throws DAOException {
		List<SimpleUser> users = new ArrayList<>();
		try (final Connection conn = this.getConnection()) {

			final String query = "SELECT * FROM users WHERE username LIKE ?";

			try (final PreparedStatement statement = conn.prepareStatement(query)) {
				statement.setString(1, "%" + user_name + "%");

				try (final ResultSet result = statement.executeQuery()) {
					while (result.next()) {
						users.add(rowToSimpleEntity(result));
					}
					return users;
				}
			}

		} catch (SQLException e) {
			throw new DAOException(e);
		}
	}

	private SimpleUser rowToSimpleEntity(ResultSet result) throws SQLException {
		return new SimpleUser(
				result.getString("username"),
				result.getInt("user_id"),
				result.getString("profile_about"),
				result.getString("public_contact"),
				result.getString("nickname")
		);

	}

	public boolean isModeratorOfGroup(int user_id, int group_id) throws DAOException{

		boolean toret = false;
		try (final Connection conn = this.getConnection()) {
			String rol = "";
			final String query = "SELECT users.role FROM users WHERE user_id=?";

			try (final PreparedStatement statement = conn.prepareStatement(query)) {
				statement.setInt(1, user_id);

				try (final ResultSet result = statement.executeQuery()) {
					if (result.next()) {
						rol = result.getString("role");
					}
				}
			}
			if((rol.equals("MODERATOR") && GroupsDAO.isUserModeratorOfThisGroup(conn, user_id, group_id))){
				toret = true;
			}
			else{
				toret = false;
			}

			return toret;
		} catch (SQLException e) {
			LOG.log(Level.SEVERE, "Error searching user", e);
			throw new DAOException(e);
		}

	}



	/**
	 * Checks if the provided credentials (login and password) correspond with a
	 * valid user registered in the system.
	 * 
	 * <p>The password is stored in the system "salted" and encoded with the
	 * SHA-256 algorithm.</p>
	 * 
	 * @param username the login of the user.
	 * @param password the password of the user.
	 * @return {@code true} if the credentials are valid. {@code false}
	 * otherwise.
	 * @throws DAOException if an error happens while checking the credentials.
	 */
	public boolean checkLogin(String username, String password) throws DAOException {
		try {
			final User user = this.get(username);
			
			final String dbPassword = user.getPassword();
			final String shaPassword = encodeSha256(password);
			
			return shaPassword.equals(dbPassword);
		} catch (IllegalArgumentException iae) {
			return false;
		}
	}
	
	public final static String encodeSha256(String text) {
		try {
			final MessageDigest digest = MessageDigest.getInstance("SHA-256");
			final byte[] digested = digest.digest(text.getBytes());
			
			return hexToString(digested);
		} catch (NoSuchAlgorithmException e) {
			LOG.log(Level.SEVERE, "SHA-256 not supported", e);
			throw new RuntimeException(e);
		}
	}
	
	private final static String hexToString(byte[] hex) {
		final StringBuilder sb = new StringBuilder();
		
		for (byte b : hex) {
			sb.append(String.format("%02x", b & 0xff));
		}
		
		return sb.toString();
	}

	private User rowToEntity(ResultSet result) throws SQLException {
		return new User(
			result.getString("username"),
			result.getString("password"),
			result.getString("role"),
			result.getString("email")
		);
	}
}
