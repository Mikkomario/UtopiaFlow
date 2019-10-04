package utopia.flow.function;

import utopia.java.flow.structure.Try;
import utopia.java.flow.util.Unit;

/**
 * This function can be used for error prone value handling
 * @author Mikko Hilpinen
 * @param <A> The first parameter type
 * @param <B> The second parameter type
 * @param <E> The type of exception thrown by this consumer
 * @since 14.5.2018
 */
@FunctionalInterface
public interface ThrowingBiConsumer<A, B, E extends Exception>
{
	/**
	 * Handles the input values. May fail.
	 * @param a The first input value
	 * @param b The second input value
	 * @throws E Exceptions are thrown on failure
	 */
	public void accept(A a, B b) throws E;
	
	/**
	 * Handles the input values, caching any exceptions
	 * @param a The first input value
	 * @param b The second input value
	 * @return A try that contains the failure if there is one
	 */
	public default Try<Unit> tryAccept(A a, B b)
	{
		return Try.run(() -> accept(a, b));
	}
}
