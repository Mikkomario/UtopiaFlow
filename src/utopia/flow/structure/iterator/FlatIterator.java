package utopia.flow.structure.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;

import utopia.flow.structure.Option;

/**
 * These iterators are used for iterating through a flattened collection
 * @author Mikko Hilpinen
 * @since 27.6.2018
 * @param <T> The type of item produced by this iterator
 */
public class FlatIterator<T> implements RichIterator<T>
{
	// ATTRIBUTES	--------------------
	
	private Iterator<? extends Iterable<? extends T>> iterator;
	private Option<Iterator<? extends T>> currentItem = Option.none();
	
	
	// CONSTRUCTOR	--------------------
	
	/**
	 * Wraps another iterator into a flat iterator
	 * @param iterator an iterator for iterable elements
	 */
	public FlatIterator(Iterator<? extends Iterable<? extends T>> iterator)
	{
		this.iterator = iterator;
	}
	
	
	// IMPLEMENTED	--------------------

	@Override
	public boolean hasNext()
	{
		if (hasNextDirect())
			return true;
		else
			return findNextIterator();
	}

	@Override
	public T next()
	{
		if (hasNextDirect() || findNextIterator())
			return this.currentItem.get().next();
		else
			throw new NoSuchElementException();
	}

	
	// OTHER METHODS	---------------
	
	private boolean hasNextDirect()
	{
		return this.currentItem.exists(i -> i.hasNext());
	}
	
	private boolean findNextIterator()
	{
		while (this.iterator.hasNext())
		{
			Iterator<? extends T> iter = this.iterator.next().iterator();
			if (iter.hasNext())
			{
				this.currentItem = Option.some(iter);
				return true;
			}
		}
		
		return false;
	}
}
