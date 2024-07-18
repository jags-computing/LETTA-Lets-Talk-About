package com.letta.rest;

import static javax.ws.rs.client.Entity.*;
import static org.hamcrest.CoreMatchers.*;

import java.io.IOException;
import java.util.List;

import javax.sql.DataSource;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
import com.letta.Letta;
import com.letta.dataset.PeopleDataset;
import com.letta.dataset.UsersDataset;
import com.letta.entities.Person;
import com.letta.listeners.ApplicationContextBinding;
import com.letta.listeners.ApplicationContextJndiBindingTestExecutionListener;
import com.letta.listeners.DbManagement;
import com.letta.listeners.DbManagementTestExecutionListener;
import com.letta.matchers.HasHttpStatus;
import com.letta.matchers.IsEqualToPerson;

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
public class PeopleResourceTest extends JerseyTest {
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
	public void testList() throws IOException {
		final Response response = target("people").request()
			.header("Authorization", "Basic " + UsersDataset.userToken(UsersDataset.adminLogin()))
		.get();
		MatcherAssert.assertThat(response, HasHttpStatus.hasOkStatus());

		final List<Person> people = response.readEntity(new GenericType<List<Person>>(){});
		
		MatcherAssert.assertThat(people, IsEqualToPerson.containsPeopleInAnyOrder(PeopleDataset.people()));
	}
	
	@Test
	public void testListUnauthorized() throws IOException {
		final Response response = target("people").request()
			.header("Authorization", "Basic " + UsersDataset.userToken(UsersDataset.normalLogin()))
		.get();
		MatcherAssert.assertThat(response, HasHttpStatus.hasUnauthorized());
	}

	@Test
	public void testGet() throws IOException {
		final Response response = target("people/" + PeopleDataset.existentId()).request()
			.header("Authorization", "Basic " + UsersDataset.userToken(UsersDataset.adminLogin()))
		.get();
		MatcherAssert.assertThat(response, HasHttpStatus.hasOkStatus());
		
		final Person person = response.readEntity(Person.class);
		
		MatcherAssert.assertThat(person, CoreMatchers.is(IsEqualToPerson.equalsToPerson(PeopleDataset.existentPerson())));
	}
	
	@Test
	public void testGetUnauthorized() throws IOException {
		final Response response = target("people/" + PeopleDataset.existentId()).request()
			.header("Authorization", "Basic " + UsersDataset.userToken(UsersDataset.normalLogin()))
		.get();
		MatcherAssert.assertThat(response, HasHttpStatus.hasUnauthorized());
	}

	@Test
	public void testGetInvalidId() throws IOException {
		final Response response = target("people/" + PeopleDataset.nonExistentId()).request()
			.header("Authorization", "Basic " + UsersDataset.userToken(UsersDataset.adminLogin()))
		.get();
		
		MatcherAssert.assertThat(response, HasHttpStatus.hasBadRequestStatus());
	}

