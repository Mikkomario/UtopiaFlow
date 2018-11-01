package utopia.flow.async;

import java.time.Duration;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import utopia.flow.structure.ImmutableList;
import utopia.flow.structure.Option;
import utopia.flow.util.StringRepresentable;

/**
 * A promise is a container for a single element that will get filled at some point
 * @author Mikko Hilpinen
 * @param <T> The type of item provided in this promise
 * @since 22.2.2018
 */
public class Promise<T> implements StringRepresentable
{
	// ATTRIBUTES	-------------------
	
	// private String name = "Promise - " + Thread.currentThread().getName();
	
	private static ThreadPool pool = new ThreadPool("Promise", 20, 1000, Duration.ofSeconds(30), e -> 
	{
		System.err.println("Error while handling promise");
		e.printStackTrace();
	});
	
	private final Volatile<Option<T>> item = new Volatile<>(Option.none());
	
	
	// CONSTRUCTOR	-------------------
	
	/**
	 * Creates a new promise waiting to be filled
	 */
	public Promise()
	{
		// System.out.println(name + " - created");
	}

	/**
	 * @return A promise that must be fulfilled separately. Shouldn't be waited for in the same thread it is fulfilled
	 */
	public static <T> Promise<T> synchronous()
	{
		return new Promise<>();
	}
	
	/**
	 * Creates a promise that is fulfilled asynchronously
	 * @param getResults The function that generates the promised results
	 * @return The promise that will be fulfilled in a separate thread
	 */
	public static <T> Promise<T> asynchronous(Supplier<T> getResults)
	{
		Promise<T> promise = new Promise<>();
		
		// Generates the promise contents in a separate thread
		Runnable r = new Runnable()
		{
			@Override
			public void run()
			{
				// Generates the result
				T result = getResults.get();
				// Fulfills the promise
				promise.fulfill(result);
			}
		};
		
		pool.execute(r);
		return promise;
	}
	
	/**
	 * Creates a promise that is fulfilled right from the beginning. This function should be used as a wrapper 
	 * when necessary
	 * @param result The result that will be wrapped in a promise
	 * @return The wrapped result
	 */
	public static <T> Promise<T> fulfilled(T result)
	{
		Promise<T> promise = new Promise<>();
		promise.fulfill(result);
		return promise;
	}
	
	/**
	 * Combines a number of promises into a single promise
	 * @param promises The promises that should be combined
	 * @return A promise that contains the results of all of the promises
	 */
	public static <T> Promise<ImmutableList<T>> combine(ImmutableList<? extends Promise<T>> promises)
	{
		if (promises.forAll(p -> p.isFulfilled()))
			return fulfilled(promises.map(p -> p.getCurrentItem().get()));
		else
			return asynchronous(() -> promises.map(p -> p.waitFor()));
	}
	
	
	// STATIC	-----------------------
	
	/**
	 * @return The thread pool used by promises and classes extending promise
	 */
	static ThreadPool getThreadPool()
	{
		return pool;
	}
	
	/**
	 * Changes the thread pool executor that is used
	 * @param newPool The new thread executor pool
	 */
	public static void setThreadPoolExecutor(ThreadPool newPool)
	{
		pool = newPool;
	}
	
	
	// IMPLEMENTED METHODS	-----------
	
	@Override
	public String toString()
	{
		return getCurrentItem().map(r -> "Promised(" + r + ")").getOrElse("Promise");
	}
	
	
	// ACCESSORS	-------------------
	
	/**
	 * @return The item in its current state. May or may not exist yet.
	 */
	public Option<T> getCurrentItem()
	{
		return this.item.get();
	}
	
	
	// OTHER METHODS	--------------
	
	/**
	 * @return Whether the promise is still waiting for fulfillment
	 */
	public boolean isEmpty()
	{
		return this.item.get().isEmpty();
	}
	
	/**
	 * @return Whether the promise is fulfilled and an item is available
	 */
	public boolean isFulfilled()
	{
		return this.item.get().isDefined();
	}
	
	/**
	 * If the promise is still unfulfilled, waits until the item is available
	 * @return The promised item as soon as it is available
	 */
	public synchronized T waitFor()
	{
		// Waits until the value has been provided
		while (isEmpty())
		{
			try
			{
				wait();
			}
			catch (InterruptedException e)
			{
				// Ignored, wait continues
			}
		}
		
		return this.item.get().get();
	}
	
	/**
	 * If the promise is still unfulfilled, waits until it is available. The wait is limited to a certain timeout, 
	 * however.
	 * @param timeOutMillis How many millisecods the promise can be waited for at maximum
	 * @return The fulfilled promise results or None if the timeout was reached
	 */
	public synchronized Option<T> waitFor(long timeOutMillis)
	{
		if (isFulfilled())
			return getCurrentItem();
		else
		{
			long currentMillis = System.currentTimeMillis();
			long maxMillis = currentMillis + timeOutMillis;
			
			while (isEmpty() && currentMillis < maxMillis)
			{
				try
				{
					wait(maxMillis - currentMillis);
				}
				catch (InterruptedException e)
				{
					// Ignored
				}
				currentMillis = System.currentTimeMillis();
			}
			
			return getCurrentItem();
		}
	}
	
