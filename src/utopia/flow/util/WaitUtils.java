package utopia.flow.util;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
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
	 * Waits until the lock is notified
	 * @param lock The locking object
	 * @see #notify(Object)
	 */
	public static void waitUntilNotified(Object lock)
	{
		synchronized (lock)
		{
			boolean waiting = true;
			while (waiting)
			{
				try
				{
					lock.wait();
					waiting = false;
				}
				catch (InterruptedException e) { }
			}
		}
	}
	
	/**
	 * Notifies a lock object and releases any thread waiting on the said object. Used with {@link #waitUntilNotified(Object)}
	 * @param lock A locking object
	 */
	public static void notify(Object lock)
	{
		synchronized (lock)
		{
			lock.notifyAll();
		}
	}
	
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
	 * Waits a certain amount of milliseconds
	 * @param duration The duration of the wait
	 * @param lock The object used as a lock
	 */
	public static void wait(Duration duration, Object lock)
	{
		wait(duration.toMillis(), lock);
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
	
	/**
	 * Waits until a specified local time
	 * @param target The target instant
	 * @param lock The object used as a lock
	 */
	public static void waitUntil(LocalDateTime target, Object lock)
	{
		long waitMillis = ChronoUnit.MILLIS.between(LocalDateTime.now(), target);
		wait(waitMillis, lock);
	}
}
