package utopia.flow.test;

import java.time.Duration;

import utopia.flow.async.ActionQueue;
import utopia.flow.async.Completion;
import utopia.flow.structure.ImmutableList;
import utopia.flow.structure.IntRange;
import utopia.flow.util.WaitUtils;

/**
 * Tests action queue
 * @author Mikko Hilpinen
 * @since 28.3.2019
 */
public class ActionQueueTest
{
	@SuppressWarnings("javadoc")
	public static void main(String[] args)
	{
		// Creates the queue
		ActionQueue queue = new ActionQueue();
		
		// Creates actions
		ImmutableList<Runnable> actions = IntRange.fromTo(1, 5).toList().map(i -> () -> 
		{
			System.out.println("Starting action " + i);
			WaitUtils.wait(Duration.ofSeconds(1), new Object());
			System.out.println("Finishing action " + i);
		});
		
		// Pushes actions to queue
		ImmutableList<Completion> completions = actions.map(queue::push);
		
		Completion.ofMany(completions).waitFor();
		System.out.println("All actions completed. Exiting.");
	}
}
