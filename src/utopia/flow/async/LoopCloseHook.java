package utopia.flow.async;

import java.time.Duration;

import utopia.flow.structure.ImmutableList;
import utopia.flow.structure.Lazy;
import utopia.flow.structure.WeakList;
import utopia.flow.util.WaitUtils;

/** 
 * This single instance class keeps track of various looping processes and closes them once the jvm closes
 * @author Mikko Hilpinen
 * @since 20.12.2018
 */
public class LoopCloseHook
{
	// ATTRIBUTES	----------------------
	
	private static final Duration ADDITIONAL_SHUTDOWN_TIME = Duration.ofMillis(100);
	
	private static final Lazy<LoopCloseHook> INSTANCE = new Lazy<>(LoopCloseHook::new);
	
	private Duration maxShutdownTimeout = Duration.ofSeconds(5);
	private WeakList<Breakable> loops = WeakList.empty();
	
	
	// CONSTRUCTOR	----------------------
	
	private LoopCloseHook()
	{
		// Calls the shutdown method once the JVM is about to close
		Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
	}
	
	/**
	 * @return The singular instance of this class
	 */
	public static LoopCloseHook getInstance()
	{
		return INSTANCE.get();
	}
	
	
	// ACCESSORS	----------------------
	
	/**
	 * Changes the maximum timeout for shutDown
	 * @param maxTimeout The new maximum timeout for shutdown
	 */
	public void setMaxShutdownTimeout(Duration maxTimeout)
	{
		this.maxShutdownTimeout = maxTimeout;
	}
	
	
	// OTHER	--------------------------
	
	/**
	 * Registers a new loop to this hook. The loop will only be weakly referenced so if it's no longer 
	 * referenced by other objects, it will be garbage collected and won't be broken by this instance.
	 * @param loop The loop that should be broken upon shutdown
	 */
	public void register(Breakable loop)
	{
		loops = loops.plus(loop);
	}
	
	/**
	 * Stops down all linked resources. Blocks until the resources have closed
	 * @return Completions of shutdowns that didn't complete in maximum shutdown duration
	 */
	public ImmutableList<Completion> shutdown()
	{
		// Breaks all loops, may need to wait
		ImmutableList<Completion> shutdownCompletions = loops.toStrongList().map(l -> l.stop());
		
		// Clears the loop list too
		loops = WeakList.empty();
		
		if (!shutdownCompletions.isEmpty())
		{
			Completion allCompleted = Completion.ofMany(shutdownCompletions);
			allCompleted.waitFor(maxShutdownTimeout);
			
			WaitUtils.wait(ADDITIONAL_SHUTDOWN_TIME, new Object());
			
			// Returns failed shutdowns, if there are any
			return shutdownCompletions.filter(c -> c.isEmpty());
		}
		else
			return ImmutableList.empty();
	}
}
