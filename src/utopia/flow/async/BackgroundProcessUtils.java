package utopia.flow.async;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.function.Supplier;

import utopia.flow.structure.Option;
import utopia.flow.util.WaitUtils;

/**
 * This is a static collection of methods used for handling background operations
 * @author Mikko Hilpinen
 * @since 25.4.2018
 */
public class BackgroundProcessUtils
{
	// ATTRIBUTES	-----------------------
	
	private static ThreadPool repeatPool = new ThreadPool("Background-Repeating", 20, 500, Duration.ofSeconds(30), e -> 
	{
		System.err.println("Error in repeating background process");
		e.printStackTrace();
	});
	private static ThreadPool pool = new ThreadPool("Background", 20, 500, Duration.ofSeconds(30), e -> 
	{
		System.err.println("Error in background process");
		e.printStackTrace();
	});
	
	
	// CONSTRUCTOR	-----------------------
	
	private BackgroundProcessUtils() { }

	
	// OTHER METHODS	-------------------
	
	/**
	 * Starts printing thread pool status in the background
	 * @param printInterval The interval between prints
	 */
	public static void startThreadDebugPrints(Duration printInterval)
	{
		repeatForever(() -> 
		{
			repeatPool.printDebugStatus();
			pool.printDebugStatus();
			Promise.getThreadPool().printDebugStatus();
			
		}, printInterval);
	}
	
	/**
	 * Repeats a certain process in the background as long as the jvm is active
	 * @param r The operation that is repeated in background
	 * @param intervalMillis The amount of milliseconds between each repeat
	 * @deprecated Please move to using duration instead of milliseconds value
	 */
	public static void repeatForever(Runnable r, long intervalMillis)
	{
		repeatForever(r, Duration.ofMillis(intervalMillis));
	}
	
	/**
	 * Repeats a certain process in the background as long as the jvm is active
	 * @param r The operation that is repeated in background
	 * @param interval The interval between each repeat
	 */
	public static void repeatForever(Runnable r, Duration interval)
	{
		repeat(r, interval, Option.none());
	}
	
	/**
	 * Repeats a certain process in the background as long as the jvm is active
	 * @param r The operation that is repeated in background
	 * @param interval The interval between each repeat
	 * @param delay The delay before the repeat is started
	 */
	public static void repeatForever(Runnable r, Duration interval, Duration delay)
	{
		startAfter(Loop.forever(r, interval), delay);
	}
	
	/**
	 * Repeats a certain operation as long as the user desires
	 * @param r The repeated operation
	 * @param intervalMillis The amount of milliseconds between each run
	 * @param checkContinue A function used for checking whether another run should be made
	 * @deprecated Please move to using duration instead of milliseconds value
	 */
	public static void repeat(Runnable r, long intervalMillis, Supplier<Boolean> checkContinue)
	{
		repeat(r, Duration.ofMillis(intervalMillis), checkContinue);
	}
	
	/**
	 * Repeats a certain operation as long as the user desires
	 * @param r The repeated operation
	 * @param interval The interval between each repeat
	 * @param checkContinue A function used for checking whether another run should be made
	 */
	public static void repeat(Runnable r, Duration interval, Supplier<Boolean> checkContinue)
	{
		repeat(r, interval, Option.some(checkContinue));
	}
	
	/**
	 * Repeats a certain operation as long as the user desires
	 * @param r The repeated operation
	 * @param interval The interval between each repeat
	 * @param delay The delay before the first run is made
	 * @param checkContinue A function used for checking whether another run should be made
	 */
	public static void repeat(Runnable r, Duration interval, Duration delay, Supplier<Boolean> checkContinue)
	{
		startAfter(new Loop(r, interval, Option.some(checkContinue)), delay);
	}
	
	/**
	 * Repeats a certain operation every day at a certain hour and minute (in local time)
	 * @param r The repeated operation
	 * @param hours The scheduled hour
	 * @param minutes The scheduled minute
	 */
	public static void repeatDaily(Runnable r, int hours, int minutes)
	{
		LocalDateTime targetTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(hours, minutes));
		runInBackground(new ScheduledRunnable(r, targetTime));
	}
	
	/**
	 * Performs a certain operation after a certain period of time
	 * @param r The operation that will be run
	 * @param durationMillis The amount of milliseconds before the start of the operation
	 */
	public static void performAfter(Runnable r, long durationMillis)
	{
		runInBackground(new DelayedRunnable(r, durationMillis));
	}
	
	private static void startAfter(Loop loop, Duration delay)
	{
		// Registers the loop to shutdown when JVM closes
		LoopCloseHook.getInstance().register(loop);
		
		repeatPool.execute(new DelayedRunnable(loop, delay.toMillis()));
	}
	
	/**
	 * Performs a certain operation after a certain period of time
	 * @param r The operation that will be run
	 * @param duration The duration before the start of the operation
	 */
	public static void performAfter(Runnable r, Duration duration)
	{
		performAfter(r, duration.toMillis());
	}
	
	/**
	 * Runs a process on a background thread
	 * @param r A process that will be run
	 */
	public static void runInBackground(Runnable r)
	{
		pool.execute(r);
	}
	
	/**
	 * Starts a loop in the background
	 * @param loop A loop that will be run in the background. The loop will be terminated once JVM is closing.
	 */
	public static void start(Loop loop)
	{
		// Registers the loop to end on JVM shutdown
		LoopCloseHook.getInstance().register(loop);
		
		// Starts the loop
		repeatPool.execute(loop);
	}
	
	private static void repeat(Runnable r, Duration interval, Option<Supplier<Boolean>> checkContinue)
	{
		// Creates the loop and starts it
		start(new Loop(r, interval, checkContinue));
	}
	
	
	// NESTED CLASSES	-------------------
	
	private static class DelayedRunnable implements Runnable
	{
		// ATTRIBUTES	-----------------
		
		private Runnable operation;
		private long intervalMillis;
		
		
		// CONSTRUCTOR	-----------------
		
		public DelayedRunnable(Runnable operation, long intervalMillis)
		{
			this.operation = operation;
			this.intervalMillis = intervalMillis;
		}


		// IMPLEMENTED METHODS	---------
		
		@Override
		public void run()
		{
			WaitUtils.wait(this.intervalMillis, this);
			this.operation.run();
		}
	}
	
	// TODO: Create a new interface for scheduled tasks
	private static class ScheduledRunnable implements Runnable
	{
		// ATTRIBUTES	--------------------
		
		private LocalDateTime nextRunTime;
		private Runnable operation;
		
		
		// CONSTRUCTOR	-------------------
		
		public ScheduledRunnable(Runnable operation, LocalDateTime nextRunTime)
		{
			this.operation = operation;
			this.nextRunTime = nextRunTime;
			
			while (this.nextRunTime.isBefore(LocalDateTime.now()))
			{
				this.nextRunTime = this.nextRunTime.plusDays(1);
			}
		}
		
		@Override
		public void run()
		{
			while (true)
			{
				WaitUtils.waitUntil(this.nextRunTime, this);
				this.operation.run();
				
				do
				{
					this.nextRunTime = this.nextRunTime.plusDays(1);
				}
				while (this.nextRunTime.isBefore(LocalDateTime.now()));
			}
		}
	}
}
