package com.letta.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.letta.dao.DAOException;
import com.letta.dao.TopicsDAO;

@Path("/topics")
@Produces(MediaType.APPLICATION_JSON)
public class TopicsResource {

    private final TopicsDAO dao;

    public TopicsResource(){
        this(new TopicsDAO());
    }

    TopicsResource(TopicsDAO dao){
        this.dao = dao;
    }


    @GET
	@Path("/{topicname}")
    public Response get(
		@PathParam("topicname") String topicname
	) {
        try {
            return Response.ok(dao.get(topicname)).build();
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
    public Response getTopicnames() {
        try {
            return Response.ok(dao.getTopicnames()).build();
        } catch (IllegalArgumentException iae) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(iae.getMessage())
            .build();
        } 
        catch (DAOException e) {
            return Response.serverError()
                .entity(e.getMessage())
            .build();
        }
	}


    
}
