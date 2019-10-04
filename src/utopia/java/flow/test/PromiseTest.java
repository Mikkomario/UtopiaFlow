package utopia.java.flow.test;

import java.time.Duration;

import utopia.flow.async.Promise;
import utopia.java.flow.structure.ImmutableList;
import utopia.java.flow.util.WaitUtils;

/**
 * This class tests the promise class
 * @author Mikko Hilpinen
 * @since 22.2.2018
 */
public class PromiseTest
{
	/**
	 * Runs The test
	 * @param args not used
	 */
	public static void main(String[] args)
	{
		Object lock = "";
		Promise<String> promise = Promise.asynchronous(() -> 
		{
			synchronized (lock)
			{
				try
				{
					System.out.println("Staring wait");
					lock.wait(1000);
					System.out.println("Finished wait");
				}
				catch (InterruptedException e)
				{
					System.err.println("Interrupted!");
				}
				return "Done!";
			}
		});
		
		Promise<String> map1 = promise.map(false, s -> "The test is " + s);
		map1.doOnceFulfilled(System.out::println);
		
		Promise<String> flatMap1 = promise.flatMap(s -> Promise.asynchronous(() -> 
		{
			WaitUtils.wait(Duration.ofSeconds(2), new String());
			return s + " again";
			
		}));
		flatMap1.doOnceFulfilled(System.out::println);
		
		Promise.combine(ImmutableList.withValues(promise, map1, flatMap1)).waitFor();
		System.out.println("ENDING");
	}
}
