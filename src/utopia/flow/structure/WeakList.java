package utopia.flow.structure;

import java.lang.ref.WeakReference;

import utopia.flow.structure.iterator.FlatIterator;
import utopia.flow.structure.iterator.MapIterator;
import utopia.flow.structure.iterator.RichIterator;
import utopia.flow.util.StringRepresentable;

/**
 * A weak list only holds weak references to its items. The list cannot be changed from the outside but its contents 
 * will change as garbage collector clears referenced items.
 * @author Mikko Hilpinen
 * @param <T> The type of item referenced by this list
 * @since 28.6.2018
 */
public class WeakList<T> implements RichIterable<T>, StringRepresentable
{
	// ATTRIBUTES	--------------------
	
	private ImmutableList<WeakReference<T>> references;
	
	
	// CONSTRUCTOR	--------------------
	
	private WeakList(ImmutableList<WeakReference<T>> references)
	{
		this.references = references;
	}
	
	/**
	 * @return An empty list
	 */
	public static <T> WeakList<T> empty()
	{
		return new WeakList<>(ImmutableList.empty());
	}
	
	/**
	 * Creates a new weakly referenced list
	 * @param items The items referenced by this list
	 * @return A weakly referenced list
	 */
	public static <T> WeakList<T> of(ImmutableList<? extends T> items)
	{
		return new WeakList<>(items.map(WeakReference::new));
	}

	/**
	 * Creates a new weakly referenced list with a single item
	 * @param item The item referenced by this list
	 * @return A list referencing the item
	 */
	public static <T> WeakList<T> withValue(T item)
	{
		return of(ImmutableList.withValue(item));
	}
	
	/**
	 * Creates a weakly referenced list with possible multiple items
	 * @param first The first item referenced
	 * @param second The second item referenced
	 * @param more More referenced items
	 * @return A list weakly referencing the provided items
	 */
	@SafeVarargs
	public static <T> WeakList<T> withValues(T first, T second, T... more)
	{
		return of(ImmutableList.withValues(first, second, more));
	}
	
	
	// IMPLEMENTED	-------------------

	@Override
	public RichIterator<T> iterator()
	{
		return new FlatIterator<>(new MapIterator<>(this.references.iterator(), ref -> new Option<>(ref.get())));
	}
	
	@Override
	public String toString()
	{
		return toStrongList().toString();
	}
	
	
	// OTHER	----------------------
	
	/**
	 * @return A strongly referenced version of the remaining items in this list
	 */
	public ImmutableList<T> toStrongList()
	{
		return view().force();
	}
	
	/**
	 * Creates a new list with a reference to an additional item
	 * @param item The item to be added
	 * @return A list with an additional reference
	 */
	public WeakList<T> plus(T item)
	{
		return new WeakList<>(clearMissingReferences().plus(new WeakReference<>(item)));
	}
	
	/**
	 * Creates a new list with additional references
	 * @param items The items to be referenced
	 * @return A list with additional references
	 */
	public WeakList<T> plus(Iterable<? extends T> items)
	{
		return WeakList.of(toStrongList().plus(items));
	}
	
	private ImmutableList<WeakReference<T>> clearMissingReferences()
	{
		this.references = this.references.filter(ref -> ref.get() != null);
		return this.references;
	}
}
