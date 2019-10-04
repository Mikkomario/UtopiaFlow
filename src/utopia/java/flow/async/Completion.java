package utopia.flow.async;

import utopia.java.flow.structure.ImmutableList;
import utopia.java.flow.structure.RichIterable;
import utopia.java.flow.util.Unit;

/**
 * These promises are used for marking a completion of an operation. They are not necessarily used for providing a 
 * value like a normal promise
 * @author Mikko Hilpinen
 * @since 18.7.2018
 */
public class Completion extends Promise<Unit>
{
	// ATTRIBUTES	---------------------
	
	/**
	 * A fulfilled completion
	 */
	public static final Completion FULFILLED = fulfilled();
	
	
	// CONSTRUCTOR	---------------------
	
	/**
	 * Creates a completion for an asynchronous operation
	 * @param operation The operation that will be completed asynchronously
	 * @return A promise for the completion of the operation
	 */
	public static Completion ofAsynchronous(Runnable operation)
	{
		Completion completion = new Completion();
		getThreadPool().execute(() -> 
		{
			operation.run();
			completion.fulfill();
		}); 
		return completion;
	}
	
	/**
	 * @return A completion that is already fulfilled
	 */
	public static Completion fulfilled()
	{
		Completion c = new Completion();
		c.fulfill();
		return c;
	}
	
	/**
	 * Creates a single completion for a number of promises
	 * @param promises The promises that all must be fulfilled before completion
	 * @return A completion for all of the promises
	 */
	public static Completion ofMany(RichIterable<? extends Promise<?>> promises)
	{
		if (promises.forAll(p -> p.isFulfilled()))
			return Completion.fulfilled();
		else
			return Completion.ofAsynchronous(() -> promises.forEach(p -> p.waitFor()));
	}
	
	/**
	 * @param first The first promise
	 * @param second The second promise
	 * @param more More promises
	 * @return A completion of all the promises
	 */
	public static Completion ofMany(Promise<?> first, Promise<?> second, Promise<?>... more)
	{
		ImmutableList<Promise<?>> promises = ImmutableList.withValues(first, second).plus(more);
		return ofMany(promises);
	}
	
	
	// IMPLEMENTED	--------------------
	
	@Override
	public Completion completion()
	{
		return this;
	}
	
	@Override
	public String toString()
	{
		if (isFulfilled())
			return "Completed";
		else
			return "Completion";
	}
	
	
	// OTHER	------------------------
	
	/**
	 * Fulfills this promise, marking the operation completed
	 */
	public void fulfill()
	{
		fulfill(Unit.getInstance());
	}
	
	/**
	 * Continues this operation with another operation and returns a completion of the second operation
	 * @param forceAsync Whether the second operation should always be run asynchronously
	 * @param r The second operation
	 * @return A completion for the second operation
	 */
	public Completion continuedWith(boolean forceAsync, Runnable r)
	{
		if (isFulfilled() && !forceAsync)
		{
			r.run();
			return Completion.fulfilled();
		}
		else
			return ofAsynchronous(() -> 
			{
				waitFor();
				r.run();
			});
	}
	
	/**
	 * Continues this operation with another operation and returns a completion of the second operation. The operation 
	 * will always be run asynchronously
	 * @param r The second operation
	 * @return A completion for the second operation
	 */
	public Completion continueAsync(Runnable r)
	{
		return continuedWith(true, r);
	}
}
