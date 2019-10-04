package utopia.java.flow.util;

import java.time.Duration;
import java.time.Instant;

import utopia.java.flow.structure.Either;
import utopia.java.flow.structure.Option;

/**
 * This class represents a waiting time, which may be until specified instant, a duration or an "infinite" time period
 * @author Mikko Hilpinen
 * @since 31.12.2018
 */
public class WaitTarget
{
	// ATTRIBUTES	---------------------
	
	private Option<Either<Duration, Instant>> targetTime;
	private boolean breakOnNotify;
	
	
	// CONSTRUCTOR	---------------------
	
	private WaitTarget(Option<Either<Duration, Instant>> target, boolean breakOnNotify)
	{
		this.targetTime = target;
		this.breakOnNotify = breakOnNotify;
	}
	
	/**
	 * @return A new wait target that won't end without notifying the lock
	 */
	public static WaitTarget untilNotified()
	{
		return new WaitTarget(Option.none(), true);
	}
	
	/**
	 * @param duration The wait duration once started
	 * @param breakOnNotify Whether the wait should be broken on notify
	 * @return A wait target with specified duration for each wait
	 */
	public static WaitTarget withDuration(Duration duration, boolean breakOnNotify)
	{
		return new WaitTarget(Option.some(Either.left(duration)), breakOnNotify);
	}
	
	/**
	 * @param duration The wait duration once started
	 * @return A wait target with specified duration for each wait
	 */
	public static WaitTarget withDuration(Duration duration)
	{
		return withDuration(duration, true);
	}
	
	/**
	 * @param time The wait end time
	 * @param breakOnNotify Whether the wait should be broken on notify
	 * @return A wait target with specified wait end time for all calls
	 */
	public static WaitTarget withEndTime(Instant time, boolean breakOnNotify)
	{
		return new WaitTarget(Option.some(Either.right(time)), breakOnNotify);
	}
	
	/**
	 * @param time The wait end time
	 * @return A wait target with specified wait end time for all calls
	 */
	public static WaitTarget withEndTime(Instant time)
	{
		return withEndTime(time, true);
	}
	
	
	// ACCESSORS	---------------------
	
	/**
	 * @return Whether the wait should break / end when the lock is notified
	 */
	public boolean breaksOnNotify()
	{
		return breakOnNotify;
	}
	
	/**
	 * @return Whether this wait is infinite (not counting possible breaks)
	 */
	public boolean isInfinite()
	{
		return targetTime.isEmpty();
	}
	
	/**
	 * @return Whether thsi wait will end naturally, eventually
	 */
	public boolean isFinite()
	{
		return !isInfinite();
	}
	
	/**
	 * @return The duration of the wait, if available. May vary based on call time.
	 */
	public Option<Duration> getDuration()
	{
		return targetTime.map(t -> t.handleMap(d -> d, i -> Duration.between(Instant.now(), i)));
	}
	
	/**
	 * @return The end time of the wait, if available. May vary based on call time.
	 */
	public Option<Instant> getEndTime()
	{
		return targetTime.map(t -> t.handleMap(d -> Instant.now().plus(d), i -> i));
	}
	
	/**
	 * @return whether this wait target is specified by wait duration
	 */
	public boolean isDurationSpecified()
	{
		return targetTime.exists(t -> t.isLeft());
	}
	
	/**
	 * @return Whether this wait target is specified by end time
	 */
	public boolean isEndTimeSpecified()
	{
		return targetTime.exists(t -> t.isRight());
	}
	
	
	// OTHER	------------------------
	
	/**
	 * Performs the actual wait. The wait may or may not be breakable by notifying the lock (depends on WaitTarget 
	 * configurations)
	 * @param lock The object that will be locked during the wait
	 */
	public void waitWith(Object lock)
	{
		if (isFinite())
		{
			boolean waitCompleted = false;
			Instant targetTime = getEndTime().get();
			
			synchronized (lock)
			{
				Instant currentTime = Instant.now();
				
				while (!waitCompleted && currentTime.isBefore(targetTime))
				{
					try
					{
						Duration waitDuration = Duration.between(currentTime, targetTime);
						// Waits in nano precision
						lock.wait(waitDuration.toMillis(), waitDuration.getNano() / 1000);
						
						if (breaksOnNotify())
							waitCompleted = true;
					}
					catch (InterruptedException e)
					{
						// Ignored
					}
					
					currentTime = Instant.now();
				}
			}
		}
		else
		{
			boolean waitCompleted = false;
			synchronized (lock)
			{
				while (!waitCompleted)
				{
					try
					{
						lock.wait();
						waitCompleted = true;
					}
					catch (InterruptedException e)
					{
						// Ignored
					}
				}
			}
		}
	}
	
	/**
	 * Notifies the lock, ending all waits depending from it (unless they have been specified not to end on notify)
	 * @param lock The lock that should be notified
	 */
	public static void notify(Object lock)
	{
		synchronized (lock) { lock.notifyAll(); }
	}
	
	/**
	 * @return A breakable version of this wait target. May return this same instance.
	 */
	public WaitTarget breakable()
	{
		if (breaksOnNotify())
			return this;
		else
			return new WaitTarget(targetTime, true);
	}
	
	/**
	 * @return A single-use wait operation based on this target
	 */
	public Wait toWait()
	{
		return new Wait(this);
	}
}
