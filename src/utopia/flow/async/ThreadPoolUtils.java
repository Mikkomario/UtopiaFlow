package utopia.flow.async;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import utopia.flow.util.Counter;

/**
 * This class contains some utility methods for thread pool handling
 * @author Mikko Hilpinen
 * @since 21.9.2018
 */
public class ThreadPoolUtils
{
	// CONSTRUCTOR	--------------------------
	
	private ThreadPoolUtils() { }

	
	// OTHER	------------------------------
	
	private static int numberOfCPUs()
	{
		return Runtime.getRuntime().availableProcessors();
	}
	
	/**
	 * Creates a new thread pool for background tasks
	 * @param name The name of the pool
	 * @param baseSize The basic thread amount per cpu
	 * @param maxSize The maximum thread amount per cpu
	 * @return A new thread pool
	 */
	public static ThreadPoolExecutor makeThreadPool(String name, int baseSize, int maxSize)
	{
		int cpus = numberOfCPUs();
		ThreadPoolExecutor exec = new ThreadPoolExecutor(baseSize * cpus, maxSize * cpus, 60, TimeUnit.SECONDS, 
				new SynchronousQueue<>(), new DaemonThreadFactory(name));
		exec.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		
		return exec;
	}
	
	
	// NESTED CLASSES	----------------------
	
	/**
	 * This is a factory class for daemon threads
	 * @author Mikko Hilpinen
	 * @since 21.9.2018
	 */
	public static class DaemonThreadFactory implements ThreadFactory
	{
		// ATTRIBUTES	----------------------
		
		private final Counter counter = new Counter(1, 1);
		private final String name;
		
		
		// CONSTRUCTOR	----------------------
		
		/**
		 * Creates a new factory
		 * @param name The name prefix for created threads
		 */
		public DaemonThreadFactory(String name)
		{
			this.name = name;
		}
		
		
		// IMPLEMENTED	----------------------
		
		@Override
		public Thread newThread(Runnable r)
		{
			Thread t = new Thread(r);
			
			StringBuilder nameBuilder = new StringBuilder();
			nameBuilder.append(name);
			nameBuilder.append("-");
			nameBuilder.append(counter.next());
			
			t.setName(nameBuilder.toString());
			t.setDaemon(true);
			
			return t;
		}	
	}
}
