package com.letta.rest;

import java.io.File;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;


/**
 * REST resource for managing users.
 * 
 * @author DRM
 */
@Path("/files")
@Produces(MediaType.APPLICATION_JSON)
public class FileResource {
	private final static Logger LOG = Logger.getLogger(FileResource.class.getName());

	private @Context SecurityContext security;
	
	/**
	 * Constructs a new instance of {@link FileResource}.
	 */
	public FileResource() {
	}

	@GET
	@Path("/picture/{fileName}")
	public Response getFile(@PathParam("fileName") String fileName) {
		File file = new File(System.getProperty("user.dir").replace("target/catalina-base", "") + "src/main/webapp/pictures/" + fileName);

		if(!(file.exists() && !file.isDirectory())) {
			file = new File(System.getProperty("user.dir").replace("target/catalina-base", "") + "src/main/webapp/pictures/default.png");
		}



		return Response.ok(file, new MediaType("image", "png"))
				.build();

	}



	private String getLogin() {
		return this.security.getUserPrincipal().getName();
	}
	
	private boolean isAdmin() {
		return this.security.isUserInRole("ADMIN");
	}

}
