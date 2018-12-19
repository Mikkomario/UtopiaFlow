package utopia.flow.function;

import utopia.flow.structure.Try;

/**
 * This predicate may throw exceptions
 * @author Mikko Hilpinen
 * @param <T> The type of tested item
 * @param <E> The type of exception thrown
 * @since 19.12.2018
 */
@FunctionalInterface
public interface ThrowingPredicate<T, E extends Exception>
{
	// ABSTRACT	-----------------------
	
	/**
	 * Tests a value, may throw
	 * @param value The tested value
	 * @return The test results
	 * @throws E Exception on error
	 */
	public boolean test(T value) throws E;
	
	
	// OTHER	-----------------------
	
	/**
	 * Tests a value and caches any exceptions
	 * @param value The tested value
	 * @return The test results. May fail.
	 */
	public default Try<Boolean> tryTest(T value)
	{
		return Try.run(() -> test(value));
	}
}
