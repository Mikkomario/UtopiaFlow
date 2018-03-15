package utopia.flow.test;

import utopia.flow.util.Promise;

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
					lock.wait(1000);
				}
				catch (InterruptedException e)
				{
					System.err.println("Interrupted!");
				}
				return "Done!";
			}
		});
		
		System.out.println(promise.map(false, s -> "The test is " + s).waitFor());
	}
}