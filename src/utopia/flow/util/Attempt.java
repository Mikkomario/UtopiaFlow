package utopia.flow.util;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * An attempt is a promise that can fail
 * @author Mikko Hilpinen
 * @since 10.4.2018
 * @param <T> The type for this attempt
 */
public class Attempt<T> extends Promise<Try<T>>
{
	// CONSTRUCTOR	----------------------
	
	/**
	 * Makes an asynchronous attempt
	 * @param f A function that produces either a success or failure result
	 * @return An asynchronous attempt
	 */
	public static <T> Attempt<T> tryAsynchronous(Supplier<? extends Try<T>> f)
	{
		// TODO: WET WET
		Attempt<T> attempt = new Attempt<>();
		
		// Generates the promise contents in a separate thread
		Runnable r = new Runnable()
		{
			@Override
			public void run()
			{
				// Generates the result
				Try<T> result = f.get();
				// Fulfills the promise
				attempt.fulfill(result);
			}
		};
		
		new Thread(r).start();
		return attempt;
	}
	
	/**
	 * Creates a completed attempt
	 * @param result The attempt results
	 * @return A fulfilled attempt
	 */
	public static <T> Attempt<T> fulfilled(Try<T> result)
	{
		Attempt<T> attempt = new Attempt<>();
		attempt.fulfill(result);
		return attempt;
	}
	
	/**
	 * Creates a failed attempt
	 * @param cause The cause of failure
	 * @return A fulfilled attempt
	 */
	public static <T> Attempt<T> failed(Exception cause)
	{
		return fulfilled(Try.failure(cause));
	}
	
	/**
	 * Creates a successful attempt
	 * @param item The success results
	 * @return A fulfilled attempt
	 */
	public static <T> Attempt<T> success(T item)
	{
		return fulfilled(Try.success(item));
	}
	
	
	// OTHER METHODS	------------------
	
	/**
	 * Completes this attempt with a success result
	 * @param result A success result
	 */
	public void succeed(T result)
	{
		fulfill(Try.success(result));
	}
	
	/**
	 * Completes this attempt with a failure result
	 * @param e The cause of failure
	 */
	public void fail(Exception e)
	{
		fulfill(Try.failure(e));
	}
	
	/**
	 * @return A promise for the successful result of this attempt
	 */
	public Promise<Option<T>> getSuccess()
	{
		return map(false, result -> result.getSuccess());
	}
	
	/**
	 * @return A promise for the failure result of this attempt
	 */
	public Promise<Option<Exception>> getFailure()
	{
		return map(false, result -> result.getFailure());
	}
	
	/**
	 * @return Whether this attempt is (already) a failure. False if attempt is not yet complete
	 */
	public boolean isSuccess()
	{
		return getCurrentItem().exists(t -> t.isSuccess());
	}
	
	/**
	 * @return Whether this attempt is (already) a failure. False if attempt is not yet complete
	 */
	public boolean isFailure()
	{
		return getCurrentItem().exists(t -> t.isFailure());
	}
	
	/**
	 * @return The current success value for this attempt. 
	 */
	public Option<T> getCurrentSuccess()
	{
		return getCurrentItem().flatMap(t -> t.getSuccess());
	}
	
	/**
	 * @return The current failure value for this attempt
	 */
	public Option<Exception> getCurrentFailure()
	{
		return getCurrentItem().flatMap(t -> t.getFailure());
	}
	
	/**
	 * Maps this attempt contents
	 * @param forceAsync Whether the contents should always be mapped asynchronously (use this for blocking operations)
	 * @param f a mapping function
	 * @return A mapped attempt
	 */
	public <B> Attempt<B> mapAttempt(boolean forceAsync, Function<? super T, ? extends Try<B>> f)
	{
		if (isFulfilled() && !forceAsync)
			return Attempt.fulfilled(getCurrentItem().get().flatMap(f));
		else
		{
			return tryAsynchronous(() -> waitFor().flatMap(f));
		}
	}
	
	/**
	 * Maps this attempt based on another attempt
	 * @param f A function that creates an attempt based on the contents of this attempt
	 * @return A mapped attempt
	 */
	public <B> Attempt<B> flatMapAttempt(Function<? super T, ? extends Attempt<B>> f)
	{
		if (isFulfilled())
			return flatMapResult(getCurrentItem().get(), f);
		else
			return Attempt.tryAsynchronous(() -> flatMapResult(waitFor(), f).waitFor());
	}
	
	private static <T, B> Attempt<B> flatMapResult(Try<T> result, Function<? super T, ? extends Attempt<B>> f)
	{
		if (result.isSuccess())
			return f.apply(result.getSuccess().get());
		else
			return Attempt.failed(result.getFailure().get());
	}
}
