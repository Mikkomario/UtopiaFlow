package utopia.java.flow.structure.iterator;

import java.util.NoSuchElementException;

/**
 * This iterator never returns anything
 * @author Mikko Hilpinen
 * @since 6.9.2019
 * @param <A> Type of iterated item
 */
public class EmptyIterator<A> implements RichIterator<A>
{
	@Override
	public boolean hasNext()
	{
		return false;
	}

	@Override
	public A next() throws NoSuchElementException
	{
		throw new NoSuchElementException("Trying to get an item from an empty iterator");
	}

	@Override
	public A poll() throws NoSuchElementException
	{
		throw new NoSuchElementException("Trying to get an item from an empty iterator");
	}
}
