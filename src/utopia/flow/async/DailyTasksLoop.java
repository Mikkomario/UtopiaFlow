package utopia.flow.async;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import utopia.flow.structure.ImmutableList;
import utopia.flow.structure.WeakList;
import utopia.flow.util.WaitUtils;

/**
 * This class takes care of a number of tasks that need to be repeated daily
 * @author Mikko Hilpinen
 * @since 20.12.2018
 */
public class DailyTasksLoop implements Runnable, Breakable
{
	// ATTRIBUTES	-----------------------
	
	private Volatile<ImmutableList<ScheduledTask>> tasks = new Volatile<>(ImmutableList.empty());
	
	private VolatileFlag runningFlag = new VolatileFlag();
	private VolatileFlag breakFlag = new VolatileFlag();
	private WeakList<Completion> breakCompletions = WeakList.empty();
	
	
	// IMPLEMENTED	-----------------------
	
	@Override
	public Completion stop()
	{
		// Non-running loops simply return an empty completion
		if (runningFlag.isSet())
		{
			// Creates the completion first
			Completion completion = new Completion();
			breakCompletions = breakCompletions.plus(completion);
			
			// Tries to stop the run and returns the completion
			breakFlag.set();
			WaitUtils.notify(this);
			
			return completion;
		}
		else
			return Completion.fulfilled();
	}

	@Override
	public void run()
	{
		breakFlag.reset();
		runningFlag.set();
		
		// Repeats the operation as long as not broken
		while (!breakFlag.isSet())
		{
			// Finds the next task to run and waits for it (the tasks list may change while waiting)
			tasks.get().headOption().handle(nextTask -> WaitUtils.waitUntil(nextTask.time, this), 
					() -> WaitUtils.waitUntilNotified(this));
			
			// After wait, checks if any tasks can be performed
			while (!breakFlag.isSet() && tasks.get().headOption().exists(t -> t.isPastScheduledTime(LocalDateTime.now())))
			{
				// while tasks can be performed, runs them
				// Pops the first task from the list
				ScheduledTask next = tasks.pop(l -> l.head(), l -> l.tail());
				next.run();
				
				// Adds the task again for tomorrow
				tasks.update(l -> l.plus(next.forTomorrow()));
			}
		}
		
		runningFlag.reset();
		breakCompletions.forEach(c -> c.fulfill());
	}
	
	
	// OTHER	----------------------------
	
	/**
	 * Schedules a new task to be repeated daily on this loop
	 * @param operation The operation that will be performed
	 * @param time The time when the operation will be run
	 */
	public void schedule(Runnable operation, LocalTime time)
	{
		// Finds the next time the task should be run. Either today or tomorrow
		LocalDateTime timeToday = LocalDateTime.of(LocalDate.now(), time);
		
		LocalDateTime finalTime;
		if (timeToday.isBefore(LocalDateTime.now()))
			finalTime = timeToday.plusDays(1);
		else
			finalTime = timeToday;
		
		// Adds the task to tasks list, resets the wait
		tasks.update(l -> l.plus(new ScheduledTask(operation, finalTime)).sorted());
		WaitUtils.notify(this);
	}

	
	// NESTED CLASSES	--------------------
	
	private static class ScheduledTask implements Runnable, Comparable<ScheduledTask>
	{
		// ATTRIBUTES	--------------------
		
		private Runnable task;
		private LocalDateTime time;
		
		
		// CONSTRUCTOR	--------------------
		
		public ScheduledTask(Runnable task, LocalDateTime time)
		{
			this.task = task;
			this.time = time;
		}
		
		
		// IMPLEMENTED	--------------------
		
		@Override
		public void run()
		{
			task.run();
		}

		@Override
		public int compareTo(ScheduledTask o)
		{
			return time.compareTo(o.time);
		}
		
		
		// OTHER	------------------------
		
		public boolean isPastScheduledTime(LocalDateTime currentTime)
		{
			return !currentTime.isBefore(time);
		}
		
		public ScheduledTask forTomorrow()
		{
			return new ScheduledTask(task, time.plusDays(1));
		}
	}
}
