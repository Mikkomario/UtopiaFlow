package utopia.flow.async;

/**
 * These actions are run asynchronously. They can be repeated, but won't get overlapped or 
 * form queues. Can be used with actions like update requests, saves, etc.
 * @author Mikko Hilpinen
 * @since 7.6.2019
 */
public class RepeatableAction
{
	// ATTRIBUTES	--------------------
	
	private Runnable action;
	private Completion lastActionCompletion = Completion.fulfilled();
	private Completion currentActionCompletion = Completion.fulfilled();
	private Completion nextActionCompletion = Completion.fulfilled();
	
	
	// CONSTRUCTOR	--------------------
	
	/**
	 * Creates a new repeatable action
	 * @param action The action that will be repeated
	 */
	public RepeatableAction(Runnable action)
	{
		this.action = action;
	}
	
	
	// OTHER	------------------------
	
	/**
	 * Runs the action, if one hasn't been queued yet
	 * @return The completion of the next action run
	 */
	public synchronized Completion runAsync()
	{
		// If an action is already waiting to be started, doesn't start a new one
		if (lastActionCompletion.isFulfilled())
		{
			nextActionCompletion = currentActionCompletion.continueAsync(() -> action.run());
			lastActionCompletion = currentActionCompletion;
			currentActionCompletion = nextActionCompletion;
		}
		
		return nextActionCompletion;
	}
}
