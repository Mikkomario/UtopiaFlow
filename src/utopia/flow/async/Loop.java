package utopia.flow.async;

import java.time.Duration;
import java.util.function.Supplier;

import utopia.flow.structure.Option;
import utopia.flow.structure.WeakList;
import utopia.flow.util.WaitUtils;

/**
 * A loop is a background process that keeps repeating either indefinitely or until a certain condition is met. Loops 
 * will always run at least once. All loops will close once the JVM closes. This class doesn't have value semantics 
 * since it implements {@link Breakable}
 * @author Mikko Hilpinen
 * @since 20.12.2018
 */
public class Loop implements Runnable, Breakable
{
	// ATTRIBUTES	------------------
	
	private Runnable operation;
	private Duration interval;
	private Option<Supplier<Boolean>> continueCheck;
	
	private VolatileFlag breakFlag = new VolatileFlag();
	private VolatileFlag runningFlag = new VolatileFlag();
	private WeakList<Completion> stopCompletions = WeakList.empty();
	
	
	// CONSTRUCTOR	------------------
	
	/**
	 * Creates a new looping process
	 * @param operation The operation that will be looped
	 * @param interval The interval between the repeats
	 * @param continueCheck A check that will be called between each run to see whether the loop should 
	 * continue. Optional.
	 */
	public Loop(Runnable operation, Duration interval, Option<Supplier<Boolean>> continueCheck)
	{
		this.operation = operation;
		this.interval = interval;
		this.continueCheck = continueCheck;
	}
	
	/**
	 * Creates a new looping process
	 * @param operation The operation that will be looped
	 * @param interval The interval between the repeats
	 * @param continueCheck A check that will be called between each run to see whether the loop should 
	 * continue. Optional.
	 */
	public Loop(Runnable operation, Duration interval, Supplier<Boolean> continueCheck)
	{
		this.operation = operation;
		this.interval = interval;
		this.continueCheck = Option.some(continueCheck);
	}
	
	/**
	 * Creates a loop that continues forever or until broken
	 * @param operation The operation that will be looped
	 * @param interval The interval between the loops
	 * @return A new loop that will not terminate by itself
	 */
	public static Loop forever(Runnable operation, Duration interval)
	{
		return new Loop(operation, interval, Option.none());
	}
	
	/**
	 * Creates a loop that continues until the JVM closes or until broken
	 * @param operation The operation that will be looped
	 * @param interval The interval between the loops
	 * @return A new loop that will not terminate by itself until the JVM closes
	 */
	public static Loop untilJVMCloses(Runnable operation, Duration interval)
	{
		Loop loop = forever(operation, interval);
		LoopCloseHook.getInstance().register(loop);
		return loop;
	}
	
	
	// IMPLEMENTED	------------------
	
	@Override
	public void run()
	{
		breakFlag.reset();
		runningFlag.set();
		
		do
		{
			operation.run();
			WaitUtils.wait(interval, this);
		}
		while (!breakFlag.isSet() && continueCheck.forAll(c -> c.get()));
		
		runningFlag.reset();
		stopCompletions.forEach(c -> c.fulfill());
	}
	
	@Override
	public Completion stop()
	{
		if (runningFlag.isSet())
		{
			// If this loop is running, has to wait until it has ended
			// Creates the completion and registers it to listen to the end of this loop
			Completion completion = new Completion();
			stopCompletions = stopCompletions.plus(completion);
			
			// Breaks this loop (asynchronous)
			breakFlag.set();
			WaitUtils.notify(this);
			
			return completion;
		}
		// If this loop had already finished, simply returns a fulfilled completion
		else
			return Completion.fulfilled();
	}
	
	
	// OTHER	-----------------------
	
	/**
	 * Creates another loop that has an additional check before continuing
	 * @param check The additional continue check
	 * @return A new loop that will also check the provided check before continuing
	 */
	public Loop withAdditionalCheck(Supplier<Boolean> check)
	{
		Supplier<Boolean> finalCheck;
		if (continueCheck.isDefined())
		{
			Supplier<Boolean> original = continueCheck.get();
			finalCheck = () -> original.get() && check.get();
		}
		else
			finalCheck = check;
		
		return new Loop(operation, interval, finalCheck);
	}
}
