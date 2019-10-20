package utopia.java.flow.util;

import utopia.java.flow.function.ThrowingConsumer;
import utopia.java.flow.structure.Wrapper;

/**
 * This class wraps an item as an autocloseable
 * @author Mikko Hilpinen
 * @param <T> The type of the wrapped item
 * @since 1.8.2018
 */
public class AutoCloseWrapper<T> implements AutoCloseable, Wrapper<T>
{
	// ATTRIBUTES	-----------------
	
	private T item;
	private ThrowingConsumer<? super T, ?> close;
	
	
	// CONSTRUCTOR	-----------------
	
	/**
	 * Creates a new wrapper
	 * @param item The item to be wrapped
	 * @param close A closing function
	 */
	public AutoCloseWrapper(T item, ThrowingConsumer<? super T, ?> close)
	{
		this.item = item;
		this.close = close;
	}
	
	/**
	 * Wraps an item as an autocloseable
	 * @param item the item to be wrapped
	 * @param close A closing function
	 * @return A wrapped item
	 */
	public static <T> AutoCloseWrapper<T> wrap(T item, ThrowingConsumer<? super T, ?> close)
	{
		return new AutoCloseWrapper<>(item, close);
	}
	
	
	// IMPLEMENTED	-----------------

	@Override
	public void close() throws Exception
	{
		close.accept(item);
	}
	
	
	// OTHER	--------------------
	
	@Override
	public T get()
	{
		return item;
	}
}
