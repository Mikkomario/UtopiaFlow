package utopia.flow.structure;

import java.lang.ref.WeakReference;

/**
 * A weak holds a single value with a weak reference. Weaks may be converted to options at any point. The user must 
 * not expect that the contents of the weak are always there.
 * @author Mikko Hilpinen
 * @param <T> The type of item in this container
 * @since 3.7.2018
 */
public class Weak<T> implements RichIterable<T>
{
	// ATTRIBUTES	--------------------
	
	private WeakReference<T> ref;
	
	
	// CONSTRUCTOR	--------------------
	
	/**
	 * Creates a new weak container
	 * @param item The item in this container
	 */
	public Weak(T item)
	{
		this.ref = new WeakReference<>(item);
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
	public Option<T> get()
	{
		return new Option<>(this.ref.get());
	}
	
	
	// NESTED CLASSES	----------------
	
	private class WeakIterator implements RichIterator<T>
	{
		private Lazy<Option<T>> next = new Lazy<>(Weak.this::get);
		private boolean consumed = false;
		
		@Override
		public boolean hasNext()
		{
			return !this.consumed && this.next.get().isDefined();
		}

		@Override
		public T next()
		{
			return this.next.get().get();
		}
	}
}
