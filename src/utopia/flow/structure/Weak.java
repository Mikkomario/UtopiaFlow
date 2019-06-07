package utopia.flow.structure;

import java.lang.ref.WeakReference;

import utopia.flow.structure.iterator.RichIterator;

/**
 * A weak holds a single value with a weak reference. Weaks may be converted to options at any point. The user must 
 * not expect that the contents of the weak are always there.
 * @author Mikko Hilpinen
 * @param <T> The type of item in this container
 * @since 3.7.2018
 */
public class Weak<T> implements RichIterable<T>, Wrapper<Option<T>>
{
	// ATTRIBUTES	--------------------
	
	private Option<WeakReference<T>> ref;
	
	
	// CONSTRUCTOR	--------------------
	
	private Weak()
	{
		this.ref = Option.none();
	}
	
	/**
	 * Creates a new weak container
	 * @param item The item in this container
	 */
	public Weak(T item)
	{
		this.ref = Option.some(new WeakReference<>(item));
	}
	
	/**
	 * @return A new weak element with no referenced item
	 */
	public static <T> Weak<T> empty()
	{
		return new Weak<>();
	}
	
	
	// IMPLEMENTED	--------------------

	@Override
	public RichIterator<T> iterator()
	{
		return new WeakIterator();
	}
	
	
	// OTHER	------------------------
	
	/**
	 * @return A strong reference to the item in this container. 
	 * None if the item was already taken by the garbage collector
	 */
	@Override
	public Option<T> get()
	{
		return ref.flatMap(r -> new Option<>(r.get()));
	}
	
	
	// NESTED CLASSES	----------------
	
	private class WeakIterator implements RichIterator<T>
	{
		private Lazy<Option<T>> next = new Lazy<>(Weak.this::get);
		private boolean consumed = false;
		
		@Override
		public boolean hasNext()
		{
			return !consumed && next.get().isDefined();
		}

		@Override
		public T next()
		{
			consumed = true;
			return poll();
		}

		@Override
		public T poll()
		{
			return next.get().get();
		}
	}
}
