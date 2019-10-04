package utopia.flow.async;

import java.util.function.Consumer;
import java.util.function.Function;

import utopia.flow.function.ThrowingConsumer;
import utopia.flow.function.ThrowingFunction;
import utopia.java.flow.structure.Pair;

/**
 * This class is used as a wrapper for a value that may be changed from multiple threads. This class should 
 * only be used with immutable instances that optimally also have value semantics. This class is, by nature, mutable. 
 * Please note that, to avoid deadlocks, you should not use volatile values inside other volatile values.
 * @author Mikko Hilpinen
 * @param <T> The type of the value held in this object
 * @since 7.8.2018
 */
public class Volatile<T>
{
	// ATTRIBUTES	--------------
	
	private volatile T value;
	
	
	// CONSTRUCTOR	--------------
	
	/**
	 * Creates a new volatile wrapper
	 * @param value The original wrapped value
	 */
	public Volatile(T value)
	{
		this.value = value;
	}

	
	// OTHER	------------------
	
	/**
	 * @return The current, up-to-date value of this container
	 */
	public synchronized T get()
	{
		return value;
	}
	
	/**
	 * Sets a new value to this container. The new value shouldn't be related to the previous value
	 * @param newValue The new value for this container
	 * @see #update(Function)
	 */
	public synchronized void set(T newValue)
	{
		this.value = newValue;
	}
	
	/**
	 * Updates the value of this container based on the previous value. The value will be updated to the return value 
	 * of the function. The current value of this container is passed to the function. This container is locked during 
	 * the operation.
	 * @param modifier A function that is used for modifying the value
	 */
	public synchronized void update(Function<? super T, ? extends T> modifier)
	{
		this.value = modifier.apply(value);
	}
	
	/**
	 * Updates the value of this container based on the previous value. The value will be updated to the return value 
	 * of the function. The current value of this container is passed to the function. This container is locked during 
	 * the operation. May throw.
	 * @param modifier A function that is used for modifying the value
	 * @throws E Error if the updating function throws
	 */
	public synchronized <E extends Exception> void tryUpdate(ThrowingFunction<? super T, ? extends T, 
			? extends E> modifier) throws E
	{
		this.value = modifier.throwingApply(value);
	}
	
	/**
	 * Updates the value of this container based on the previous value. The value will be updated to the return value 
	 * of the function. The current value of this container is passed to the function. This container is locked during 
	 * the operation.
	 * @param modifier A function that is used for modifying the value
	 * @return The new value in this container
	 */
	public synchronized T updateAndGet(Function<? super T, ? extends T> modifier)
	{
		this.value = modifier.apply(value);
		return this.value;
	}
	
	/**
	 * Retrieves the value in this volatile container, then updates this container
	 * @param newValue The new value that will be set to this container
	 * @return The previous value of this container
	 */
	public synchronized T getAndSet(T newValue)
	{
		T result = value;
		value = newValue;
		return result;
	}
	
	/**
	 * This method reads a value from the held value, then mutates the held value and finally returns the read value.
	 * @param taker A function for taking / reading a value from the currently held value
	 * @param modifier A function for updating the value afterwards
	 * @return The value that was read before the update
	 */
	public synchronized <B> B pop(Function<? super T, ? extends B> taker, Function<? super T, ? extends T> modifier)
	{
		B result = taker.apply(value);
		update(modifier);
		return result;
	}
	
	/**
	 * This method reads a value and mutates the current value at the same time
	 * @param update A function for reading and updating the value. The first result should be the read value while 
	 * the second result should be the new modified value for this container
	 * @return The value read by the function
	 */
	public synchronized <B> B pop(Function<? super T, ? extends Pair<B, T>> update)
	{
		Pair<B, T> result = update.apply(value);
		value = result.second();
		return result.first();
	}
	
	/**
	 * Locks the value in this container for the current thread only for the duration of the action
	 * @param action An action
	 */
	public synchronized void lockWhile(Consumer<? super T> action)
	{
		action.accept(value);
	}
	
	/**
	 * Locks the value in this container for the current thread only for the duration of the action. The action may throw.
	 * @param action An action
	 * @throws E If the action fails
	 */
	public synchronized <E extends Exception> void tryLockWhile(ThrowingConsumer<? super T, ? extends E> action) throws E
	{
		action.accept(value);
	}
}
