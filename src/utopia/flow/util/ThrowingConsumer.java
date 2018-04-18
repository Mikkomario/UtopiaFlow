package utopia.flow.util;

/**
 * This function can be used for error prone value handling
 * @author Mikko Hilpinen
 * @since 5 Dec 2017
 * @param <T> The type of value handled by this consumer
 * @param <E> The type of exception possibly thrown by this consumer
 */
@FunctionalInterface
public interface ThrowingConsumer<T, E extends Exception>
{
	/**
	 * Handles an input value. May fail.
	 * @param o The input value
	 * @throws E Exceptions are thrown on failure
	 */
	public void accept(T o) throws E;
}
