package com.letta.rest;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;

import javax.sql.DataSource;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import com.letta.Letta;
import com.letta.LettaTest;
import com.letta.dataset.UsersDataset;
import com.letta.listeners.ApplicationContextBinding;
import com.letta.listeners.ApplicationContextJndiBindingTestExecutionListener;
import com.letta.listeners.DbManagement;
import com.letta.listeners.DbManagementTestExecutionListener;
import com.letta.matchers.HasHttpStatus;
import com.letta.matchers.IsEqualToUser;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;

import com.letta.entities.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:contexts/mem-context.xml")
@TestExecutionListeners({
	DbUnitTestExecutionListener.class,
	DbManagementTestExecutionListener.class,
	ApplicationContextJndiBindingTestExecutionListener.class
})
@ApplicationContextBinding(
	jndiUrl = "java:/comp/env/jdbc/daaexample",
	type = DataSource.class
)
@DbManagement(
	create = "classpath:db/hsqldb.sql",
	drop = "classpath:db/hsqldb-drop.sql"
)
@DatabaseSetup("/datasets/dataset.xml")
@ExpectedDatabase("/datasets/dataset.xml")
public class UsersResourceTest extends JerseyTest {
	@Override
	protected Application configure() {
		return new Letta();
	}

	@Override
	protected void configureClient(ClientConfig config) {
		super.configureClient(config);
		
		// Enables JSON transformation in client
		config.register(JacksonJsonProvider.class);
		config.property("com.sun.jersey.api.json.POJOMappingFeature", Boolean.TRUE);
	}
	
	@Test
	public void testGetAdminOwnUser() throws IOException {
		final String admin = UsersDataset.adminLogin();
		
		final Response response = target("users/" + admin).request()
			.header("Authorization", "Basic " + UsersDataset.userToken(admin))
		.get();
		MatcherAssert.assertThat(response, HasHttpStatus.hasOkStatus());
		
		final User user = response.readEntity(User.class);
		
		MatcherAssert.assertThat(user, CoreMatchers.is(IsEqualToUser.equalsToUser(UsersDataset.user(admin))));
	}
	
	@Test
	public void testGetAdminOtherUser() throws IOException {
		final String admin = UsersDataset.adminLogin();
		final String otherUser = UsersDataset.normalLogin();
		
		final Response response = target("users/" + otherUser).request()
			.header("Authorization", "Basic " + UsersDataset.userToken(admin))
		.get();
		MatcherAssert.assertThat(response, HasHttpStatus.hasOkStatus());
		
		final User user = response.readEntity(User.class);
		
		MatcherAssert.assertThat(user, CoreMatchers.is(IsEqualToUser.equalsToUser(UsersDataset.user(otherUser))));
	}
	
	@Test
	public void testGetNormalOwnUser() throws IOException {
		final String login = UsersDataset.normalLogin();
		
		final Response response = target("users/" + login).request()
			.header("Authorization", "Basic " + UsersDataset.userToken(login))
		.get();
		MatcherAssert.assertThat(response, HasHttpStatus.hasOkStatus());
		
		final User user = response.readEntity(User.class);
		
		MatcherAssert.assertThat(user, CoreMatchers.is(IsEqualToUser.equalsToUser(UsersDataset.user(login))));
	}
	
	@Test
	public void testGetNoCredentials() throws IOException {
		final Response response = target("users/" + UsersDataset.normalLogin()).request().get();
		
		MatcherAssert.assertThat(response, HasHttpStatus.hasUnauthorized());
	}
	
	@Test
	public void testGetBadCredentials() throws IOException {
		final Response response = target("users/" + UsersDataset.adminLogin()).request()
			.header("Authorization", "Basic YmFkOmNyZWRlbnRpYWxz")
		.get();
		
		MatcherAssert.assertThat(response, HasHttpStatus.hasUnauthorized());
	}
	
	@Test
	public void testGetIllegalAccess() throws IOException {
		final Response response = target("users/" + UsersDataset.adminLogin()).request()
			.header("Authorization", "Basic " + UsersDataset.userToken(UsersDataset.normalLogin()))
		.get();
		
		MatcherAssert.assertThat(response, HasHttpStatus.hasUnauthorized());
	}
}
