package com.letta;

import static java.util.stream.Collectors.*;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import com.letta.rest.EventsResource;
import com.letta.rest.FileResource;
import com.letta.rest.GroupsResource;
import com.letta.rest.PeopleResource;
import com.letta.rest.TopicsResource;
import com.letta.rest.UsersResource;

/**
 * Configuration of the REST application. This class includes the resources and
 * configuration parameter used in the REST API of the application.
 * 
 * @author Miguel Reboiro Jato
 *
 */
@ApplicationPath("/rest/*") 
public class Letta extends Application {
	@Override
	public Set<Class<?>> getClasses() {
		return Stream.of(
			PeopleResource.class,
			UsersResource.class,
			GroupsResource.class,
			TopicsResource.class,
			EventsResource.class,
			FileResource.class
		).collect(toSet());
	}
	
	@Override
	public Map<String, Object> getProperties() {
		// Activates JSON automatic conversion in JAX-RS
		return Collections.singletonMap(
			"com.sun.jersey.api.json.POJOMappingFeature", true
		);
	}
}
