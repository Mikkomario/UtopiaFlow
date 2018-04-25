package utopia.flow.util;

/**
 * This is a static collection of methods used for handling background operations
 * @author Mikko Hilpinen
 * @since 25.4.2018
 */
public class BackgroundProcessUtils
{
	// CONSTRUCTOR	-----------------------
	
	private BackgroundProcessUtils() { }

	
	// OTHER METHODS	-------------------
	
	/**
	 * Repeats a certain process in the background as long as the jvm is active
	 * @param r The operation that is repeated in background
	 * @param intervalMillis The amount of milliseconds between each repeat
	 */
	public static void repeatForever(Runnable r, int intervalMillis)
	{
		Thread t = new Thread(new RepeatingRunnable(r, intervalMillis));
		t.setDaemon(true);
		t.start();
	}
	
	
	// NESTED CLASSES	-------------------
	
	private static class RepeatingRunnable implements Runnable
	{
		// ATTRIBUTES	-------------------
		
		private Runnable operation;
		private int intervalMillis;
		
		
		// CONSTRUCTOR	-------------------
		
		public RepeatingRunnable(Runnable operation, int intervalMillis)
		{
			this.operation = operation;
			this.intervalMillis = intervalMillis;
		}
		
		
		// IMPLEMENTED METHODS	----------
		
		@Override
		public synchronized void run()
		{
			while (true)
			{
				this.operation.run();
				WaitUtils.wait(this.intervalMillis, this);
			}
		}
	}
}
