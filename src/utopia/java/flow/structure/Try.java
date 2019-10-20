package utopia.java.flow.structure;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import utopia.java.flow.function.ThrowingConsumer;
import utopia.java.flow.function.ThrowingFunction;
import utopia.java.flow.function.ThrowingRunnable;
import utopia.java.flow.function.ThrowingSupplier;
import utopia.java.flow.util.AutoCloseWrapper;
import utopia.java.flow.util.StringRepresentable;
import utopia.java.flow.util.Unit;

/**
 * A try is used for wrapping an exception into a returnable format without breaking the control flow
 * @author Mikko Hilpinen
 * @param <T> The type of success wrapped in the try
 * @since 5 Dec 2017
 */
public class Try<T> implements StringRepresentable
{
	// ATTRIBUTES	----------------------
	
	/**
	 * A success with no specific content
	 */
	public static final Try<Unit> SUCCESS = Try.success(Unit.getInstance());
	
	private Option<T> success;
	private Option<Exception> exception;
	private Option<String> message;
	
	
	// CONSTRUCTOR	----------------------
	
	private Try(Option<T> success, Option<Exception> exception, Option<String> message)
	{
		this.success = success;
		this.exception = exception;
		this.message = message;
	}
	
	/**
	 * Creates a new try by "trying" a throwing function. If the function throws, the resulting try is a failure, 
	 * otherwise the resulting try will contain the function result
	 * @param f A function that produces the tried result
	 */
	public Try(ThrowingSupplier<? extends T, ?> f)
	{
		this.message = Option.none();
		
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
		return new Try<>(Option.some(success), Option.none(), Option.none());
	}
	
	/**
	 * Creates a new failure result
	 * @param e The exception wrapped in try
	 * @param message The message sent with the exception
	 * @return The wrapped exception
	 */
	public static <T> Try<T> failure(Exception e, Option<String> message)
	{
		return new Try<>(Option.none(), Option.some(e), message);
	}
	
	/**
	 * Creates a new failure result
	 * @param e The exception wrapped in try
	 * @param message The message sent with the exception
	 * @return The wrapped exception
	 */
	public static <T> Try<T> failure(Exception e, String message)
	{
		return failure(e, Option.some(message));
	}
	