	/**
	 * If the promise is still unfulfilled, waits until it is available. The wait is limited to a certain timeout, 
	 * however.
	 * @param timeout The maximum wait time
	 * @return The fulfilled promise results or None if the timeout was reached
	 */
	public synchronized Option<T> waitFor(Duration timeout)
	{
		return waitFor(timeout.toMillis());
	}
	
	/**
	 * Fulfills this promise by providing a concrete item
	 * @param result The item that is provided in this promise
	 */
	public synchronized void fulfill(T result)
	{
		this.item.set(Option.some(result));
		// System.out.println(name + " - fulfilled");
		notifyAll();
	}
	
	/**
	 * Performs an asynchronous operation on this promise one it has been completed
	 * @param f The function that is performed asynchronously
	 */
	public synchronized void doAsync(Consumer<? super T> f)
	{
		pool.execute(() -> f.accept(waitFor()));
	}
	
	/**
	 * Performs an operation on this promise. If the promise is fulfilled, performs the function right away in a 
	 * synchronized manner. If the promise is still empty, performs the function asynchronously once the promise is 
	 * fulfilled
	 * @param f The opertation that is performed for the promise results
	 * @see #doAsync(Consumer)
	 */
	public synchronized void doOnceFulfilled(Consumer<? super T> f)
	{
		if (isFulfilled())
			f.accept(getCurrentItem().get());
		else
			doAsync(f);
	}
	
	/**
	 * Creates a new promise that uses the value of this promise
	 * @param forceAsync Whether the mapping function should always be run asynchronously (use this for blocking functions)
	 * @param f The mapping function for the value of this promise
	 * @return The mapped promise
	 */
	public synchronized <U> Promise<U> map(boolean forceAsync, Function<? super T, ? extends U> f)
	{
		if (isFulfilled() && !forceAsync)
			return Promise.fulfilled(f.apply(getCurrentItem().get()));
		else
		{
			return asynchronous(() -> f.apply(waitFor()));
		}
	}
	
	/**
	 * Creates a new promise that uses the value of this promise
	 * @param f A function that maps the value of this promise into a new promise
	 * @return A one level deep promise for the final value
	 */
	public synchronized <U> Promise<U> flatMap(Function<? super T, ? extends Promise<U>> f)
	{
		if (isFulfilled())
			return f.apply(getCurrentItem().get());
		else
		{
			return asynchronous(() -> f.apply(waitFor()).waitFor());
		}
	}
	
	/**
	 * @return A promise of the completion of this promise
	 */
	public Completion completion()
	{
		if (isFulfilled())
			return Completion.fulfilled();
		else
			return Completion.ofAsynchronous(this::waitFor);
	}
	
	/**
	 * Performs an operation once this promise has completed. Also provides a completion promise.
	 * @param f An operation that will be performed on the completed value before completing
	 * @return A completion of this promise
	 */
	public Completion completionWith(Consumer<? super T> f)
	{
		if (isFulfilled())
		{
			f.accept(getCurrentItem().get());
			return Completion.fulfilled();
		}
		else
			return Completion.ofAsynchronous(() -> f.accept(waitFor()));
	}
	
	/**
	 * Performs a runnable once the promise has been completed. If the promise is already completed, performs the 
	 * runnable synchronously right away
	 * @param r A runnable that will be run once this promise has completed
	 */
	public void onCompletion(Runnable r)
	{
		doOnceFulfilled(i -> r.run());
	}
	
	/**
	 * Creates a new promise based on this promise, but with a failure timeout
	 * @param timeoutDuration The maximum duration for the completion of this promise
	 * @return An attempt for the results of this promise. Will be a failure if timeout is reached before the completion 
	 * of this promise.
	 */
	public Attempt<T> withTimeout(Duration timeoutDuration)
	{
		if (isFulfilled())
			return Attempt.success(getCurrentItem().get());
		else
			return Attempt.tryAsynchronous(() -> waitFor(timeoutDuration).toTry(() -> new TimeoutException()));
	}
	
	
	// NESTED CLASSES	-----------------------
	
	/**
	 * These exceptions may be thrown on promise timeouts
	 * @author Mikko Hilpinen
	 * @since 29.8.2018
	 */
	public static class TimeoutException extends Exception
	{
		private static final long serialVersionUID = -8775087013009213871L;

		/**
		 * Creates a new exception
		 */
		public TimeoutException() { }
		
		/**
		 * Creates a new exception
		 * @param message The exception message
		 */
		public TimeoutException(String message)
		{
			super(message);
		}
	}
}
