package utopia.flow.async;

import utopia.flow.function.ThrowingRunnable;

/**
 * Volatile flags are used for marking singular events (flags) in a multi-threaded environment
 * @author Mikko Hilpinen
 * @since 16.8.2018
 */
public class VolatileFlag extends Volatile<Boolean>
{
	// CONSTRUCTOR	-----------------
	
	/**
	 * Creates a new volatile flag with default value of 'false'
	 */
	public VolatileFlag()
	{
		super(false);
	}

	
	// OTHER	---------------------
	
	/**
	 * Sets this flag to 'true'
	 */
	public void set()
	{
		set(true);
	}
	
	/**
	 * Resets this flag back to 'false'
	 */
	public void reset()
	{
		set(false);
	}
	
	/**
	 * @return Whether the flag is currently set
	 */
	public boolean isSet()
	{
		return get();
	}
	
	/**
	 * Performs an action if and only if this flag hasn't been set yet. Locks the flag during the action
	 * @param action The action that will be ran if this flag hasn't been set
	 */
	public void doIfNotSet(Runnable action)
	{
		lockWhile(status -> 
		{
			if (!status)
				action.run();
		});
	}
	
	/**
	 * Perfoms an action if this flag is not set yet. Otherwise does nothing. The flag will be set after the action 
	 * has been completed.
	 * @param action The action that will be run if this flag is not set yet
	 */
	public void runAndSet(Runnable action)
	{
		update(status -> 
		{
			if (status)
				return status;
			else
			{
				action.run();
				return true;
			}
		});
	}
	
	/**
	 * Perfoms an action if this flag is not set yet. Otherwise does nothing. The flag will be set after the action 
	 * has been completed.
	 * @param action The action that will be run if this flag is not set yet
	 * @throws E If the operation fails (in which case the flag will not be set)
	 */
	public <E extends Exception> void tryAndSet(ThrowingRunnable<? extends E> action) throws E
	{
		tryUpdate(status -> 
		{
			if (status)
				return status;
			else
			{
				action.run();
				return true;
			}
		});
	}
}
