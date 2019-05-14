package utopia.flow.async;

import java.util.function.Supplier;

import utopia.flow.function.ThrowingRunnable;
import utopia.flow.function.ThrowingSupplier;
import utopia.flow.structure.Pair;
import utopia.flow.structure.Try;
import utopia.flow.util.Unit;

/**
 * This queue handles attempts in an order, delaying some attempts when necessary
 * @author Mikko Hilpinen
 * @since 2.7.2018
 */
public class Queue
{
	// ATTRIBUTES	---------------------
	
	private int maxWidth;
	private Volatile<Integer> currentWidth = new Volatile<>(0);
	private VolatileList<Delayed<?>> queue = new VolatileList<>();
	
	
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
	 * Pushes a new synchronous action to this queue. Action will be performed asynchronously.
	 * @param getResult A function for producing a result
	 * @return A promise of the resulting result
	 */
	public <T> Promise<T> push(Supplier<? extends T> getResult)
	{
		return pushPromise(() -> Promise.asynchronous(getResult));
	}
	
	/**
	 * Pushes a new synchronous action to this queue. Action will be performed asynchronously.
	 * @param getResult A function for producing a result. May fail.
	 * @return A promise of the resulting result. May contain a failure.
	 */
	public <T> Attempt<T> pushTry(Supplier<? extends Try<T>> getResult)
	{
		return pushAttempt(() -> Attempt.tryAsynchronous(getResult));
	}
	
	/**
	 * Pushes a new synchronous action to this queue. Action will be performed asynchronously.
	 * @param getResult A function for producing a result. May fail.
	 * @return A promise of the resulting result. May contain a failure.
	 */
	public <T> Attempt<T> pushThrowing(ThrowingSupplier<T, ?> getResult)
	{
		return pushTry(getResult);
	}
	
	/**
	 * Pushes a new synchronous action to this queue. Action will be performed asynchronously.
	 * @param action An action that will be run
	 * @return Completion of the action
	 */
	public Completion push(Runnable action)
	{
		return pushCompletion(() -> Completion.ofAsynchronous(action));
	}
	
	/**
	 * Pushes a new synchronous action to this queue. Action will be performed asynchronously.
	 * @param action An action that will be run. May fail.
	 * @return Completion of the action. May contain a failure.
	 */
	public Attempt<Unit> pushThrowing(ThrowingRunnable<?> action)
	{
		return pushAttempt(() -> Attempt.tryAsynchronous(action::tryRun));
	}
	
	/**
	 * Pushes a new attempt to this queue. If the queue is empty / has room, performs the attempt immediately, otherwise 
	 * delays the start of the attempt.
	 * @param makeRequest A function for providing / starting an attempt
	 * @return An attempt that provides the final value for the operation
	 */
	public <T> Attempt<T> pushAttempt(Supplier<? extends Attempt<T>> makeRequest)
	{
		return push(makeRequest, Attempt::new);
	}
	
	/**
	 * Pushes a new promise to this queue. If the queue is empty / has room, performs the attempt immediately, otherwise 
	 * delays the start of the promise.
	 * @param makeRequest A function for providing / starting a promise
	 * @return A promise that provides the final value for the operation
	 */
	public <T> Promise<T> pushPromise(Supplier<? extends Promise<T>> makeRequest)
	{
		return push(makeRequest, Promise::new);
	}
	
	/**
	 * Pushes a new asynchronous action to this queue. If this queue is empty or has room, 
	 * performs the action immediately, otherwise delays the start of the action.
	 * @param performAction A function for providing and starting the action
	 * @return A completion for when the action is done
	 */
	public Completion pushCompletion(Supplier<? extends Completion> performAction)
	{
		return push(performAction, Completion::new);
	}
	
	private <T, P extends Promise<T>> P push(Supplier<? extends P> makeRequest, 
			Supplier<? extends P> makeEmpty)
	{
		return currentWidth.pop(w -> 
		{
			// If there is room, just runs the attempt at once
			if (w < maxWidth)
			{
				P request = makeRequest.get();
				request.doAsync(u -> release());
				// request.onCompletion(this::release);
				
				return new Pair<>(request, w + 1);
			}
			// Otherwise delays the attempt until there is enough room
			else
			{
				P empty = makeEmpty.get();
				Delayed<T> delayed = new Delayed<>(makeRequest, empty);
				queue.add(delayed);
				
				return new Pair<>(empty, w);
			}
		});
	}
	
	private void release()
	{
		// When released, tries taking the next operation in this queue
		queue.pop().handle(next -> 
		{
			// If there was an operation in the queue, runs it and releases once completed
			next.start();
			next.toPromise().doAsync(u -> release());
			
			// Otherwise, since running stopped, makes space for new operations
		}, () -> currentWidth.update(i -> i - 1));
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
			return promise;
		}
		
		
		// OTHER	---------------------
		
		public void start()
		{
			makeRequest.get().doOnceFulfilled(promise::fulfill);
		}
	}
}
