package utopia.flow.util;

import java.util.function.Function;

/**
 * Throwing functions allow use of functional interfaces for throwing operations. The function results are wrapped 
 * in a try instance
 * @author Mikko Hilpinen
 * @since 5 Dec 2017
 * @param <T> The type of the input parameter
 * @param <R> The type of a successful output
 * @param <E> The type of exception thrown by this function
 */
@FunctionalInterface
public interface ThrowingFunction<T, R, E extends Exception> extends Function<T, Try<R>>
{
	/**
	 * Processes a parameter. May fail.
	 * @param param The input parameter
	 * @return The return value for the parameter
	 * @throws Exception Throws an exception on failure
	 */
	public R throwingApply(T param) throws E;
	
	@Override
	public default Try<R> apply(T param)
	{
		try
		{
			return Try.success(throwingApply(param));
		}
		catch (Exception e)
		{
			return Try.failure(e);
		}
	}
}
