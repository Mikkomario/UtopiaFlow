package utopia.flow.util;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A try is used for wrapping an exception into a returnable format without breaking the control flow
 * @author Mikko Hilpinen
 * @param <T> The type of success wrapped in the try
 * @since 5 Dec 2017
 */
public class Try<T>
{
	// ATTRIBUTES	----------------------
	
	private Option<T> success;
	private Option<Exception> exception;
	
	
	// CONSTRUCTOR	----------------------
	
	private Try(Option<T> success, Option<Exception> exception)
	{
		this.success = success;
		this.exception = exception;
	}
	
	/**
	 * Creates a new try by "trying" a throwing function. If the function throws, the resulting try is a failure, 
	 * otherwise the resulting try will contain the function result
	 * @param f A function that produces the tried result
	 */
	public Try(ThrowingSupplier<? extends T, ?> f)
	{
		try
		{
			this.success = Option.some(f.throwingGet());
			this.exception = Option.none();
		}
		catch (Exception e)
		{
			this.exception = Option.some(e);
			this.success = Option.none();
		}
	}

	/**
	 * Creates a new success result
	 * @param success The value wrapped in try
	 * @return A wrapped value
	 */
	public static <T> Try<T> success(T success)
	{
		return new Try<>(Option.some(success), Option.none());
	}
	
	/**
	 * Creates a new failure result
	 * @param e The exception wrapped in try
	 * @return The wrapped exception
	 */
	public static <T> Try<T> failure(Exception e)
	{
		return new Try<>(Option.none(), Option.some(e));
	}
	
	/**
	 * Runs a value generating function and caches any exceptions
	 * @param f The function that is ran
	 * @return The results of the function wrapped in a Try
	 */
	public static <T> Try<T> run(ThrowingSupplier<? extends T, ?> f)
	{
		return new Try<>(f);
	}
	
	/**
	 * Runs a void function and caches any excpetions
	 * @param r A runnable
	 * @return A try that contains the error on failure
	 */
	public static Try<Unit> run(ThrowingRunnable<?> r)
	{
		return new Try<>(() -> 
		{
			r.run();
			return Unit.getInstance();
		});
	}
	
	/**
	 * Runs a function that returns a try result but which may also throw. Flattens the result to a single level deep try.
	 * @param f A function which returns a try
	 * @return The function result or a failure
	 */
	public static <T> Try<T> runAndFlatten(ThrowingSupplier<? extends Try<T>, ?> f)
	{
		return flatten(f.get());
	}
	
	/**
	 * Flattens a two level deep try element into a single level deep element
	 * @param t A two level deep try element
	 * @return A single level deep try element
	 */
	public static <T> Try<T> flatten(Try<? extends Try<T>> t)
	{
		if (t.isFailure())
			return failure(t.getFailure().get());
		else
			return t.getSuccess().get();
	}
	
	
	// IMPLEMENTED METHODS	-------------
	
	@Override
	public String toString()
	{
		if (isSuccess())
			return "Success(" + getSuccess().get() + ")";
		else
			return "Failure(" + getFailure().get().getMessage() + ")";
	}
	
	
	// ACCESSORS	---------------------
	
	/**
	 * @return the success value, if the try is a success
	 */
	public Option<T> getSuccess()
	{
		return this.success;
	}
	
	/**
	 * @return The failure value, if the try is a failure
	 */
	public Option<Exception> getFailure()
	{
		return this.exception;
	}
	
	
	// OTHER METHODS	-----------------
	
	/**
	 * Unwraps the try, throwing the exception on failure
	 * @return The success value on success
	 * @throws TryFailedException Throws on failure
	 */
	public T unwrap() throws TryFailedException
	{
		throwIfFailure();
		return getSuccess().get();
	}
	
	/**
	 * Unwraps the try, throwing the exception on failure. The exception is wrapped into another exception
	 * @param e A function that wraps the exception to a specific type
	 * @return The item inside this try on success
	 * @throws E An exception on failure
	 */
	public <E extends Exception> T unwrapThrowing(Function<Exception, E> e) throws E
	{
		if (isFailure())
			throw e.apply(getFailure().get());
		else
			return getSuccess().get();
	}
	
