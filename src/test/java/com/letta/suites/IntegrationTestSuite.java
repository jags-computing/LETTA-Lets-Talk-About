package com.letta.suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.letta.rest.PeopleResourceTest;
import com.letta.rest.UsersResourceTest;

@SuiteClasses({ 
	PeopleResourceTest.class,
	UsersResourceTest.class
})
@RunWith(Suite.class)
public class IntegrationTestSuite {
}
