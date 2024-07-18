package com.letta.rest;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;
import java.util.regex.Pattern;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.letta.dao.DAOException;
import com.letta.dao.GroupsDAO;
import com.letta.dao.TopicsDAO;
import com.letta.dao.UsersDAO;
import com.letta.entities.EventReport;
import com.letta.entities.Group;

@Path("/groups")
@Produces(MediaType.APPLICATION_JSON)
public class GroupsResource {
    
    private final GroupsDAO dao;

	private final UsersDAO usersDAO = new UsersDAO();

	private final TopicsDAO topicsDAO = new TopicsDAO();



    public GroupsResource(){
        this(new GroupsDAO());
    }

    GroupsResource(GroupsDAO dao){
        this.dao = dao;
    }

    @GET
	@Path("/bytopic/{groupid}")
	public Response getbytopic(
		@PathParam("groupid") int group_id
	) {
        try {
            return Response.ok(dao.getbytopic(group_id)).build();
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
	@Path("/byname/{group_name}")
	public Response get(
		@PathParam("group_name") String group_name
	) {
		try {
			final Group group = this.dao.getByName(group_name);
			
			return Response.ok(group).build();
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
	 * @param groupName the group name
	 * @param description short text that describe the group.
	 * @param username  username of the owner of the group.
	 * @param topics the topics of the group.
	 * @return a 200 OK response with a group that has been created. If the
	 * name or the description are not provided, a 400 Bad Request response with an
	 * error message will be returned. If an error happens while retrieving the
	 * list, a 500 Internal Server Error response with an error message will be
	 * returned.
	 */
	@POST
	public Response addGroup(
			@FormParam("name") String groupName,
			@FormParam("description") String description,
			@FormParam("username") String username,
			@FormParam("topics") List<String> topics,
			@HeaderParam("Authorization") String auth
	) {
		try {
			if(!Pattern.matches("^[A-Za-z0-9_]{3,16}", groupName)) {
				return Response.serverError()
						.entity("group name invalid")
						.build();
			}
			if(!Pattern.matches("^.{5,20}$", description)) {
				return Response.serverError()
						.entity("description must contain between 5 and 20 characters")
						.build();
			}


			// get the entire string after "Basic "
			String encodedCredentials = auth.substring(auth.indexOf("Basic ") + "Basic ".length());

			// decode the credentials
			byte[] decodedBytes = Base64.getDecoder().decode(encodedCredentials);
			String decodedString = new String(decodedBytes, StandardCharsets.UTF_8);

			// split the string to get the username and password
			String[] loginPassword = decodedString.split(":");
			String decodeUsername = loginPassword[0];
			int ownerId = this.usersDAO.getUserId(decodeUsername);


			final Group newGroup = this.dao.addGroup(groupName, description, ownerId);

			// parse the topics string by comma and create an array string topics
			this.topicsDAO.setTopics(groupName, topics);

			return Response.ok(newGroup).build();
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
	 * Adds a user as member of a group
	 *
	 * @param groupID the name of the group to join
	 * @param auth the credentials of the user
	 * @return a 200 OK response with a group that has been created. If the
	 * name or the description are not provided, a 400 Bad Request response with an
	 * error message will be returned. If an error happens while retrieving the
	 * list, a 500 Internal Server Error response with an error message will be
	 * returned.
	 */
	@POST
	@Path("/join")
	public Response addUserToGroup(
			@FormParam("group_id") int groupID,
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
			int userID = this.usersDAO.getUserId(decodeUsername);


			this.dao.addUserToGroup(userID, groupID);

			return Response.ok().build();
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
    @Path("/eventReports/{group_id}")
    public Response getEventReports(
		@PathParam("group_id") int group_id,
        @HeaderParam("Authorization") String auth
	) {
		try{
			String encodedCredentials = auth.substring(auth.indexOf("Basic ") + "Basic ".length());

			byte[] decodedBytes = Base64.getDecoder().decode(encodedCredentials);
			String decodedString = new String(decodedBytes, StandardCharsets.UTF_8);

			String[] loginPassword = decodedString.split(":");
			String decodeUsername = loginPassword[0];
			int ownerId = this.usersDAO.getUserId(decodeUsername);

			List<EventReport> eventReports = dao.getEventReportsByGroup(ownerId, group_id);

			return Response.ok(eventReports).build();
			
		}catch (IllegalArgumentException iae) {
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
    @Path("/eventIds/{group_id}")
    public Response getEvents(
		@PathParam("group_id") int group_id
	) throws SQLException {
		try{
			return Response.ok(this.dao.getEventIds(group_id)).build();
			
		}catch (IllegalArgumentException iae) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(iae.getMessage())
                	.build();
        }

	}


}
