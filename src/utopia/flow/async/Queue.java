package utopia.flow.async;

import java.util.function.Supplier;

import utopia.flow.structure.ImmutableList;

/**
 * This queue handles attempts in an order, delaying some attempts when necessary
 * @author Mikko Hilpinen
 * @since 2.7.2018
 */
public class Queue
{
	// ATTRIBUTES	---------------------
	
	private int maxWidth;
	private int currentWidth = 0;
	private volatile ImmutableList<Delayed<?>> queue = ImmutableList.empty();
	
	
	// CONSTRUCTOR	---------------------
	
	/**
	 * Creates a new queue
	 * @param maxWidth The maximum width of the queue (= how many request can be active at once)
	 */
	public Queue(int maxWidth)
	{
		this.maxWidth = maxWidth;
	}

	
	// OTHER	-------------------------
	
	/**
	 * Pushes a new attempt to this queue. If the queue is empty / has room, performs the attempt immediately, otherwise 
	 * delays the start of the attempt.
	 * @param makeRequest A function for providing / starting an attempt
	 * @return An attempt that provides the final value for the operation
	 */
	public synchronized <T> Attempt<T> pushAttempt(Supplier<? extends Attempt<T>> makeRequest)
	{
		return push(makeRequest, Attempt::new);
	}
	
	/**
	 * Pushes a new promise to this queue. If the queue is empty / has room, performs the attempt immediately, otherwise 
	 * delays the start of the promise.
	 * @param makeRequest A function for providing / starting a promise
	 * @return A promise that provides the final value for the operation
	 */
	public synchronized <T> Promise<T> pushPromise(Supplier<? extends Promise<T>> makeRequest)
	{
		return push(makeRequest, Promise::new);
	}
	
	private synchronized <T, P extends Promise<T>> P push(Supplier<? extends P> makeRequest, 
			Supplier<? extends P> makeEmpty)
	{
		// If there is room, just runs the attempt at once
		if (this.currentWidth < this.maxWidth)
		{
			this.currentWidth ++;
			P request = makeRequest.get();
			request.onCompletion(this::release);
			
			return request;
		}
		// Otherwise delays the attempt until there is enough room
		else
		{
			P empty = makeEmpty.get();
			Delayed<T> delayed = new Delayed<>(makeRequest, empty);
			this.queue = this.queue.plus(delayed);
			
			return empty;
		}
	}
	
	private synchronized void take()
	{
		Delayed<?> next = this.queue.head();
		this.queue = this.queue.tail();
		
		next.start();
		next.toPromise().onCompletion(this::release);
		this.currentWidth ++;
	}
	
	private synchronized void release()
	{
		this.currentWidth --;
		if (!this.queue.isEmpty())
			take();
	}
	
	
	// NESTED CLASSES	------------------
	
	private static class Delayed<T>
	{
		// ATTRIBUTES	------------------
		
		private Promise<T> promise;
		private Supplier<? extends Promise<? extends T>> makeRequest;
		
		
		// CONSTRUCTOR	-----------------
		
		public Delayed(Supplier<? extends Promise<? extends T>> makeRequest, Promise<T> empty)
		{
			this.promise = empty;
			this.makeRequest = makeRequest;
		}
		
		
		// ACCESSORS	-----------------
		
		public Promise<T> toPromise()
		{
			return this.promise;
		}
		
		
		// OTHER	---------------------
		
		public void start()
		{
			this.makeRequest.get().doOnceFulfilled(this.promise::fulfill);
		}
	}
}
