package utopia.flow.util;

import java.util.function.Supplier;

/**
 * This interface allows a supplier to throw an exception, wrapping the results
 * @author Mikko Hilpinen
 * @since 5 Dec 2017
 * @param <T> The type of result supplied on success
 */
@FunctionalInterface
public interface ThrowingSupplier<T> extends Supplier<Try<T>>
{
	/**
	 * Supplies a new value. May fail.
	 * @return The generated value
	 * @throws Exception Throws an exception on failure
	 */
	public T throwingGet() throws Exception;
	
	@Override
	public default Try<T> get()
	{
		try
		{
			return Try.success(throwingGet());
		}
		catch (Exception e)
		{
			return Try.failure(e);
		}
	}
}
