package utopia.flow.test;

import java.time.Duration;

import utopia.flow.async.StaticIntervalLoop;
import utopia.flow.util.Counter;
import utopia.flow.util.WaitUtils;

/**
 * This is a simple test for both loops and waiting
 * @author Mikko Hilpinen
 * @since 31.12.2018
 */
public class WaitLoopTest
{
	@SuppressWarnings("javadoc")
	public static void main(String[] args)
	{
		System.out.println("Starts counting seconds. Stops after 15 seconds.");
		
		// Creates the loop first
		Counter counter = new Counter(1, 1);
		StaticIntervalLoop loop = StaticIntervalLoop.untilJVMCloses(() -> 
		{
			int next = counter.next();
			System.out.println(next);
			
		}, Duration.ofSeconds(1)); 
		
		// Starts the loop, waits for 15 seconds, then closes
		loop.startInBackground();
		
		Object lock = new Object();
		WaitUtils.wait(Duration.ofSeconds(15), lock);
		
		System.out.println("Done!");
	}
}
