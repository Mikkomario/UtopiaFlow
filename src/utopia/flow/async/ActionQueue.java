package utopia.flow.async;

import utopia.flow.structure.Option;
import utopia.flow.structure.Pair;

/**
 * Action queues are used for performing multiple asynchronous operations back to back
 * @author Mikko Hilpinen
 * @since 28.3.2019
 */
public class ActionQueue
{
	// ATTRIBUTES	----------------
	
	private VolatileList<Pair<Runnable, Completion>> actions = new VolatileList<>();
	private Volatile<Completion> currentRunCompleted = new Volatile<>(Completion.fulfilled());
	
	
	// OTHER	--------------------
	
	/**
	 * Pushes a new action to this action queue. The action will be run asynchronously once the 
	 * previous actions have finished
	 * @param action The action to be run
	 * @return A completion for that specific action (completes when action finishes)
	 */
	public Completion push(Runnable action)
	{
		// Adds the task to the queue
		Completion taskCompletion = new Completion();
		actions.add(new Pair<>(action, taskCompletion));
		
		// If the queue is not running, starts it
		currentRunCompleted.update(status -> 
		{
			if (status.isEmpty())
				return status;
			else
				return processData();
		});
		
		return taskCompletion;
	}
	
	private Completion processData()
	{
		return Completion.ofAsynchronous(() -> 
		{
			// Iterates as long as actions remain
			while (!actions.isEmpty())
			{
				Option<Pair<Runnable, Completion>> next = actions.pop();
				
				if (next.isEmpty())
					break;
				else
				{
					// Performs the action
					next.get().getFirst().run();
					// Completes the associated completion
					next.get().getSecond().fulfill();
				}
			}
		});
	}
}
