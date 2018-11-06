package utopia.flow.structure.iterator;

import java.util.Iterator;
import java.util.function.BiFunction;

import utopia.flow.structure.Duo;
import utopia.flow.structure.Pair;

/**
 * This iterator merges the contents of two iterators
 * @author Mikko Hilpinen
 * @since 6.11.2018
 * @param <A> The type of the first merge parameter
 * @param <B> The type of the second merge parameter
 * @param <Result> The merge result type
 */
public class MergeIterator<A, B, Result> implements RichIterator<Result>
{
	// ATTRIBUTES	--------------------
	
	private BiFunction<? super A, ? super B, ? extends Result> merge;
	private Iterator<? extends A> firstIter;
	private Iterator<? extends B> secondIter;
	
	
	// CONSTRUCTOR	--------------------
	
	/**
	 * Creates a new merge iterator
	 * @param first The first source iterator
	 * @param second The second source iterator
	 * @param merge A function for merging two items
	 */
	public MergeIterator(Iterator<? extends A> first, Iterator<? extends B> second, 
			BiFunction<? super A, ? super B, ? extends Result> merge)
	{
		this.merge = merge;
		this.firstIter = first;
		this.secondIter = second;
	}
	
	/**
	 * Creates a new merge iterator that forms pairs
	 * @param first The first source iterator
	 * @param second The second source iterator
	 * @return A new merge iterator
	 */
	public static <A, B> MergeIterator<A, B, Pair<A, B>> pair(Iterator<? extends A> first, Iterator<? extends B> second)
	{
		return new MergeIterator<>(first, second, Pair::new);
	}
	
	/**
	 * Creates a merge iterator that forms duos
	 * @param first The first source iterator
	 * @param second The second source iterator
	 * @return A new merge iterator
	 */
	public static <T> MergeIterator<T, T, Duo<T>> duo(Iterator<? extends T> first, Iterator<? extends T> second)
	{
		return new MergeIterator<>(first, second, Duo::new);
	}
	
	
	// IMPLEMENTED	--------------------

	@Override
	public boolean hasNext()
	{
		return firstIter.hasNext() && secondIter.hasNext();
	}

	@Override
	public Result next()
	{
		return merge.apply(firstIter.next(), secondIter.next());
	}
}