	/**
	 * @return Whether this try is a success
	 */
	public boolean isSuccess()
	{
		return this.success.isDefined();
	}
	
	/**
	 * @return Whether this try is a failure
	 */
	public boolean isFailure()
	{
		return !isSuccess();
	}
	
	/**
	 * Throws an exception if this try is a failure
	 * @throws TryFailedException Throws on failure
	 */
	public void throwIfFailure() throws TryFailedException
	{
		if (isFailure())
			throw new TryFailedException(getFailure().get());
	}
	
	/**
	 * Gets the value from this try or uses the default value, which may also fail
	 * @param defaultValue The default value that may fail
	 * @return This try if success or the default value otherwise
	 */
	public Try<T> orElse(Try<T> defaultValue)
	{
		if (isSuccess())
			return this;
		else
			return defaultValue;
	}
	
	/**
	 * Gets the value from this try or uses the default value, which may also fail
	 * @param defaultValue The default value that may fail
	 * @return This try if success or the default value otherwise
	 */
	public Try<T> orElse(Supplier<? extends Try<T>> defaultValue)
	{
		if (isSuccess())
			return this;
		else
			return defaultValue.get();
	}
	
	/**
	 * Gets the value from this try or uses the default value, which may also fail
	 * @param defaultValue The default value. None implies a failure.
	 * @return This try if success or the default value otherwise
	 */
	public Try<T> orElse(Option<? extends T> defaultValue)
	{
		if (isSuccess())
			return this;
		else if (defaultValue.isDefined())
			return Try.success(defaultValue.get());
		else
			return this;
	}
	
	/**
	 * Maps the success value of this try
	 * @param f a transformation function
	 * @return The transformed try
	 */
	public <B> Try<B> map(Function<? super T, B> f)
	{
		return new Try<>(getSuccess().map(f), getFailure());
	}
	
	/**
	 * Maps the success value of this try
	 * @param f a transformation function
	 * @return The transformed try
	 */
	public <B> Try<B> flatMap(Function<? super T, ? extends Try<B>> f)
	{
		if (isSuccess())
			return f.apply(getSuccess().get());
		else
			return Try.failure(getFailure().get());
	}
	
	/**
	 * Handles either a success or a failure
	 * @param successHandler The function called on success
	 * @param errorHandler the function called on failure
	 */
	public void handle(Consumer<? super T> successHandler, Consumer<? super Exception> errorHandler)
	{
		if (isSuccess())
			successHandler.accept(getSuccess().get());
		else
			errorHandler.accept(getFailure().get());
	}
	
	/**
	 * Performs an operation on the try's success value or fails
	 * @param successHandler The function called on success
	 * @throws Exception Throws on failure
	 */
	public void forEach(Consumer<? super T> successHandler) throws Exception
	{
		throwIfFailure();
		successHandler.accept(getSuccess().get());
	}
	
	/**
	 * Performs an operation on the try's success value or fails
	 * @param successHandler The function called on success
	 * @throws TryFailedException Throws on failure
	 */
	public <E extends Exception> void forEachThrowing(ThrowingConsumer<? super T, ? extends E> successHandler) throws TryFailedException, E
	{
		throwIfFailure();
		successHandler.accept(getSuccess().get());
	}
	
	
	// NESTED CLASSES	----------------------------
	
	/**
	 * These exceptions are thrown by unhandled failing tries
	 * @author Mikko Hilpinen
	 * @since 18.4.2018
	 */
	public static class TryFailedException extends Exception
	{
		private static final long serialVersionUID = 8213827146452728954L;
		
		/**
		 * Wraps another exception
		 * @param cause The causing exception
		 */
		public TryFailedException(Throwable cause)
		{
			super(cause);
		}
		
		/**
		 * Throws the original causing exception
		 * @throws Throwable The original exception
		 */
		public void throwCause() throws Throwable
		{
			throw getCause();
		}
	}
}
