package utopia.flow.function;

import utopia.flow.structure.Option;

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
