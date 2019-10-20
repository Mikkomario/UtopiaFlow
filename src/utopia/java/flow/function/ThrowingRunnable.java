package utopia.java.flow.function;

import utopia.java.flow.structure.Option;
import utopia.java.flow.structure.Try;
import utopia.java.flow.util.Unit;

/**
 * Throwing runnables are used instead of runnables in environments that allow throwing of exceptions
 * @author Mikko Hilpinen
 * @param <E> The type of exception thrown by this runnable
 * @since 13 Dec 2017
 */
@FunctionalInterface
public interface ThrowingRunnable<E extends Exception>
{
	/**
	 * Performs the operation in this runnable. May fail.
	 * @throws Exception An exception is thrown on failure
	 */
	public void run() throws E;
	
	/**
	 * Performs this operation. Returns the result.
	 * @return Success if operation ran without exceptions. Failure otherwise.
	 */
	public default Try<Unit> tryRun()
	{
		try
		{
			run();
			return Try.SUCCESS;
		}
		catch (Exception e)
		{
			return Try.failure(e);
		}
	}
	
	/**
	 * Performs the operation in this runnable. If the operation fails, catches the exception and returns it
	 * @return The exception that caused the runnable to fail. None if the runnable succeeded.
	 */
	public default Option<Exception> runAndCatch()
	{
		try
		{
			run();
			return Option.none();
		}
		catch (Exception e)
		{
			return Option.some(e);
		}
	}
}
