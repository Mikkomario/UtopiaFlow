package utopia.flow.util;

import utopia.flow.structure.RichIterable;

/**
 * This inteface should be implemented by elements that can be converted to streams
 * @author Mikko Hilpinen
 * @param <T> The type of bjects in the stream
 * @since 9.3.2018
 * @deprecated Moved all methods to RichIterable interface. This interface is left only for support of old code
 */
public interface Streamable<T> extends RichIterable<T>
{
	
}
