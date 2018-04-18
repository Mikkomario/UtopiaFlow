package utopia.flow.util;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This inteface should be implemented by elements that can be converted to streams
 * @author Mikko Hilpinen
 * @param <T> The type of bjects in the stream
 * @since 9.3.2018
 */
public interface Streamable<T>
{
	// ABSTRACT METHODS	----------------
	
	/**
	 * @return A stream from this object's contents
	 */
	public Stream<T> stream();
	
	
	// OTHER METHODS	----------------
	
	/**
	 * Performs a throwing operation on each of the elements in this collection. Stops iterating on the first exception.
	 * @param f The function that is performed for each element in the list
	 * @throws Exception The first exception thrown by the function
	 */
	public default <E extends Exception> void forEachThrowing(ThrowingConsumer<? super T, ? extends E> f) throws E
	{
		for (T item : stream().collect(Collectors.toList()))
		{
			f.accept(item);
		}
	}
}
