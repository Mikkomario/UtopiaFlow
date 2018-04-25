package utopia.flow.util;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * This is a static collection of methods that are used for waiting / blocking on threads. This class takes responsibility 
 * for handling synchronized blocks and interrupted exceptions
 * @author Mikko Hilpinen
 * @since 25.4.2018
 */
public class WaitUtils
{
	// CONSTRUCTOR	-------------------
	
	private WaitUtils() { }

	
	// OTHER METHODS	---------------
	
	/**
	 * Waits a certain amount of milliseconds
	 * @param millis The amount of milliseconds waited
	 * @param lock The object used as a lock
	 */
	public static void wait(long millis, Object lock)
	{
		long targetMillis = System.currentTimeMillis() + millis;
		
		synchronized (lock)
		{
			long currentMillis = System.currentTimeMillis();
			
			while (currentMillis < targetMillis)
			{
				try
				{
					lock.wait(targetMillis - currentMillis);
				}
				catch (InterruptedException e)
				{
					// Ignored
				}
				
				currentMillis = System.currentTimeMillis();
			}
		}
	}
	
	/**
	 * Waits until a specified instant
	 * @param target The target instant
	 * @param lock The object used as a lock
	 */
	public static void waitUntil(Instant target, Object lock)
	{
		long waitMillis = ChronoUnit.MILLIS.between(Instant.now(), target);
		wait(waitMillis, lock);
	}
}
