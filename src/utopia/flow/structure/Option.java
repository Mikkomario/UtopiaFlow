package utopia.flow.structure;

import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import utopia.flow.function.ThrowingFunction;

/**
 * Optionals can be used for more null-safe access of values
 * @author Mikko Hilpinen
 * @param <T> The type of the value handled by this option
 * @since 6.9.2017
 */
public class Option<T> implements RichIterable<T>
{
	// ATTRIBUTES	---------------------
	
	private final T value;
	
	
	// CONSTRUCTOR	--------------------
	
	/**
	 * Creates a new option
	 * @param value The value that is wrapped in this option
	 */
	public Option(T value)
	{
		this.value = value;
	}
	
	/**
	 * Copies an option. This should only be used when the type of the option needs to change
	 * @param other Another option
	 */
	public Option(Option<? extends T> other)
	{
		if (other == null)
			this.value = null;
		else
			this.value = other.value;
	}
	
	/**
	 * @return An empty option (null value inside)
	 */
	public static <T> Option<T> none()
	{
		return new Option<>(null);
	}
	
	/**
	 * @param element An element. Not null.
	 * @return The element wrapped into an option
	 * @throws NullPointerException if element is null
	 */
	public static <T> Option<T> some(T element) throws NullPointerException
	{
		if (element == null)
			throw new NullPointerException();
		else
			return new Option<>(element);
	}

	/**
	 * @param value An integer value
	 * @param allowZero Should a zero value be allowed as some
	 * @return The same integer if it is > 0. None if not.
	 */
	public static Option<Integer> positiveInt(int value, boolean allowZero)
	{
		if (value > 0 || (value == 0 && allowZero))
			return new Option<>(value);
		else
			return none();
	}
	
