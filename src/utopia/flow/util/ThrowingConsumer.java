package utopia.flow.util;

/**
 * This function can be used for error prone value handling
 * @author Mikko Hilpinen
 * @since 5 Dec 2017
 * @param <T> The type of value handled by this consumer
 */
@FunctionalInterface
public interface ThrowingConsumer<T>
{
	/**
	 * Handles an input value. May fail.
	 * @param o The input value
	 * @throws Exception Exceptions are thrown on failure
	 */
	public void accept(T o) throws Exception;
}
