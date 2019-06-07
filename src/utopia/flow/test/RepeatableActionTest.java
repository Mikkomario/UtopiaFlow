package utopia.flow.test;

import java.time.Duration;

import utopia.flow.async.RepeatableAction;
import utopia.flow.async.Volatile;
import utopia.flow.structure.IntRange;
import utopia.flow.util.Test;
import utopia.flow.util.WaitUtils;

/**
 * This is a unit test for the RepeatableAction class
 * @author Mikko Hilpinen
 * @since 7.6.2019
 */
public class RepeatableActionTest
{
	@SuppressWarnings("javadoc")
	public static void main(String[] args)
	{
		Volatile<Integer> completionCounter = new Volatile<>(0);
		RepeatableAction action = new RepeatableAction(() -> action(completionCounter));
		Object creationWaitLock = new Object();
		
		// Quickly queues multiple actions, should only complete twice
		IntRange.fromTo(1, 5).forEach(i -> 
		{
			System.out.println("Queuing action");
			action.runAsync();
			WaitUtils.wait(Duration.ofMillis(50), creationWaitLock);
		}); 
		
		// Waits until actions have completed
		System.out.println("Waiting...");
		WaitUtils.wait(Duration.ofMillis(1000 * 5), creationWaitLock);
		
		Test.checkEquals(completionCounter.get(), 2);
		
		System.out.println("Success!");
	}
	
	private static void action(Volatile<Integer> counter)
	{
		System.out.println("Starting action");
		WaitUtils.wait(Duration.ofMillis(1000), new Object());
		System.out.println("Ending action");
		counter.update(i -> i + 1);
	}
}
