package utopia.flow.util;

/**
 * Throwing runnables are used instead of runnables in environments that allow throwing of exceptions
 * @author Mikko Hilpinen
 * @since 13 Dec 2017
 */
@FunctionalInterface
public interface ThrowingRunnable
{
	/**
	 * Performs the operation in this runnable. May fail.
	 * @throws Exception An exception is thrown on failure
	 */
	public void run() throws Exception;
	
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
