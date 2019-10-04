package utopia.java.flow.structure;

import java.util.function.Function;

import utopia.java.flow.structure.iterator.RichIterator;

/**
 * A duo holds two values of a similar type
 * @author Mikko Hilpinen
 * @since 6.7.2018
 * @param <T> The type of item in this double
 */
public class Duo<T> extends Pair<T, T> implements RichIterable<T>
{
	// CONSTRUCTOR	-------------------
	
	/**
	 * Creates a new duo
	 * @param first The first value in the double
	 * @param second The second value in the double
	 */
	public Duo(T first, T second)
	{
		super(first, second);
	}
	
	/**
	 * Wraps a pair into a duo
	 * @param pair A pair
	 * @return A duo
	 */
	public static <T> Duo<T> of(Pair<? extends T, ? extends T> pair)
	{
		return new Duo<>(pair.first(), pair.second());
	}
	
	
	// IMPLEMENTED	------------------

	@Override
	public RichIterator<T> iterator()
	{
		return new Iterator();
	}
	
	@Override
	public Option<Integer> estimatedSize()
	{
		return Option.some(2);
	}

	
	// OTHER	-----------------------
	
	/**
	 * Maps this duo to a different type
	 * @param f A mapping function
	 * @return A double with mapped values
	 */
	public <B> Duo<B> map(Function<? super T, ? extends B> f)
	{
		return new Duo<>(f.apply(first()), f.apply(second()));
	}
	
	
	// NESTED CLASSES	--------------
	
	private class Iterator implements RichIterator<T>
	{
		private int nextIndex = 0;
		
		@Override
		public boolean hasNext()
		{
			return nextIndex < 2;
		}

		@Override
		public T next()
		{
			T item = poll();
			nextIndex ++;
			return item;
		}

		@Override
		public T poll()
		{
			return nextIndex == 0 ? first() : second();
		}
	}
}
