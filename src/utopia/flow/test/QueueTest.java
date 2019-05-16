package utopia.flow.test;

import java.time.Duration;

import utopia.flow.async.ActionQueue;
import utopia.flow.util.WaitUtils;

/**
 * This class tests the queue implementation
 * @author Mikko Hilpinen
 * @since 2.7.2018
 */
public class QueueTest
{
	@SuppressWarnings("javadoc")
	public static void main(String[] args)
	{
		// Makes a queue with 5 items and a width of 2
		ActionQueue q = new ActionQueue(2);
		
		q.push(() -> waitAction(0));
		q.push(() -> waitAction(1));
		q.push(() -> waitAction(2));
		q.push(() -> waitAction(3));
		q.push(() -> waitAction(4));
		
		System.out.println("Waiting 15 seconds before program closes");
		WaitUtils.wait(Duration.ofSeconds(15), new String());
		System.out.println("DONE");
	}
	
	private static void waitAction(int index)
	{
		System.out.println("Wait starting " + index);
		WaitUtils.wait(Duration.ofSeconds(3), new String());
		System.out.println("Wait complete " + index);
	}
}
