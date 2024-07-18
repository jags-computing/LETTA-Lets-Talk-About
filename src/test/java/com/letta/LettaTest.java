package com.letta;

import static java.util.Collections.unmodifiableSet;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;

import com.letta.filters.AuthorizationFilter;

@ApplicationPath("/rest/*")
public class LettaTest extends Letta {
	@Override
	public Set<Class<?>> getClasses() {
		final Set<Class<?>> classes = new HashSet<>(super.getClasses());
		
		classes.add(AuthorizationFilter.class);
		
		return unmodifiableSet(classes);
	}
}
