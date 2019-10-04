package utopia.java.flow.structure.iterator;

import java.util.function.Supplier;

/**
 * This iterator keeps repeating the same content over and over (repeats infinitely, except for 0 sized collections)
 * @author Mikko Hilpinen
 * @since 26.10.2018
 * @param <T> The type of item iterated
 */
public class RepeatingIterator<T> implements RichIterator<T>
{
	// ATTRIBUTES	------------------
	
	private Supplier<? extends RichIterator<T>> makeIterator;
	private RichIterator<T> currentIterator;
	
	
	// CONSTRUCTOR	------------------
	
	/**
	 * Creates a new repeating iterator
	 * @param makeIterator A function for producing new iterators
	 */
	public RepeatingIterator(Supplier<? extends RichIterator<T>> makeIterator)
	{
		this.makeIterator = makeIterator;
		this.currentIterator = makeIterator.get();
	}
	
	
	// IMPLEMENTED	------------------

	@Override
	public boolean hasNext()
	{
		// Always has a next item, until stuck with an iterator with 0 values
		return currentIterator.hasNext();
	}

	@Override
	public T next()
	{
		T next = currentIterator.next();
		
		// Switches iterators once the previous becomes empty
		if (!currentIterator.hasNext())
			currentIterator = makeIterator.get();
		
		return next;
	}

	@Override
	public T poll()
	{
		return currentIterator.poll();
	}
}
