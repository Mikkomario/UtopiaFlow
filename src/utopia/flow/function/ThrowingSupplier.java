package utopia.flow.function;

import java.util.function.Supplier;

import utopia.flow.structure.Try;

/**
 * This interface allows a supplier to throw an exception, wrapping the results
 * @author Mikko Hilpinen
 * @since 5 Dec 2017
 * @param <T> The type of result supplied on success
 * @param <E> The type of exception thrown by this supplier
 */
@FunctionalInterface
public interface ThrowingSupplier<T, E extends Exception> extends Supplier<Try<T>>
{
	/**
	 * Supplies a new value. May fail.
	 * @return The generated value
	 * @throws Exception Throws an exception on failure
	 */
	public T throwingGet() throws E;
	
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
