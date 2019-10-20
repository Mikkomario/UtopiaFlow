package utopia.java.flow.async;

import java.time.Duration;
import java.util.function.Supplier;

import utopia.java.flow.structure.Option;
import utopia.java.flow.util.WaitTarget;

/**
 * A loop is a background process that keeps repeating either indefinitely or until a certain condition is met. Loops 
 * will always run at least once. All loops will close once the JVM closes. This class doesn't have value semantics 
 * since it implements {@link Breakable}
 * @author Mikko Hilpinen
 * @since 20.12.2018
 */
public class StaticIntervalLoop extends Loop
{
	// ATTRIBUTES	------------------
	
	private Runnable operation;
	private Duration interval;
	private Option<Supplier<Boolean>> continueCheck;
	
	
	// CONSTRUCTOR	------------------
	
	/**
	 * Creates a new looping process
	 * @param operation The operation that will be looped
	 * @param interval The interval between the repeats
	 * @param continueCheck A check that will be called between each run to see whether the loop should 
	 * continue. Optional.
	 */
	public StaticIntervalLoop(Runnable operation, Duration interval, Option<Supplier<Boolean>> continueCheck)
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
	public StaticIntervalLoop(Runnable operation, Duration interval, Supplier<Boolean> continueCheck)
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
	public static StaticIntervalLoop forever(Runnable operation, Duration interval)
	{
		return new StaticIntervalLoop(operation, interval, Option.none());
	}
	
	/**
	 * Creates a loop that continues until the JVM closes or until broken
	 * @param operation The operation that will be looped
	 * @param interval The interval between the loops
	 * @return A new loop that will not terminate by itself until the JVM closes
	 */
	public static StaticIntervalLoop untilJVMCloses(Runnable operation, Duration interval)
	{
		StaticIntervalLoop loop = forever(operation, interval);
		LoopCloseHook.getInstance().register(loop);
		return loop;
	}
	
	
	// IMPLEMENTED	------------------
	
	@Override
	protected boolean runOnce()
	{
		operation.run();
		return continueCheck.map(c -> c.get()).getOrElse(true);
	}

	@Override
	protected WaitTarget getNextWaitTarget()
	{
		return WaitTarget.withDuration(interval);
	}
	
	
	// OTHER	-----------------------
	
	/**
	 * Creates another loop that has an additional check before continuing
	 * @param check The additional continue check
	 * @return A new loop that will also check the provided check before continuing
	 */
	public StaticIntervalLoop withAdditionalCheck(Supplier<Boolean> check)
	{
		Supplier<Boolean> finalCheck;
		if (continueCheck.isDefined())
		{
			Supplier<Boolean> original = continueCheck.get();
			finalCheck = () -> original.get() && check.get();
		}
		else
			finalCheck = check;
		
		return new StaticIntervalLoop(operation, interval, finalCheck);
	}
}