	@Test
	@ExpectedDatabase("/datasets/dataset-add.xml")
	public void testAdd() throws IOException {
		final Form form = new Form();
		form.param("name", PeopleDataset.newName());
		form.param("surname", PeopleDataset.newSurname());
		
		final Response response = target("people").request(MediaType.APPLICATION_JSON_TYPE)
			.header("Authorization", "Basic " + UsersDataset.userToken(UsersDataset.adminLogin()))
		.post(entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
		MatcherAssert.assertThat(response, HasHttpStatus.hasOkStatus());
		
		final Person person = response.readEntity(Person.class);
		
		MatcherAssert.assertThat(person, CoreMatchers.is(IsEqualToPerson.equalsToPerson(PeopleDataset.newPerson())));
	}
	
	@Test
	public void testAddUnauthorized() throws IOException {
		final Form form = new Form();
		form.param("name", PeopleDataset.newName());
		form.param("surname", PeopleDataset.newSurname());
		
		final Response response = target("people").request(MediaType.APPLICATION_JSON_TYPE)
			.header("Authorization", "Basic " + UsersDataset.userToken(UsersDataset.normalLogin()))
		.post(entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

		MatcherAssert.assertThat(response, HasHttpStatus.hasUnauthorized());
	}

	@Test
	public void testAddMissingName() throws IOException {
		final Form form = new Form();
		form.param("surname", PeopleDataset.newSurname());
		
		final Response response = target("people").request(MediaType.APPLICATION_JSON_TYPE)
			.header("Authorization", "Basic " + UsersDataset.userToken(UsersDataset.adminLogin()))
		.post(entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
		
		MatcherAssert.assertThat(response, HasHttpStatus.hasBadRequestStatus());
	}

	@Test
	public void testAddMissingSurname() throws IOException {
		final Form form = new Form();
		form.param("name", PeopleDataset.newName());
		
		final Response response = target("people").request(MediaType.APPLICATION_JSON_TYPE)
			.header("Authorization", "Basic " + UsersDataset.userToken(UsersDataset.adminLogin()))
		.post(entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
		
		MatcherAssert.assertThat(response, HasHttpStatus.hasBadRequestStatus());
	}

	@Test
	@ExpectedDatabase("/datasets/dataset-modify.xml")
	public void testModify() throws IOException {
		final Form form = new Form();
		form.param("name", PeopleDataset.newName());
		form.param("surname", PeopleDataset.newSurname());
		
		final Response response = target("people/" + PeopleDataset.existentId()).request(MediaType.APPLICATION_JSON_TYPE)
			.header("Authorization", "Basic " + UsersDataset.userToken(UsersDataset.adminLogin()))
		.put(entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
		MatcherAssert.assertThat(response, HasHttpStatus.hasOkStatus());
		
		final Person modifiedPerson = response.readEntity(Person.class);
		
		final Person person = PeopleDataset.existentPerson();
		person.setName(PeopleDataset.newName());
		person.setSurname(PeopleDataset.newSurname());
		
		MatcherAssert.assertThat(modifiedPerson, CoreMatchers.is(IsEqualToPerson.equalsToPerson(person)));
	}

	@Test
	public void testModifyUnauthorized() throws IOException {
		final Form form = new Form();
		form.param("name", PeopleDataset.newName());
		form.param("surname", PeopleDataset.newSurname());
		
		final Response response = target("people/" + PeopleDataset.existentId()).request(MediaType.APPLICATION_JSON_TYPE)
			.header("Authorization", "Basic " + UsersDataset.userToken(UsersDataset.normalLogin()))
		.put(entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

		MatcherAssert.assertThat(response, HasHttpStatus.hasUnauthorized());
	}

	@Test
	public void testModifyName() throws IOException {
		final Form form = new Form();
		form.param("name", PeopleDataset.newName());
		
		final Response response = target("people/" + PeopleDataset.existentId()).request(MediaType.APPLICATION_JSON_TYPE)
			.header("Authorization", "Basic " + UsersDataset.userToken(UsersDataset.adminLogin()))
		.put(entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

		MatcherAssert.assertThat(response, HasHttpStatus.hasBadRequestStatus());
	}

	@Test
	public void testModifySurname() throws IOException {
		final Form form = new Form();
		form.param("surname", PeopleDataset.newSurname());
		
		final Response response = target("people/" + PeopleDataset.existentId()).request(MediaType.APPLICATION_JSON_TYPE)
			.header("Authorization", "Basic " + UsersDataset.userToken(UsersDataset.adminLogin()))
		.put(entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
		
		MatcherAssert.assertThat(response, HasHttpStatus.hasBadRequestStatus());
	}

	@Test
	public void testModifyInvalidId() throws IOException {
		final Form form = new Form();
		form.param("name", PeopleDataset.newName());
		form.param("surname", PeopleDataset.newSurname());
		
		final Response response = target("people/" + PeopleDataset.nonExistentId()).request(MediaType.APPLICATION_JSON_TYPE)
			.header("Authorization", "Basic " + UsersDataset.userToken(UsersDataset.adminLogin()))
		.put(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

		MatcherAssert.assertThat(response, HasHttpStatus.hasBadRequestStatus());
	}

	@Test
	@ExpectedDatabase("/datasets/dataset-delete.xml")
	public void testDelete() throws IOException {
		final Response response = target("people/" + PeopleDataset.existentId()).request()
			.header("Authorization", "Basic " + UsersDataset.userToken(UsersDataset.adminLogin()))
		.delete();
		
		MatcherAssert.assertThat(response, HasHttpStatus.hasOkStatus());
		
		final Integer deletedId = response.readEntity(Integer.class);
		
		MatcherAssert.assertThat(deletedId, CoreMatchers.is(equalTo(PeopleDataset.existentId())));
	}
	
	@Test
	public void testDeleteUnauthorized() throws IOException {
		final Response response = target("people/" + PeopleDataset.existentId()).request()
			.header("Authorization", "Basic " + UsersDataset.userToken(UsersDataset.normalLogin()))
		.delete();
		
		MatcherAssert.assertThat(response, HasHttpStatus.hasUnauthorized());
	}

	@Test
	public void testDeleteInvalidId() throws IOException {
		final Response response = target("people/" + PeopleDataset.nonExistentId()).request()
			.header("Authorization", "Basic " + UsersDataset.userToken(UsersDataset.adminLogin()))
		.delete();

		MatcherAssert.assertThat(response, HasHttpStatus.hasBadRequestStatus());
	}
}
