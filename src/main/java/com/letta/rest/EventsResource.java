package com.letta.rest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import com.letta.dao.EventsDAO;
import com.letta.dao.UsersDAO;
import com.letta.entities.Comment;
import com.letta.entities.CommentReport;
import com.letta.entities.Event;
import com.letta.entities.EventReport;


@Path("/events")
@Produces(MediaType.APPLICATION_JSON)
public class EventsResource {
    
	private final static Logger LOG = Logger.getLogger(PeopleResource.class.getName());
    private final EventsDAO dao;

    private final UsersDAO usersDAO = new UsersDAO();

    public EventsResource(){
        this(new EventsDAO());
    }

    EventsResource(EventsDAO dao){
        this.dao = dao;
    }

    @POST
    public Response add(
        @FormParam("eventName") String eventName, 
        @FormParam("eventDate") String eventDate, 
        @FormParam("groupId") int groupid,
        @FormParam("description") String description,
        @FormParam("mediaFile") String mediaFile,
        @HeaderParam("Authorization") String auth
    )
     {
        try {

			String encodedCredentials = auth.substring(auth.indexOf("Basic ") + "Basic ".length());

			byte[] decodedBytes = Base64.getDecoder().decode(encodedCredentials);
			String decodedString = new String(decodedBytes, StandardCharsets.UTF_8);

			String[] loginPassword = decodedString.split(":");
			String decodeUsername = loginPassword[0];
			int ownerId = this.usersDAO.getUserId(decodeUsername);


            final Event newEvent = this.dao.add(eventName, eventDate, groupid, description, mediaFile, ownerId);
            
            return Response.ok(newEvent).build();
        } catch (IllegalArgumentException iae) {
            LOG.log(Level.FINE, "Invalid arguments in add method", iae);
            
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(iae.getMessage())
                .build();
        } catch (DAOException e) {
            LOG.log(Level.SEVERE, "Error adding an event", e);
            return Response.serverError()
                    .entity(e.getMessage())
                .build();
        }
    }

    @POST
    @Path("/{eventId}/comments")
    public Response addComment(
            @PathParam("eventId") int eventId,
            @FormParam("commentText") String commentText,
            @HeaderParam("Authorization") String auth
    ) {
        try {

            String encodedCredentials = auth.substring(auth.indexOf("Basic ") + "Basic ".length());

			byte[] decodedBytes = Base64.getDecoder().decode(encodedCredentials);
			String decodedString = new String(decodedBytes, StandardCharsets.UTF_8);

			String[] loginPassword = decodedString.split(":");
			String decodeUsername = loginPassword[0];
			int userId = this.usersDAO.getUserId(decodeUsername);

            this.dao.addComment(eventId, userId, commentText);
            return Response.ok().build();
        } catch (DAOException e) {
            LOG.log(Level.SEVERE, "Error adding comment", e);
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/report")
    public Response addReport(
        @FormParam("event_id") int event_id,
        @FormParam("reportReason") String report_reason,
        @HeaderParam("Authorization") String auth
    )
     {
        try {

            String encodedCredentials = auth.substring(auth.indexOf("Basic ") + "Basic ".length());

			byte[] decodedBytes = Base64.getDecoder().decode(encodedCredentials);
			String decodedString = new String(decodedBytes, StandardCharsets.UTF_8);

			String[] loginPassword = decodedString.split(":");
			String decodeUsername = loginPassword[0];
			int ownerId = this.usersDAO.getUserId(decodeUsername);

            final EventReport newEvent = this.dao.addReport(event_id, report_reason, ownerId);
            
            return Response.ok(newEvent).build();
        } catch (IllegalArgumentException iae) {
            LOG.log(Level.FINE, "Invalid arguments in add method", iae);
            
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(iae.getMessage())
                .build();
        } catch (DAOException e) {
            LOG.log(Level.SEVERE, "Error adding an report", e);
            return Response.serverError()
                    .entity(e.getMessage())
                .build();
        }
    }

    @POST
    @Path("/report/comment")
    public Response addCommentReport(
        @FormParam("comment_id") int comment_id, 
        @FormParam("reportReason") String report_reason,
        @HeaderParam("Authorization") String auth
    )
    {
        try {

			String encodedCredentials = auth.substring(auth.indexOf("Basic ") + "Basic ".length());
			byte[] decodedBytes = Base64.getDecoder().decode(encodedCredentials);
			String decodedString = new String(decodedBytes, StandardCharsets.UTF_8);
			String[] loginPassword = decodedString.split(":");
			String decodeUsername = loginPassword[0];

            int user_id = this.usersDAO.getUserId(decodeUsername);


            final CommentReport newCommnetReport = this.dao.addCommentReport(comment_id, user_id, report_reason);
            
            return Response.ok(newCommnetReport).build();
        } catch (IllegalArgumentException iae) {
            LOG.log(Level.FINE, "Invalid arguments in add report method", iae);
            
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(iae.getMessage())
                .build();
        } catch (DAOException e) {
            LOG.log(Level.SEVERE, "Error adding an comment report", e);
            return Response.serverError()
                    .entity(e.getMessage())
                .build();
        }
    }

    @GET
    @Path("/report/comment/{comentId}")
    public Response getCommentReport(
            @PathParam("comentId") int comentId,
            @HeaderParam("Authorization") String auth
    ) {
        try {
            String encodedCredentials = auth.substring(auth.indexOf("Basic ") + "Basic ".length());
			byte[] decodedBytes = Base64.getDecoder().decode(encodedCredentials);
			String decodedString = new String(decodedBytes, StandardCharsets.UTF_8);
			String[] loginPassword = decodedString.split(":");
			String decodeUsername = loginPassword[0];

            int user_id = this.usersDAO.getUserId(decodeUsername);
            return Response.ok(this.dao.getCommentReport(comentId, user_id)).build();
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
     * Returns a list of events associated to a specific group
     * @param groupId the id of the group to be retrieved.
     * @return a list of events with the provided group id.
     *
     * */
    @GET
    @Path("/by_group/{groupId}")
    public Response getEventsOfAGroup(
            @PathParam("groupId") String groupId
    ) {
        try {
            return Response.ok(this.dao.getEventsOfAGroup(groupId)).build();
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
    @Path("/byname/{eventName}")
    public Response getEventIdByName(
            @PathParam("eventName") String eventName
    ) {
        try {
            return Response.ok(this.dao.getIdByName(eventName)).build();
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
    @Path("/byid/{eventId}")
    public Response getEventNameById(
            @PathParam("eventId") int eventId
    ) {
        try {
            return Response.ok(this.dao.getNameById(eventId)).build();
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
    @Path("/{eventId}/comments")
    public Response getCommentsByEventId(@PathParam("eventId") int eventId) {
        try {
            List<Comment> comments = this.dao.getCommentsByEventId(eventId);
            return Response.ok(comments).build();
        } catch (DAOException e) {
            LOG.log(Level.SEVERE, "Error getting comments for event ID", e);
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    

}
