package utopia.java.flow.util;

import java.time.Duration;
import java.time.Instant;

import utopia.flow.async.Breakable;
import utopia.flow.async.Completion;
import utopia.flow.async.VolatileFlag;

/**
 * This class represents a single waiting operation. Since waits don't have value semantics, they should not be 
 * shared or reused.
 * @author Mikko Hilpinen
 * @since 31.12.2018
 */
public class Wait implements Breakable, Runnable, Copyable<Wait>
{
	// ATTRIBUTES	--------------------
	
	private VolatileFlag startedFlag = new VolatileFlag();
	private WaitTarget target;
	
	private Object lock = new Object();
	private Completion completion = new Completion();
	
	
	// CONSTRUCTOR	--------------------
	
	/**
	 * Creates a new wait operation based on provided wait time target
	 * @param target The target wait time
	 */
	public Wait(WaitTarget target)
	{
		this.target = target;
	}
	
	/**
	 * Creates a new wait instance with specified wait end time
	 * @param time The natural end time of the wait
	 * @return A new wait operation to be ran
	 */
	public static Wait until(Instant time)
	{
		return new Wait(WaitTarget.withEndTime(time));
	}
	
	/**
	 * Creates a new wait instance with specified wait duration
	 * @param duration The natural duration of the wait
	 * @return A new wait operation to be ran
	 */
	public static Wait duration(Duration duration)
	{
		return new Wait(WaitTarget.withDuration(duration));
	}
	
	/**
	 * @return A new wait instance that won't stop until broken
	 */
	public static Wait untilBroken()
	{
		return new Wait(WaitTarget.untilNotified());
	}
	
	
	// IMPLEMENTED	--------------------
	
	@Override
	public void run()
	{
		// If the wait had already started, simply waits until the original run has been completed
		if (startedFlag.getAndSet())
			completion.waitFor();
		else
		{
			// Performs the wait, then finishes the completion
			target.breakable().waitWith(lock);
			completion.fulfill();
		}
	}

	@Override
	public Completion stop()
	{
		synchronized (lock) { lock.notifyAll(); }
		return completion;
	}
	
	@Override
	public Wait copy()
	{
		return new Wait(target);
	}

	
	// ACCESSORS	--------------------
	
	/**
	 * @return The lock used in this wait
	 */
	public Object getWaitLock()
	{
		return lock;
	}
	
	/**
	 * @return Whether this wait operation has already started
	 */
	public boolean hasStarted()
	{
		return startedFlag.get();
	}
	
	/**
	 * @return The completion of this wait operation. Will not complete before {@link #run()} is called
	 */
	public Completion completion()
	{
		return completion;
	}
}
