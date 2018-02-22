package utopia.flow.util;

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
}