	/**
	 * Flattens a two levels deep option
	 * @param option A two level deep option
	 * @return A single level deep option
	 */
	public static <T> Option<T> flatten(Option<? extends Option<? extends T>> option)
	{
		if (option.exists(o -> o.isDefined()))
			return Option.some(option.get().get());
		else
			return Option.none();
	}
	
	
	// IMPLEMENTED METHODS	-------------------
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.value == null) ? 0 : this.value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Option))
			return false;
		Option<?> other = (Option<?>) obj;
		if (this.value == null)
		{
			if (other.value != null)
				return false;
		}
		else if (!this.value.equals(other.value))
			return false;
		return true;
	}
	
	@Override
	public String toString()
	{
		if (isDefined())
			return "Some(" + this.value.toString() + ")";
		else
			return "None";
	}
	
	@Override
	public RichIterator<T> iterator()
	{
		return new OptionIterator();
	}
	
	
	// OTHER METHODS	-----------------

	/**
	 * @return If this option doesn't contain a specified value
	 */
	public boolean isEmpty()
	{
		return this.value == null;
	}
	
	/**
	 * @return If this option contains a specified value
	 */
	public boolean isDefined()
	{
		return !isEmpty();
	}
	
	/**
	 * @return The value from inside this option
	 * @throws NullPointerException If the option was empty (null value)
	 */
	public T get() throws NullPointerException
	{
		if (isDefined())
			return this.value;
		else
			throw new NullPointerException("Trying to get a value out of an empty option");
	}
	
	/**
	 * Returns the value from this option, if it is defined or a default value
	 * @param defaultValue The default value
	 * @return The value from this option or default value if there was no specified value in this option
	 */
	public T getOrElse(T defaultValue)
	{
		if (isDefined())
			return this.value;
		else
			return defaultValue;
	}
	
	/**
	 * Returns the value from this option, if it is defined or a default value
	 * @param defaultValue The default value
	 * @return The value from this option or default value if there was no specified value in this option
	 */
	public T getOrElse(Supplier<? extends T> defaultValue)
	{
		if (isDefined())
			return this.value;
		else
			return defaultValue.get();
	}
	
	/**
	 * Returns the value from this option or throws an exception
	 * @param errorSuplier The supplier that will create the exception if necessary
	 * @return The value inside this option
	 * @throws E If the option was empty
	 */
	public <E extends Throwable> T getOrFail(Supplier<E> errorSuplier) throws E
	{
		if (isDefined())
			return this.value;
		else
			throw errorSuplier.get();
	}
	
	/**
	 * Returns the value from this option or a failure if this option was empty
	 * @param errorSuplier A function for producing the correct error
	 * @return The result wrapped in a try
	 */
	public Try<T> toTry(Supplier<? extends Exception> errorSuplier)
	{
		if (isDefined())
			return Try.success(this.value);
		else
			return Try.failure(errorSuplier.get());
	}
	
	/**
	 * @param defaultOption An option returned if this option is empty
	 * @return this option, if defined, or another option if not defined
	 */
	public Option<T> orElse(Option<T> defaultOption)
	{
		if (isDefined())
			return this;
		else
			return defaultOption;
	}
	
	/**
	 * @param defaultOption An option returned if this option is empty
	 * @return this option, if defined, or another option if not defined
	 */
	public Option<T> orElse(Supplier<Option<T>> defaultOption)
	{
		if (isDefined())
			return this;
		else
			return defaultOption.get();
	}
	
	/**
	 * Maps this option into a different type of option
	 * @param f A function that transforms the value in this option
	 * @return An option wrapping the transformed value or none if this option was empty
	 */
	public <B> Option<B> map(Function<? super T, B> f)
	{
		if (isDefined())
			return new Option<>(f.apply(this.value));
		else
			return Option.none();
	}
	
	/**
	 * Maps this option into a different type of option
	 * @param f A function that transforms the value of this option but may fail
	 * @return An option wrapping the transformed value or none if this option was empty or if transform failed
	 */
	public <B> Option<B> tryMap(ThrowingFunction<? super T, B, ?> f)
	{
		if (isDefined())
			return f.apply(this.value).success();
		else
			return Option.none();
	}
	
	/**
	 * Maps this option into a different type of option, flattening the result
	 * @param f A function that transforms the value in this option but may return None
	 * @return An option wrapping the transformed value or none if this option was empty
	 */
	public <B> Option<B> flatMap(Function<? super T, Option<B>> f)
	{
		Option<Option<B>> result = map(f);
		if (result.isDefined())
			return result.get();
		else
			return Option.none();
	}
	
	/**
	 * Maps this option into a different type of option, flattening the result
	 * @param f A function that transforms the value in this option but may return None and may fail
	 * @return An option wrapping the transformed value or none if this option was empty or if transform failed
	 */
	public <B> Option<B> tryFlatMap(ThrowingFunction<? super T, Option<B>, ?> f)
	{
		Option<Option<B>> result = tryMap(f);
		if (result.isDefined())
			return result.get();
		else
			return Option.none();
	}
	
	/**
	 * Checks whether this option has an equal value with anohter option
	 * @param other Another option
	 * @param equals A method for checking equality between values
	 * @return Whether the two options have equal values
	 */
	public boolean hasEqualValueWith(Option<? extends T> other, BiPredicate<? super T, ? super T> equals)
	{
		if (isEmpty())
			return other.isEmpty();
		else if (other.isEmpty())
			return false;
		else
			return equals.test(getValue(), other.getValue());
	}
	
	/**
	 * Checks whether this option has an equal value with anohter option
	 * @param other Another option
	 * @return Whether the two options have equal values
	 */
	public boolean hasEqualValueWith(Option<?> other)
	{
		if (isEmpty())
			return other.isEmpty();
		else if (other.isEmpty())
			return false;
		else
			return getValue().equals(other.getValue());
	}
	
	/**
	 * @return The value inside this option, <b>which may be null</b>
	 * @see #get()
	 * @see #getOrElse(Object)
	 */
	public T getValue()
	{
		return this.value;
	}
	
	/**
	 * Checks whether the value inside this option is equal to another value
	 * @param other Another value
	 * @return Whether the value in this option is equal to the provided value
	 */
	public boolean valueEquals(Object other)
	{
		return contains(other);
	}
	
	/**
	 * @param f a filter
	 * @return This option if it is accepted by the filter. None otherwise
	 */
	public Option<T> filter(Predicate<? super T> f)
	{
		if (exists(f))
			return this;
		else
			return none();
	}
	
	/**
	 * Calls either the itemHandler or the emptyHandler based on whether the option has a value or not
	 * @param itemHandler The function that will be called if the option is defined
	 * @param emptyHandler The function that will be called if the option is empty
	 */
	public void handle(Consumer<? super T> itemHandler, Runnable emptyHandler)
	{
		if (isDefined())
			itemHandler.accept(this.value);
		else
			emptyHandler.run();
	}
	
	/**
	 * @return A list representation of this option
	 */
	public ImmutableList<T> toList()
	{
		if (isDefined())
			return ImmutableList.withValue(this.value);
		else
			return ImmutableList.empty();
	}
	
	
	// NESTED CLASSES	----------------------
	
	private class OptionIterator implements RichIterator<T>
	{
		// ATTRIBUTES	----------------------
		
		private boolean consumed = false;
		
		
		// IMPLEMENTED METHODS	--------------
		
		@Override
		public boolean hasNext()
		{
			return !this.consumed && isDefined();
		}

		@Override
		public T next()
		{
			this.consumed = true;
			return get();
		}	
	}
}