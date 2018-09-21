package utopia.flow.async;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.ThreadPoolExecutor;
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
	
	private static ThreadPoolExecutor repeatPool = ThreadPoolUtils.makeThreadPool("Background-Repeating", 5, 1000);
	private static ThreadPoolExecutor pool = ThreadPoolUtils.makeThreadPool("Background", 10, 100);
	
	
	// CONSTRUCTOR	-----------------------
	
	private BackgroundProcessUtils() { }

	
	// OTHER METHODS	-------------------
	
	/**
	 * Repeats a certain process in the background as long as the jvm is active
	 * @param r The operation that is repeated in background
	 * @param intervalMillis The amount of milliseconds between each repeat
	 */
	public static void repeatForever(Runnable r, long intervalMillis)
	{
		repeat(r, intervalMillis, Option.none());
	}
	
	/**
	 * Repeats a certain process in the background as long as the jvm is active
	 * @param r The operation that is repeated in background
	 * @param interval The interval between each repeat
	 */
	public static void repeatForever(Runnable r, Duration interval)
	{
		repeatForever(r, interval.toMillis());
	}
	
	/**
	 * Repeats a certain process in the background as long as the jvm is active
	 * @param r The operation that is repeated in background
	 * @param interval The interval between each repeat
	 * @param delay The delay before the repeat is started
	 */
	public static void repeatForever(Runnable r, Duration interval, Duration delay)
	{
		repeatAfter(new RepeatingRunnable(r, interval.toMillis(), Option.none()), delay);
	}
	
	/**
	 * Repeats a certain operation as long as the user desires
	 * @param r The repeated operation
	 * @param intervalMillis The amount of milliseconds between each run
	 * @param checkContinue A function used for checking whether another run should be made
	 */
	public static void repeat(Runnable r, long intervalMillis, Supplier<Boolean> checkContinue)
	{
		repeat(r, intervalMillis, Option.some(checkContinue));
	}
	
	/**
	 * Repeats a certain operation as long as the user desires
	 * @param r The repeated operation
	 * @param interval The interval between each repeat
	 * @param checkContinue A function used for checking whether another run should be made
	 */
	public static void repeat(Runnable r, Duration interval, Supplier<Boolean> checkContinue)
	{
		repeat(r, interval.toMillis(), checkContinue);
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
		repeatAfter(new RepeatingRunnable(r, interval.toMillis(), Option.some(checkContinue)), delay);
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
	
	private static void repeatAfter(Runnable r, Duration delay)
	{
		repeatPool.execute(new DelayedRunnable(r, delay.toMillis()));
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
	
	private static void repeat(Runnable r, long intervalMillis, Option<Supplier<Boolean>> checkContinue)
	{
		runInBackground(new RepeatingRunnable(r, intervalMillis, checkContinue));
	}
	
	
	// NESTED CLASSES	-------------------
	
	private static class RepeatingRunnable implements Runnable
	{
		// ATTRIBUTES	-------------------
		
		private Runnable operation;
		private long intervalMillis;
		private Option<Supplier<Boolean>> continueCheck;
		
		
		// CONSTRUCTOR	-------------------
		
		public RepeatingRunnable(Runnable operation, long intervalMillis, Option<Supplier<Boolean>> continueCheck)
		{
			this.operation = operation;
			this.intervalMillis = intervalMillis;
			this.continueCheck = continueCheck;
		}
		
		
		// IMPLEMENTED METHODS	----------
		
		@Override
		public synchronized void run()
		{
			do
			{
				this.operation.run();
				WaitUtils.wait(this.intervalMillis, this);
			}
			while (this.continueCheck.forAll(c -> c.get()));
		}
	}
	
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
			try
			{
				WaitUtils.wait(this.intervalMillis, this);
				this.operation.run();
			}
			finally
			{
				this.operation = null;
			}
		}
	}
	
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
