package com.letta.suites;

import com.letta.entities.PersonUnitTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@SuiteClasses({
	PersonUnitTest.class
})
@RunWith(Suite.class)
public class UnitTestSuite {
}
