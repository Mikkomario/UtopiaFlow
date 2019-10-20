package utopia.java.flow.async;

/**
 * This interface is implemented by classes that represent operations that can be quit / broken before their 
 * natural end
 * @author Mikko Hilpinen
 * @since 20.12.2018
 */
public interface Breakable
{
	// ABSTRACT	--------------------
	
	/**
	 * Stops this operation
	 * @return An asynchronous completion for the stopping operation
	 */
	public Completion stop();
	
	
	// OTHER	-------------------
	
	/**
	 * Registers this loop to stop when JVM is closing
	 */
	public default void registerToStopAtExit()
	{
		LoopCloseHook.getInstance().register(this);
	}
}
