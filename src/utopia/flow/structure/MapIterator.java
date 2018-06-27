package utopia.flow.structure;

import java.util.Iterator;
import java.util.function.Function;

/**
 * This iterator performs a map operation on the iterated items
 * @author Mikko Hilpinen
 * @since 27.6.2018
 * @param <From> The input type
 * @param <To> The output type
 */
public class MapIterator<From, To> implements RichIterator<To>
{
	// ATTRIBUTES	------------------
	
	private Iterator<? extends From> iterator;
	private Function<? super From, ? extends To> map;
	
	
	// CONSTRUCTOR	------------------
	
	/**
	 * Creates a new iterator
	 * @param i The source iterator
	 * @param map The mapping function
	 */
	public MapIterator(Iterator<? extends From> i, Function<? super From, ? extends To> map)
	{
		this.iterator = i;
		this.map = map;
	}
	
	
	// IMPLEMENTED	-----------------

	@Override
	public boolean hasNext()
	{
		return this.iterator.hasNext();
	}

	@Override
	public To next()
	{
		return this.map.apply(this.iterator.next());
	}
}
