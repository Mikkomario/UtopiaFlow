package utopia.flow.util;

import java.util.function.Consumer;
import java.util.function.Function;

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
	 * @throws Exception Throws on failure
	 */
	public T unwrap() throws Exception
	{
		throwIfFailure();
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
	 * @throws Exception Throws on failure
	 */
	public void throwIfFailure() throws Exception
	{
		if (isFailure())
			throw getFailure().get();
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
	 * @throws Exception Throws on failure
	 */
	public void forEachThrowing(ThrowingConsumer<? super T> successHandler) throws Exception
	{
		throwIfFailure();
		successHandler.accept(getSuccess().get());
	}
}
