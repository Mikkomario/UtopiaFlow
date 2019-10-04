package utopia.flow.function;

import java.util.function.BiFunction;

import utopia.java.flow.structure.Try;

/**
 * Throwing functions allow use of functional interfaces for throwing operations. The function results are wrapped 
 * in a try instance
 * @author Mikko Hilpinen
 * @since 5 Dec 2017
 * @param <T> The type of the first input parameter
 * @param <U> The type of the second input parameter
 * @param <R> The type of a successful output
 * @param <E> Type of exception thrown
 */
@FunctionalInterface
public interface ThrowingBiFunction<T, U, R, E extends Exception> extends BiFunction<T, U, Try<R>>
{
	/**
	 * Processes a parameter. May fail.
	 * @param p1 the first input parameter
	 * @param p2 The second input parameter
	 * @return The return value for the parameter
	 * @throws E Throws an exception on failure
	 */
	public R throwingApply(T p1, U p2) throws E;
	
	@Override
	public default Try<R> apply(T p1, U p2)
	{
		try
		{
			return Try.success(throwingApply(p1, p2));
		}
		catch (Exception e)
		{
			return Try.failure(e);
		}
	}
}
