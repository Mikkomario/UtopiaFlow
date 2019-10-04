package utopia.java.flow.structure;

import java.util.function.Supplier;

/**
 * Wrappers are used for wrapping an item, possibly providing extra functionality.
 * @author Mikko Hilpinen
 * @param <T> The type of the wrapped item
 * @since 1.8.2018
 */
public interface Wrapper<T> extends Supplier<T>
{
	/**
	 * @return The item in this wrapper
	 */
	@Override
	public T get();
}
