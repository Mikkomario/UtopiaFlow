package utopia.flow.test;

import java.time.Duration;

import utopia.flow.async.Promise;
import utopia.flow.async.Queue;
import utopia.flow.util.Unit;
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
		Queue q = new Queue(2);
		
		q.pushPromise(() -> waitPromise(0));
		q.pushPromise(() -> waitPromise(1));
		q.pushPromise(() -> waitPromise(2));
		q.pushPromise(() -> waitPromise(3));
		q.pushPromise(() -> waitPromise(4));
		
		System.out.println("Waiting 15 seconds before program closes");
		WaitUtils.wait(Duration.ofSeconds(15), new String());
		System.out.println("DONE");
	}
	
	private static Promise<Unit> waitPromise(int index)
	{
		return Promise.asynchronous(() -> 
		{
			System.out.println("Wait starting " + index);
			WaitUtils.wait(Duration.ofSeconds(3), new String());
			System.out.println("Wait complete " + index);
			
			return Unit.getInstance();
		});
	}
}
