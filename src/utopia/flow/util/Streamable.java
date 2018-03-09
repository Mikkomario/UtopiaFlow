package utopia.flow.util;

import java.util.stream.Stream;

/**
 * This inteface should be implemented by elements that can be converted to streams
 * @author Mikko Hilpinen
 * @param <T> The type of bjects in the stream
 * @since 9.3.2018
 */
public interface Streamable<T>
{
	/**
	 * @return A stream from this object's contents
	 */
	public Stream<T> stream();
}
