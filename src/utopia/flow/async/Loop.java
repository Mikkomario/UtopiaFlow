package utopia.flow.async;

import utopia.flow.structure.WeakList;
import utopia.flow.util.WaitTarget;

/**
 * This is an abstract class for looping, breakable operations that handles continuing checks and waiting between 
 * iterations, as well as breaking the loop
 * @author Mikko Hilpinen
 * @since 31.12.2018
 */
public abstract class Loop implements Runnable, Breakable
{
	// ATTRIBUTES	------------------
	
	private final Object waitLock = new Object();
	
	private final VolatileFlag breakFlag = new VolatileFlag();
	private final VolatileFlag runningFlag = new VolatileFlag();
	private WeakList<Completion> stopCompletions = WeakList.empty();
	
	
	// ABSTRACT	----------------------
	
	/**
	 * @return Perfomrs the main operation of this loop once
	 */
	protected abstract boolean runOnce();
	
	/**
	 * This method is used for checking how long the loop should wait before calling {@link #runOnce()} next time
	 * @return A wait target for the next iteration
	 */
	protected abstract WaitTarget getNextWaitTarget();
	
	
	// IMPLEMENTED	------------------
	
	@Override
	public void run()
	{
		// Starts the loop
		breakFlag.reset();
		runningFlag.set();
		
		boolean isContinuing = true;
		do
		{
			// Performs the operation, checks whether should be continued
			isContinuing = runOnce();
			
			// Waits between continues
			if (isContinuing && !isBroken())
				getNextWaitTarget().waitWith(waitLock);
		}
		// Loops until broken or finished
		while (isContinuing && !isBroken());
		
		// Finishes loop & informs listeners
		runningFlag.reset();
		stopCompletions.forEach(c -> c.fulfill());
	}
	
	@Override
	public Completion stop()
	{
		if (isRunning())
		{
			// If this loop is running, has to wait until it has ended
			// Creates the completion and registers it to listen to the end of this loop
			Completion completion = new Completion();
			stopCompletions = stopCompletions.plus(completion);
			
			// Breaks this loop (asynchronous)
			breakFlag.set();
			WaitTarget.notify(waitLock);
			
			return completion;
		}
		// If this loop had already finished, simply returns a fulfilled completion
		else
			return Completion.fulfilled();
	}
	
	
	// OTHER	-----------------------
	
	/**
	 * @return Whether this loop is currently running
	 */
	public boolean isRunning()
	{
		return runningFlag.isSet();
	}
	
	/**
	 * @return Whether this loop has been broken
	 */
	public boolean isBroken()
	{
		return breakFlag.isSet();
	}
	
	/**
	 * @return A wait lock used by this loop. Should be used in all breakable waits concerning this loop.
	 */
	protected Object getWaitLock()
	{
		return waitLock;
	}
}
