package utopia.flow.structure;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import utopia.flow.function.ThrowingFunction;
import utopia.flow.structure.iterator.RichIterator;
import utopia.flow.util.StringRepresentable;

/**
 * Optionals can be used for more null-safe access of values
 * @author Mikko Hilpinen
 * @param <T> The type of the value handled by this option
 * @since 6.9.2017
 */
public class Option<T> implements RichIterable<T>, StringRepresentable, 
	Filterable<T, Option<T>, OptionBuilder<T>>
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
			return some(option.get().get());
		else
			return none();
	}
	
	/**
	 * Wraps an item into an option, but only if a certain condition is fulfilled
	 * @param item The wrapped item
	 * @param condition A condition for wrapping
	 * @return The item if the condition is true, none if the condition is false
	 */
	public static <T> Option<T> takeIf(T item, boolean condition)
	{
		if (condition)
			return some(item);
		else
			return none();
	}
	
	/**
	 * This method can be used instead of orElse when typing requires it
	 * @param first The first optional value
	 * @param second A function for retrieving backup value
	 * @return The first value if defined, otherwise gives the backup value
	 */
	public static <T> Option<? extends T> either(Option<? extends T> first, Supplier<? extends Option<? extends T>> second)
	{
		if (first.isDefined())
			return first;
		else
			return second.get();
	}
	
	/**
	 * This method can be used instead of getOrElse when typing requires it
	 * @param option The optional value
	 * @param backup A function for producing a back up value
	 * @return The option's value or the backup value
	 */
	public static <T> T getWithBackUp(Option<? extends T> option, Supplier<? extends T> backup)
	{
		if (option.isDefined())
			return option.get();
		else
			return backup.get();
	}
	
	
	// STATIC	-------------------------------
	
	/**
	 * Finds a maximum value from a number of optional values
	 * @param first The first value
	 * @param second The second value
	 * @param more More values
	 * @return The maximum value, if there is one
	 */
	@SafeVarargs
	public static <T extends Comparable<? super T>> Option<T> max(Option<? extends T> first, Option<? extends T> second, 
			Option<? extends T>... more)
	{
		return RichIterable.maxFrom(View.flatten(first, second, more));
	}
	
	/**
	 * Finds a minimum value from a number of optional values
	 * @param first The first value
	 * @param second The second value
	 * @param more More values
	 * @return The minimum value, if there is one
	 */
	@SafeVarargs
	public static <T extends Comparable<? super T>> Option<T> min(Option<? extends T> first, Option<? extends T> second, 
			Option<? extends T>... more)
	{
		return RichIterable.minFrom(View.flatten(first, second, more));
	}
	
	
	// IMPLEMENTED METHODS	-------------------
	
	@Override
	public OptionBuilder<T> newBuilder(Option<Integer> capacity)
	{
		return new OptionBuilder<>();
	}

	@Override
	public Option<Integer> estimatedSize()
	{
		return Option.some(isDefined() ? 1 : 0);
	}

	@Override
	public Option<T> self()
	{
		return this;
	}
	
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
	@Override
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
	 * Returns the value of this option or throws
	 * @return The value inside this option
	 * @throws EmptyResultException If this option was empty
	 */
	public T getOrFail() throws EmptyResultException
	{
		return getOrFail(() -> new EmptyResultException("The option is empty"));
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
	 * Returns the value from this option or a failure if this option was empty
	 * @return The result wrapped in a try
	 */
	public Try<T> toTry()
	{
		return toTry(() -> new EmptyResultException("The option is empty"));
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
	public <B> Option<B> map(Function<? super T, ? extends B> f)
	{
		if (isDefined())
			return new Option<>(f.apply(this.value));
		else
			return Option.none();
	}
	
	/**
	 * Maps item in this option. May throw.
	 * @param f A mapping function
	 * @return Mapped option
	 * @throws E If mapping function threw
	 */
	public <B, E extends Exception> Option<B> mapThrowing(ThrowingFunction<? super T, ? extends B, 
			? extends E> f) throws E
	{
		return mapThrowing(f, i -> new OptionBuilder<>());
	}
	
	/**
	 * Maps item in this option. May fail.
	 * @param f A mapping function.
	 * @return Mapped option. Failure if map failed.
	 */
	public <B> Try<Option<B>> tryMap(Function<? super T, ? extends Try<? extends B>> f)
	{
		return tryMap(f, i -> new OptionBuilder<>());
	}
	
	/**
	 * Maps this option into a different type of option, flattening the result
	 * @param f A function that transforms the value in this option but may return None
	 * @return An option wrapping the transformed value or none if this option was empty
	 */
	public <B> Option<B> flatMap(Function<? super T, ? extends Option<B>> f)
	{
		Option<Option<B>> result = map(f);
		if (result.isDefined())
			return result.get();
		else
			return Option.none();
	}
	
	/**
	 * Maps this option into a different type, flattening the result. May throw.
	 * @param f A mapping function that may return none
	 * @return Mapped item, if any
	 * @throws E If mapping function threw
	 */
	public <B, E extends Exception> Option<B> flatMapThrowing(ThrowingFunction<? super T, 
			? extends Option<? extends B>, ? extends E> f) throws E
	{
		return flatMapThrowing(f, OptionBuilder::new);
	}
	
	/**
	 * Maps this option into a different type of option, flattening the result. May fail.
	 * @param f A mapping function. May fail.
	 * @return Mapped option. Failure if mapping function failed.
	 */
	public <B> Try<Option<B>> tryFlatMap(
			Function<? super T, ? extends Try<? extends Option<? extends B>>> f)
	{
		return tryFlatMap(f, OptionBuilder::new);
	}
	
	/**
	 * Maps the value of this option. Silently catches any thrown exceptions.
	 * @param f A mapping function
	 * @return Mapped otpion
	 */
	public <B> Option<B> mapCatching(ThrowingFunction<? super T, ? extends B, ?> f)
	{
		return mapCatching(f, i -> new OptionBuilder<>());
	}
	
	/**
	 * Maps the value of this option. Silently catches any thrown exceptions.
	 * @param f A mapping function that may return none
	 * @return Mapped option
	 */
	public <B> Option<B> flatMapCatching(ThrowingFunction<? super T, 
			? extends Option<? extends B>, ?> f)
	{
		return flatMapCatching(f, OptionBuilder::new);
	}
	
	/**
	 * Checks whether this option has an equal value with another option
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
	 * Either maps a value in this option or generates a new value and returns it instead
	 * @param itemMap A map function
	 * @param emptyHandler A back-up generator function
	 * @return Mapped item or the backup item
	 */
	public <B> B handleMap(Function<? super T, ? extends B> itemMap, Supplier<? extends B> emptyHandler)
	{
		if (isDefined())
			return itemMap.apply(value);
		else
			return emptyHandler.get();
	}
	
	/**
	 * Merges this option with another. Produces a value only if both options are defined.
	 * @param other Another option
	 * @param merge A merge function
	 * @return Merged value if both options were non-empty
	 */
	public <B, Merge> Option<Merge> mergedWith(Option<? extends B> other, 
			BiFunction<? super T, ? super B, ? extends Merge> merge)
	{
		return mergedWith(other, merge, i -> new OptionBuilder<>());
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
			return !consumed && isDefined();
		}

		@Override
		public T next()
		{
			consumed = true;
			return get();
		}

		@Override
		public T poll()
		{
			return get();
		}	
	}
}
