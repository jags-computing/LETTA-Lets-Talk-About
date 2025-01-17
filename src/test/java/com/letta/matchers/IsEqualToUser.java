package com.letta.matchers;

import org.hamcrest.Factory;
import org.hamcrest.Matcher;

import com.letta.entities.Person;
import com.letta.entities.User;

public class IsEqualToUser extends IsEqualToEntity<User> {
	public IsEqualToUser(User entity) {
		super(entity);
	}

	@Override
	protected boolean matchesSafely(User actual) {
		this.clearDescribeTo();
		
		if (actual == null) {
			this.addTemplatedDescription("actual", expected.toString());
			return false;
		} else {
			return checkAttribute("login", User::getUsername, actual)
				&& checkAttribute("password", User::getPassword, actual);
		}
	}

	/**
	 * Factory method that creates a new {@link IsEqualToEntity} matcher with
	 * the provided {@link Person} as the expected value.
	 * 
	 * @param user the expected person.
	 * @return a new {@link IsEqualToEntity} matcher with the provided
	 * {@link Person} as the expected value.
	 */
	@Factory
	public static IsEqualToUser equalsToUser(User user) {
		return new IsEqualToUser(user);
	}
	
	/**
	 * Factory method that returns a new {@link Matcher} that includes several
	 * {@link IsEqualToUser} matchers, each one using an {@link Person} of the
	 * provided ones as the expected value.
	 * 
	 * @param users the persons to be used as the expected values.
	 * @return a new {@link Matcher} that includes several
	 * {@link IsEqualToUser} matchers, each one using an {@link Person} of the
	 * provided ones as the expected value.
	 * @see IsEqualToEntity#containsEntityInAnyOrder(java.util.function.Function, Object...)
	 */
	@Factory
	public static Matcher<Iterable<? extends User>> containsPeopleInAnyOrder(User ... users) {
		return containsEntityInAnyOrder(IsEqualToUser::equalsToUser, users);
	}

}
