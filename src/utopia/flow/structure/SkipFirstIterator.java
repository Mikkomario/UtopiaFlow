package utopia.flow.structure;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * This iterator skips the first n item(s), but works like a normal iterator afterwards
 * @author Mikko Hilpinen
 * @since 1.11.2018
 * @param <T> The type of item iterated through
 */
public class SkipFirstIterator<T> implements RichIterator<T>
{
	// ATTRIBUTES	--------------------
	
	private Iterator<? extends T> iter;
	private int skipsLeft;
	
	
	// CONSTRUCTOR	--------------------
	
	/**
	 * @param iter The underlying iterator
	 * @param skipAmount The amount of items skipped
	 */
	public SkipFirstIterator(Iterator<? extends T> iter, int skipAmount)
	{
		this.skipsLeft = skipAmount;
		this.iter = iter;
	}
	
	
	// IMPLEMENTED METHODS	-----------

	@Override
	public boolean hasNext()
	{
		return doSkip();
	}

	@Override
	public T next()
	{
		if (doSkip())
			return iter.next();
		else
			throw new NoSuchElementException();
	}
	
	
	// OTHER	----------------------
	
	private boolean doSkip()
	{
		while (skipsLeft > 0)
		{
			if (iter.hasNext())
			{
				iter.next();
				skipsLeft --;
			}
			else
			{
				skipsLeft = 0;
				return false;
			}
		}
		
		return iter.hasNext();
	}
}
