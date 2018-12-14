package utopia.flow.test;

import java.time.Duration;

import utopia.flow.async.BackgroundProcessUtils;
import utopia.flow.util.WaitUtils;

/**
 * Tests backgroundOperationUtils and waitUtils
 * @author Mikko Hilpinen
 * @since 26.4.2018
 */
public class BackgroundOperationTest
{
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		System.out.println("Starting");
		
		Container<Boolean> running = new Container<>(true);
		
		// Prints forever
		BackgroundProcessUtils.repeatForever(BackgroundOperationTest::printRepeat, Duration.ofSeconds(1));
		
		// Stops after 10 seconds
		BackgroundProcessUtils.performAfter(() -> running.set(false), Duration.ofSeconds(10));
		
		// Prints line every 1 second
		BackgroundProcessUtils.repeat(() -> System.out.println("Second passed"), Duration.ofSeconds(1), running::get);
		
		System.out.println("Waits 15 seconds before stopping");
		WaitUtils.wait(Duration.ofSeconds(15), running);
		
		System.out.println("Stopped");
	}
	
	private static void printRepeat()
	{
		System.out.println(".");
	}
	
	private static class Container<T>
	{
		private T value;
		
		public Container(T value)
		{
			this.value = value;
		}
		
		public T get()
		{
			return this.value;
		}
		
		public void set(T value)
		{
			this.value = value;
		}
	}
}
