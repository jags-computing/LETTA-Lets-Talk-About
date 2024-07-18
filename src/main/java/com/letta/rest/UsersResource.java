package com.letta.rest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import com.letta.dao.DAOException;
import com.letta.dao.UsersDAO;
import com.letta.entities.User;


/**
 * REST resource for managing users.
 * 
 * @author DRM
 */
@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
public class UsersResource {
	private final static Logger LOG = Logger.getLogger(UsersResource.class.getName());
	
	private final UsersDAO dao;
	private final UsersDAO usersDAO = new UsersDAO();
	
	private @Context SecurityContext security;
	
	/**
	 * Constructs a new instance of {@link UsersResource}.
	 */
	public UsersResource() {
		this(new UsersDAO());
	}
	
	// Needed for testing purposes
	UsersResource(UsersDAO dao) {
		this(dao, null);
	}
	
	// Needed for testing purposes
	UsersResource(UsersDAO dao, SecurityContext security) {
		this.dao = dao;
		this.security = security;
	}
	
	/**
	 * Returns a user with the provided login.
	 * 
	 * @param username the identifier of the user to retrieve.
	 * @return a 200 OK response with an user that has the provided login.
	 * If the request is done without providing the login credentials or using
	 * invalid credentials a 401 Unauthorized response will be returned. If the
	 * credentials are provided and a regular user (i.e. non admin user) tries
	 * to access the data of other user, a 403 Forbidden response will be
	 * returned. If the credentials are OK, but the login does not corresponds
	 * with any user, a 400 Bad Request response with an error message will be
	 * returned. If an error happens while retrieving the list, a 500 Internal
	 * Server Error response with an error message will be returned.
	 */
	@GET
	@Path("/{username}")
	public Response get(
		@PathParam("username") String username
	) {
		final String loggedUser = getLogin();
		
		// Each user can only access his or her own data. Only the admin user
		// can access the data of any user.
		if (loggedUser.equals(username) || this.isAdmin() || this.isModerator()) {
			try {
				return Response.ok(dao.get(username)).build();
			} catch (IllegalArgumentException iae) {
				LOG.log(Level.FINE, "Invalid user login in get method", iae);
				
				return Response.status(Response.Status.BAD_REQUEST)
					.entity(iae.getMessage())
				.build();
			} catch (DAOException e) {
				LOG.log(Level.SEVERE, "Error getting an user", e);
				
				return Response.serverError()
					.entity(e.getMessage())
				.build();
			}
		} else {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
	}

	@GET
	@Path("/groups")
	public Response getGroups(
			@HeaderParam("Authorization") String auth
	) {
		try {
			// get the entire string after "Basic "
			String encodedCredentials = auth.substring(auth.indexOf("Basic ") + "Basic ".length());

			// decode the credentials
			byte[] decodedBytes = Base64.getDecoder().decode(encodedCredentials);
			String decodedString = new String(decodedBytes, StandardCharsets.UTF_8);

			// split the string to get the username and password
			String[] loginPassword = decodedString.split(":");
			String decodeUsername = loginPassword[0];
			int userID = this.dao.getUserId(decodeUsername);


			return Response.ok(this.dao.getJoinedGroups(userID)).build();
		} catch (IllegalArgumentException iae) {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity(iae.getMessage())
					.build();
		} catch (DAOException e) {
			return Response.serverError()
					.entity(e.getMessage())
					.build();
		}
	}

	/**
	 * Creates a new person in the system.
	 *
	 * @param username the username of the user
	 * @param password the password of the user.
	 * @param email the email of the user.
	 * @param profilePicture the profile picture of the user.
	 * @return a 200 OK response with a person that has been created. If the
	 * name or the surname are not provided, a 400 Bad Request response with an
	 * error message will be returned. If an error happens while retrieving the
	 * list, a 500 Internal Server Error response with an error message will be
	 * returned.
	 */
	@POST
	public Response registration(
			@FormParam("username") String username,
			@FormParam("password") String password,
			@FormParam("email") String email
	) {
		String role = "USER";
		try {
			final User newUser = this.dao.register(username, email, password, role);

			return Response.ok(newUser).build();
		} catch (IllegalArgumentException iae) {
			LOG.log(Level.FINE, "Invalid User id in add method", iae);

			return Response.status(Response.Status.BAD_REQUEST)
					.entity(iae.getMessage())
					.build();
		} catch (DAOException e) {
			LOG.log(Level.SEVERE, "Error adding a user", e);

			return Response.serverError()
					.entity(e.getMessage())
					.build();
		}
	}

	/**
	 * Updates the profile of a user
	 *
	 * @param username the username of the user
	 * @param about_me a text about the user.
	 * @param public_contact a publicly viewable contact (phone, email, whatever).
	 * @param profilePictureName the name of the user's profile picture
	 * @param profilePicture the profile picture in base64 encoding.
	 * @return a 200 OK response with a person that has been created. If the
	 * name or the surname are not provided, a 400 Bad Request response with an
	 * error message will be returned. If an error happens while retrieving the
	 * list, a 500 Internal Server Error response with an error message will be
	 * returned.
	 */
	@POST
	@Path("/profile")
	public Response update_profile(
			@FormParam("about_me") String about_me,
			@FormParam("public_contact") String public_contact,
			@FormParam("nickname") String nickname,
			@FormParam("profilePicture") String profilePicture,
			@HeaderParam("Authorization") String auth
	) {
		try {
			// get the entire string after "Basic "
			String encodedCredentials = auth.substring(auth.indexOf("Basic ") + "Basic ".length());
			// decode the credentials
			byte[] decodedBytes = Base64.getDecoder().decode(encodedCredentials);
			String decodedString = new String(decodedBytes, StandardCharsets.UTF_8);

			// split the string to get the username and password
			String[] loginPassword = decodedString.split(":");
			String decodeUsername = loginPassword[0];

			usersDAO.set_profile(decodeUsername, about_me, public_contact, nickname);


			try {
				//convert the picture an array of bytes
				final int dataStartIndex = profilePicture.indexOf(",") + 1;
				final String data = profilePicture.substring(dataStartIndex);
				byte[] decodedBytesPicture = java.util.Base64.getDecoder().decode(data);

				java.nio.file.Path path = Paths.get(System.getProperty("user.dir").replace("target\\catalina-base", "") + "src\\main\\webapp\\pictures\\" + usersDAO.getUserId(decodeUsername) + ".png");

				Files.write(path, decodedBytesPicture);
				return Response.ok().build();
			} catch (IOException e) {
				System.out.println("IOException");
			}
			catch (NullPointerException e){
				System.out.println("File empty");
			}

			return Response.ok().build();
		} catch (IllegalArgumentException iae) {
			LOG.log(Level.FINE, "Invalid User id in add method", iae);

			return Response.status(Response.Status.BAD_REQUEST)
					.entity(iae.getMessage())
					.build();
		} catch (DAOException e) {
			LOG.log(Level.SEVERE, "Error adding a user", e);

			return Response.serverError()
					.entity(e.getMessage())
					.build();
		}
	}
	@GET
	@Path("/byname/{user_name}")
	public Response get_by_name(
			@PathParam("user_name") String user_name
	) {
		try {

			return Response.ok(this.dao.getByName(user_name)).build();
		} catch (IllegalArgumentException iae) {

			return Response.status(Response.Status.BAD_REQUEST)
					.entity(iae.getMessage())
					.build();
		} catch (DAOException e) {

			return Response.serverError()
					.entity(e.getMessage())
					.build();
		}
	}

	@GET
	@Path("/isModerator/{groupId}")
	public Response isModeratorOfGroup(
		@PathParam("groupId") int groupId,
		@HeaderParam("Authorization") String auth
	) {

		try {
            String encodedCredentials = auth.substring(auth.indexOf("Basic ") + "Basic ".length());
			byte[] decodedBytes = Base64.getDecoder().decode(encodedCredentials);
			String decodedString = new String(decodedBytes, StandardCharsets.UTF_8);
			String[] loginPassword = decodedString.split(":");
			String decodeUsername = loginPassword[0];

            int user_id = this.dao.getUserId(decodeUsername);


            return Response.ok(this.dao.isModeratorOfGroup(user_id, groupId)).build();
        } catch (IllegalArgumentException iae) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(iae.getMessage())
                    .build();
        } catch (DAOException e) {
            return Response.serverError()
                    .entity(e.getMessage())
                    .build();  
        }
	}



	private String getLogin() {
		return this.security.getUserPrincipal().getName();
	}
	
	private boolean isAdmin() {
		return this.security.isUserInRole("ADMIN");
	}
	private boolean isModerator() {
		return this.security.isUserInRole("MODERATOR");
	}
}
