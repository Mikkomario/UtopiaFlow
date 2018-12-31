package utopia.flow.util;

/**
 * This interface is implemented by classes that may produce replicates of themselves. Classes with value semantics 
 * don't need to implement this interface since they can be shared without copying
 * @author Mikko Hilpinen
 * @since 31.12.2018
 * @param <T> The result class of a copy
 */
public interface Copyable<T>
{
	/**
	 * @return A copy / replicate of this instance
	 */
	public T copy();
}
