package utopia.flow.async;

import java.util.function.Supplier;

import utopia.flow.function.ThrowingSupplier;
import utopia.flow.structure.ImmutableList;
import utopia.flow.structure.Option;
import utopia.flow.structure.Try;
import utopia.flow.util.Unit;

/**
 * This queue takes in multiple operations and performs them in order asynchronously. This queue 
 * may handle multiple operations at once, based on its width
 * @author Mikko Hilpinen
 * @since 16.5.2019
 */
public class ActionQueue
{
	// ATTRIBUTES	---------------------
	
	private int maxWidth;
	private VolatileList<Action<?>> queue = new VolatileList<>();
	private VolatileList<Completion> handlerCompletions = new VolatileList<>();
	
	
	// CONSTRUCTOR	---------------------
	
	/**
	 * Creates a new queue with maximum width
	 * @param maxWidth A maximum width for this queue
	 */
	public ActionQueue(int maxWidth)
	{
		this.maxWidth = maxWidth;
	}
	
	
	// OTHER	-------------------------
	
	/**
	 * Pushes a new operation to this queue
	 * @param operation An operation that will be performed asynchronously
	 * @return A promise for the operation result
	 */
	public <T> Promise<T> push(Supplier<? extends T> operation)
	{
		// Pushes the item to the queue
		Action<T> action = new Action<>(operation);
		queue.add(action);
		
		// Starts additional handlers if possible
		handlerCompletions.update(current -> 
		{
			ImmutableList<Completion> incomplete = current.filter(c -> c.isEmpty());
			if (incomplete.size() < maxWidth)
				return incomplete.plus(Completion.ofAsynchronous(new Handler(queue::pop)));
			else
				return incomplete;
		});
		
		return action.getPromise();
	}
	
	/**
	 * Pushes a new runnable to this queue
	 * @param operation An operation that will be performed asynchronously
	 * @return A completion for the operation
	 */
	public Promise<Unit> push(Runnable operation)
	{
		return push(() -> 
		{
			operation.run();
			return Unit.getInstance();
		}); 
	}
	
	/**
	 * Pushes a new operation to this queue
	 * @param operation An operation that may throw
	 * @return A promise for the operation result (wrapped in try)
	 */
	public <T> Promise<Try<T>> pushTry(ThrowingSupplier<T, ?> operation)
	{
		return push(operation);
	}
	
	
	// NESTED CLASSES	-----------------
	
	private static class Handler implements Runnable
	{
		// ATTRIBUTES	-----------------
		
		private Supplier<Option<Action<?>>> getNext;
		
		
		// CONSTRUCTOR	-----------------
		
		public Handler(Supplier<Option<Action<?>>> getNext)
		{
			this.getNext = getNext;
		}
		
		
		// IMPLEMENTED	----------------
		
		@Override
		public void run()
		{
			// Handles items as long as there are some available
			Option<Action<?>> next = getNext.get();
			
			while (next.isDefined())
			{
				next.get().run();
				next = getNext.get();
			}
		}
	}
	
	private static class Action<T> implements Runnable
	{
		// ATTRIBUTES	-----------------
		
		private Promise<T> promise = new Promise<>();
		private Supplier<? extends T> make;
		
		
		// CONSTRUCTOR	----------------
		
		public Action(Supplier<? extends T> make)
		{
			this.make = make;
		}
		
		
		// ACCESSORS	----------------
		
		public Promise<T> getPromise()
		{
			return promise;
		}
		
		
		// IMPLEMENTED	----------------
		
		@Override
		public void run()
		{
			promise.fulfill(make.get());
		}
	}
}
