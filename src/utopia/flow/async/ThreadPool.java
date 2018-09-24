package utopia.flow.async;

import java.time.Duration;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Supplier;

import utopia.flow.structure.ImmutableList;
import utopia.flow.structure.Option;
import utopia.flow.util.Counter;
import utopia.flow.util.WaitUtils;

/**
 * This class handles thread reuse and distribution
 * @author Mikko Hilpinen
 * @since 24.9.2018
 */
public class ThreadPool implements Executor
{
	// ATTRIBUTES	----------------------
	
	private String name;
	private int maxSize;
	private Consumer<? super Exception> errorHandler;
	private Duration maxIdleDuration;
	
	private Counter indexCounter = new Counter(0, 1);
	private Volatile<ImmutableList<WorkerThread>> threads;
	private Volatile<ImmutableList<Runnable>> queue = new Volatile<>(ImmutableList.empty());
	
	
	// CONSTRUCTOR	----------------------
	
	/**
	 * Creates a new thread pool
	 * @param name The name of the pool
	 * @param coreSize The minimum amount of pooled threads
	 * @param maxSize The maximum amount of pooled threads
	 * @param maxIdleDuration The maximum duration for a thread to remain idle before being removed
	 * @param errorHandler A function for handling task errors
	 */
	public ThreadPool(String name, int coreSize, int maxSize, Duration maxIdleDuration, 
			Consumer<? super Exception> errorHandler)
	{
		this.maxSize = maxSize;
		this.name = name;
		this.maxIdleDuration = maxIdleDuration;
		this.errorHandler = errorHandler;
		this.threads = new Volatile<>(ImmutableList.filledWith(coreSize, 
				() -> WorkerThread.core(nextCoreName(), this::nextQueueTask, errorHandler)));
	}
	
	
	// IMPLEMENTED	----------------------
	
	@Override
	public void execute(Runnable command)
	{
		// Updates the thread list (drops ended threads, possibly creates a new thread if all current threads are busy)
		threads.update(old -> 
		{
			ImmutableList<WorkerThread> filtered = old.filter(t -> !t.ended.isSet());
			
			// First checks if any thread accepts the new task
			if (filtered.exists(t -> t.offer(command)))
			{
				return filtered;
			}
			else
			{
				// If all were busy, tries to create a new thread
				if (filtered.size() < maxSize)
				{
					return filtered.plus(WorkerThread.temp(nextThreadName(), maxIdleDuration, command, 
							this::nextQueueTask, errorHandler));
				}
				else
				{
					// If max thread limit reached, pushes the task to queue
					queue.update(q -> q.plus(command));
					return filtered;
				}
			}
		});
	}
	
	
	// OTHER	-------------------------
	
	private String nextCoreName()
	{
		StringBuilder s = new StringBuilder();
		s.append(name);
		s.append("-core-");
		s.append(indexCounter.next());
		
		return s.toString();
	}
	
	private String nextThreadName()
	{
		StringBuilder s = new StringBuilder();
		s.append(name);
		s.append("-");
		s.append(indexCounter.next());
		
		return s.toString();
	}
	
	private Option<Runnable> nextQueueTask()
	{
		return queue.pop(q -> q.headOption(), q -> q.tail());
	}

	
	// NESTED CLASSES	------------------
	
	private static class WorkerThread extends Thread
	{
		// ATTRIBUTES	------------------
		
		private Option<Duration> maxIdleDuration = Option.none();
		private VolatileFlag ended = new VolatileFlag();
		
		private Volatile<Option<Runnable>> nextTask = new Volatile<>(Option.none());
		
		private Supplier<Option<Runnable>> getNext;
		private Consumer<? super Exception> errorHandler;
		
		
		// CONSTRUCTOR	------------------
		
		public WorkerThread(String name, Option<Duration> maxIdleDuration, Supplier<Option<Runnable>> getNext, 
				Consumer<? super Exception> errorHandler)
		{
			setName(name);
			setDaemon(true);
			
			this.maxIdleDuration = maxIdleDuration;
			this.getNext = getNext;
			this.errorHandler = errorHandler;
		}
		
		public static WorkerThread core(String name, Supplier<Option<Runnable>> getNext, 
				Consumer<? super Exception> errorHandler)
		{
			WorkerThread t = new WorkerThread(name, Option.none(), getNext, errorHandler);
			t.start();
			return t;
		}
		
		public static WorkerThread temp(String name, Duration maxIdleDuration, Runnable initialTask, 
				Supplier<Option<Runnable>> getNext, Consumer<? super Exception> errorHandler)
		{
			WorkerThread t = new WorkerThread(name, Option.some(maxIdleDuration), getNext, errorHandler);
			t.nextTask.set(Option.some(initialTask));
			t.start();
			return t;
		}
		
		
		// IMPLEMENTED	------------------
		
		@Override
		public void run()
		{
			try
			{
				while (!ended.get())
				{
					// Waits until a task is received
					// There may exist a maximum idle duration for the threads
					if (maxIdleDuration.isDefined())
					{
						if (nextTask.get().isEmpty())
						{
							WaitUtils.wait(maxIdleDuration.get(), this);
						
							if (nextTask.get().isEmpty())
							{
								ended.set();
								return;
							}
						}
					}
					else
					{
						while (nextTask.get().isEmpty())
						{
							WaitUtils.waitUntilNotified(this);
						}
					}
					
					
					// Performs the task
					try
					{
						nextTask.get().forEach(t -> t.run());
					}
					catch (Exception e)
					{
						errorHandler.accept(e);
					}
					finally
					{
						// Checks for a new task
						nextTask.set(getNext.get());
					}
				}
			}
			finally
			{
				nextTask.set(Option.none());
				
				maxIdleDuration = null;
				getNext = null;
				errorHandler = null;
			}
		}
		
		
		// OTHER	---------------------
		
		public boolean offer(Runnable task)
		{
			if (ended.isSet())
				return false;
			else
			{
				boolean taskAccepted = nextTask.pop(t -> t.isEmpty(), t -> 
				{
					if (t.isDefined())
						return t;
					else
						return Option.some(task);
				});
				
				// If the task was accepted, skips waiting
				if (taskAccepted)
					WaitUtils.notify(this);
				
				return taskAccepted;
			}
		}
	}
}
