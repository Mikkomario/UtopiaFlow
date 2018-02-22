package utopia.flow.util;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A promise is a container for a single element that will get filled at some point
 * @author Mikko Hilpinen
 * @param <T> The type of item provided in this promise
 * @since 22.2.2018
 */
public class Promise<T>
{
	// ATTRIBUTES	-------------------
	
	private Option<T> item = Option.none();
	
	
	// CONSTRUCTOR	-------------------
	
	/**
	 * Creates a new promise waiting to be filled
	 */
	public Promise() { }

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
		
		new Thread(r).start();
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
	
	
	// ACCESSORS	-------------------
	
	/**
	 * @return The item in its current state. May or may not exist yet.
	 */
	public Option<T> getCurrentItem()
	{
		return this.item;
	}
	
	
	// OTHER METHODS	--------------
	
	/**
	 * @return Whether the promise is still waiting for fulfillment
	 */
	public boolean isEmpty()
	{
		return this.item.isEmpty();
	}
	
	/**
	 * @return Whether the promise is fulfilled and an item is available
	 */
	public boolean isFulfilled()
	{
		return this.item.isDefined();
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
		
		return this.item.get();
	}
	
	/**
	 * If the promise is still unfulfilled, waits until it is available. The wait is limited to a certain timeout, 
	 * however.
	 * @param timeOutMillis How many millisecods the promise can be waited for at maximum
	 * @return The fulfilled promise results or None if the timeout was reached
	 */
	public synchronized Option<T> waitFor(int timeOutMillis)
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
	 * Fulfills this promise by providing a concrete item
	 * @param result The item that is provided in this promise
	 */
	public synchronized void fulfill(T result)
	{
		this.item = Option.some(result);
		notifyAll();
	}
	
	/**
	 * Performs an asynchronous operation on this promise one it has been completed
	 * @param f The function that is performed asynchronously
	 */
	public synchronized void doAsync(Consumer<? super T> f)
	{
		new Thread(() -> f.accept(waitFor())).start();
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
}