	/**
	 * Creates a new failure result
	 * @param e The exception wrapped in try
	 * @return The wrapped exception
	 */
	public static <T> Try<T> failure(Exception e)
	{
		return failure(e, Option.none());
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
	 * Creates a try-with
	 * @param makeInput A function for making temporary autocloseable input object
	 * @param makeOutput A function for consuming input object and producing output object. Shouldn't 
	 * close input.
	 * @return Second function result or an error if any process failed.
	 */
	public static <I extends AutoCloseable, O> Try<O> with(ThrowingSupplier<? extends I, ?> makeInput, 
			ThrowingFunction<? super I, O, ?> makeOutput)
	{
		try (I input = makeInput.throwingGet())
		{
			return makeOutput.apply(input);
		}
		catch (Exception e)
		{
			return failure(e);
		}
	}
	
	/**
	 * Creates a try-with
	 * @param makeInput A function for making temporary autocloseable input object
	 * @param useInput A function for consuming input object. Shouldn't close input.
	 * @return Unit or an error if any process failed.
	 */
	public static <I extends AutoCloseable> Try<Unit> with(ThrowingSupplier<? extends I, ?> makeInput, 
			ThrowingConsumer<? super I, ?> useInput)
	{
		return with(makeInput, input -> 
		{
			useInput.accept(input);
			return Unit.getInstance();
		});
	}
	
	/**
	 * Creates a try-with
	 * @param makeInput A function for making temporary non-autocloseable input object
	 * @param closeInput A function for closing the input object once operation has been completed
	 * @param makeOutput A function for consuming input object and producing output object. Shouldn't 
	 * close input.
	 * @return Second function result or an error if any process failed.
	 */
	public static <I, O> Try<O> with(ThrowingSupplier<? extends I, ?> makeInput, 
			ThrowingConsumer<? super I, ?> closeInput, 
			ThrowingFunction<? super I, ? extends O, ?> makeOutput)
	{
		return Try.<AutoCloseWrapper<I>, O>with(
				() -> AutoCloseWrapper.wrap(makeInput.throwingGet(), closeInput), 
				inputWrap -> makeOutput.throwingApply(inputWrap.get()));
	}
	
	/**
	 * Creates a try-with
	 * @param makeInput A function for making temporary non-autocloseable input object
	 * @param closeInput A function for closing the input object once operation has been completed
	 * @param useInput A function for consuming input object. Shouldn't close input.
	 * @return Unit or an error if any process failed.
	 */
	public static <I> Try<Unit> with(ThrowingSupplier<? extends I, ?> makeInput, 
			ThrowingConsumer<? super I, ?> closeInput, ThrowingConsumer<? super I, ?> useInput)
	{
		return Try.<AutoCloseWrapper<I>>with(
				() -> AutoCloseWrapper.wrap(makeInput.throwingGet(), closeInput), 
				inputWrap -> useInput.accept(inputWrap.get()));
	}
	
	/**
	 * Flattens a two level deep try element into a single level deep element
	 * @param t A two level deep try element
	 * @return A single level deep try element
	 */
	public static <T> Try<T> flatten(Try<? extends Try<T>> t)
	{
		if (t.isFailure())
			return failure(t.getFailure(), t.message);
		else
			return t.getSuccess();
	}
	
	/**
	 * Converts an either into a try
	 * @param either A either with error or value
	 * @return A try
	 */
	public static <T> Try<T> of(Either<? extends Exception, ? extends T> either)
	{
		return either.toValue(Try::failure, Try::success);
	}
	
	
	// IMPLEMENTED METHODS	-------------
	
	@Override
	public String toString()
	{
		if (isSuccess())
			return "Success(" + getSuccess() + ")";
		else
			return "Failure(" + getMessage() + ")";
	}
	
	
	// ACCESSORS	---------------------
	
	/**
	 * @return the success value, if this try is a success
	 */
	public Option<T> success()
	{
		return success;
	}
	
	/**
	 * @return the success value. Similar to calling success().get()
	 * @throws TryFailedRuntimeException If this try was a failure
	 */
	public T getSuccess() throws TryFailedRuntimeException
	{
		throwRuntimeIfFailure();
		return success.get();
	}
	
	/**
	 * @return The failure value, if this try is a failure
	 */
	public Option<Exception> failure()
	{
		return exception;
	}
	
	/**
	 * @return The failure value
	 * @throws NullPointerException If this try was a success
	 */
	public Exception getFailure() throws NullPointerException
	{
		return exception.get();
	}
	
	
	// OTHER METHODS	-----------------
	
	/**
	 * @return The error message sent with this try. If this try is a success, a 
	 * placeholder message is provided.
	 */
	public String getMessage()
	{
		return message.getOrElse(() -> handleMap(s -> "Success (" + s + ")", e -> 
		{
			if (e.getMessage() == null)
				return "No error message provided";
			else
				return e.getMessage();
		}));
	}
	
	/**
	 * Unwraps the try, throwing the exception on failure
	 * @return The success value on success
	 * @throws TryFailedException Throws on failure
	 */
	public T unwrap() throws TryFailedException
	{
		throwIfFailure();
		return getSuccess();
	}
	
	/**
	 * Unwraps the try, throwing a runtime exception on failure
	 * @return The success value on success
	 * @throws TryFailedRuntimeException throws on failure
	 */
	public T unwrapRuntime() throws TryFailedRuntimeException
	{
		return getSuccess();
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
			throw e.apply(getFailure());
		else
			return getSuccess();
	}
	
	/**
	 * @return Whether this try is a success
	 */
	public boolean isSuccess()
	{
		return success.isDefined();
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
			throw new TryFailedException(getMessage(), getFailure());
	}
	
	/**
	 * Throws an exception if this try is a failure. The exception is a runtime exception
	 * @throws TryFailedRuntimeException Throws on failure
	 */
	public void throwRuntimeIfFailure() throws TryFailedRuntimeException
	{
		if (isFailure())
			throw new TryFailedRuntimeException(getMessage(), getFailure());
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
	 * Transforms this try into an either
	 * @return This try as an either
	 */
	public Either<Exception, T> toEither()
	{
		if (isSuccess())
			return Either.right(getSuccess());
		else
			return Either.left(getFailure());
	}
	
	/**
	 * Maps the success value of this try
	 * @param f a transformation function
	 * @return The transformed try
	 */
	public <B> Try<B> map(Function<? super T, B> f)
	{
		return new Try<>(success().map(f), failure(), message);
	}
	
	/**
	 * Maps the success value of this try, caching possible errors
	 * @param f A mapping function that may fail
	 * @return The results of the map
	 */
	public <B> Try<B> tryMap(ThrowingFunction<? super T, B, ?> f)
	{
		return flatMap(f);
	}
	
	/**
	 * Maps the success value of this try
	 * @param f a transformation function
	 * @return The transformed try
	 */
	public <B> Try<B> flatMap(Function<? super T, ? extends Try<B>> f)
	{
		if (isSuccess())
			return f.apply(getSuccess());
		else
			return Try.failure(getFailure(), message);
	}
	
	/**
	 * Handles either a success or a failure
	 * @param successHandler The function called on success
	 * @param errorHandler the function called on failure
	 */
	public void handle(Consumer<? super T> successHandler, Consumer<? super Exception> errorHandler)
	{
		if (isSuccess())
			successHandler.accept(getSuccess());
		else
			errorHandler.accept(getFailure());
	}
	
	/**
	 * Handles either a success or a failure, creating a result value as well
	 * @param successHandler A function for mapping success value
	 * @param errorHandler A function for mapping error value
	 * @return The mapped value
	 */
	public <B> B handleMap(Function<? super T, ? extends B> successHandler, 
			Function<? super Exception, ? extends B> errorHandler)
	{
		if (isSuccess())
			return successHandler.apply(getSuccess());
		else
			return errorHandler.apply(getFailure());
	}
	
	/**
	 * Performs an operation on the try's success value or fails
	 * @param successHandler The function called on success
	 * @throws TryFailedException Throws on failure
	 */
	public void forEach(Consumer<? super T> successHandler) throws TryFailedException
	{
		successHandler.accept(unwrap());
	}
	
	/**
	 * Performs an operation on the try's success value or fails
	 * @param successHandler The function called on success
	 * @throws TryFailedRuntimeException Throws on failure
	 */
	public void forEachRuntime(Consumer<? super T> successHandler) throws TryFailedRuntimeException
	{
		successHandler.accept(unwrapRuntime());
	}
	
	/**
	 * Performs an operation on the try's success value or fails
	 * @param successHandler The function called on success
	 * @throws TryFailedException Throws on failure
	 */
	public <E extends Exception> void forEachThrowing(ThrowingConsumer<? super T, ? extends E> successHandler) throws TryFailedException, E
	{
		successHandler.accept(unwrap());
	}
	
	/**
	 * Performs an operation on the try's success value or fails
	 * @param successHandler The function called on success
	 * @throws TryFailedRuntimeException Throws on failure
	 */
	public void forEachThrowingRuntime(ThrowingConsumer<? super T, ?> successHandler) throws TryFailedRuntimeException
	{
		throwRuntimeIfFailure();
		try
		{
			successHandler.accept(getSuccess());
		}
		catch (Exception e)
		{
			throw new TryFailedRuntimeException(e);
		}
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
			super(cause.getMessage(), cause);
		}
		
		/**
		 * Wraps another exception
		 * @param message The message sent with this exception
		 * @param cause The causing exception
		 */
		public TryFailedException(String message, Throwable cause)
		{
			super(message, cause);
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
	
	/**
	 * These exceptions are thrown by failing tries. These are not meant to be cached, but should break the program 
	 * flow instead
	 * @author Mikko Hilpinen
	 * @since 25.5.2018
	 */
	public static class TryFailedRuntimeException extends RuntimeException
	{
		private static final long serialVersionUID = -7433461448278614125L;

		/**
		 * Wraps another exception
		 * @param cause The causing exception
		 */
		public TryFailedRuntimeException(Exception cause)
		{
			super(cause.getMessage(), cause);
		}
		
		/**
		 * Wraps another exception
		 * @param message The message sent with this exception
		 * @param cause The causing exception
		 */
		public TryFailedRuntimeException(String message, Exception cause)
		{
			super(message, cause);
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
