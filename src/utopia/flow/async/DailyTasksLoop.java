package utopia.flow.async;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import utopia.flow.structure.ImmutableList;
import utopia.flow.util.WaitTarget;
import utopia.flow.util.WaitUtils;

/**
 * This class takes care of a number of tasks that need to be repeated daily
 * @author Mikko Hilpinen
 * @since 20.12.2018
 */
public class DailyTasksLoop extends Loop
{
	// ATTRIBUTES	-----------------------
	
	private Volatile<ImmutableList<ScheduledTask>> tasks = new Volatile<>(ImmutableList.empty());
	
	
	// IMPLEMENTED	-----------------------
	
	@Override
	protected boolean runOnce()
	{
		// After wait, checks if any tasks can be performed
		while (!isBroken() && tasks.get().headOption().exists(t -> t.isPastScheduledTime()))
		{
			// while tasks can be performed, runs them
			// Pops the first task from the list
			ScheduledTask next = tasks.pop(l -> l.head(), l -> l.tail());
			next.run();
			
			// Adds the task again for tomorrow
			tasks.update(l -> l.plus(next.forTomorrow()));
		}
		
		return true;
	}

	@Override
	protected WaitTarget getNextWaitTarget()
	{
		// Finds the next task to run and waits for it (the tasks list may change while waiting)
		return tasks.get().headOption().handleMap(task -> WaitTarget.withDuration(task.getDurationUntilNextRun()), 
				() -> WaitTarget.untilNotified());
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
		
		public Duration getDurationUntilNextRun()
		{
			return Duration.between(LocalDateTime.now(), time);
		}
		
		public boolean isPastScheduledTime()
		{
			return !LocalDateTime.now().isBefore(time);
		}
		
		public ScheduledTask forTomorrow()
		{
			return new ScheduledTask(task, time.plusDays(1));
		}
	}
}
