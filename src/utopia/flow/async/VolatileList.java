package utopia.flow.async;

import java.util.function.Predicate;

import utopia.flow.structure.ImmutableList;
import utopia.flow.structure.Option;
import utopia.flow.structure.Pair;
import utopia.flow.structure.RichIterable;
import utopia.flow.structure.iterator.RichIterator;

/**
 * A volatile list is a mutable list that handles items in a thread safe manner.
 * @author Mikko Hilpinen
 * @since 27.3.2019
 * @param <T> The type of items stored in this list
 */
public class VolatileList<T> extends Volatile<ImmutableList<T>> implements RichIterable<T>
{
	// CONSTRUCTOR	--------------------
	
	/**
	 * Creates a new empty list
	 */
	public VolatileList()
	{
		super(ImmutableList.empty());
	}
	
	
	// IMPLEMENTED	--------------------

	@Override
	public RichIterator<T> iterator()
	{
		return get().iterator();
	}
	
	@Override
	public Option<Integer> estimatedSize()
	{
		return get().estimatedSize();
	}
	
	
	// OTHER	------------------------
	
	/**
	 * Adds a new item to this list
	 * @param item The new item to be added
	 */
	public void add(T item)
	{
		update(l -> l.plus(item));
	}
	
	/**
	 * Adds multiple items to this list
	 * @param items The items to add
	 */
	public void addAll(RichIterable<? extends T> items)
	{
		update(l -> l.plus(items));
	}
	
	/**
	 * Removes an item from this list
	 * @param item The item to be removed from this list
	 */
	public void remove(Object item)
	{
		update(l -> l.without(item));
	}
	
	/**
	 * Filters the items in this list. Please note that unlike .get().filter(...) this operation 
	 * is actually mutating
	 * @param include A function that determines whether an item should be included
	 */
	public void filter(Predicate<? super T> include)
	{
		update(l -> l.filter(include));
	}
	
	/**
	 * Clears this list of items
	 */
	public void clear()
	{
		set(ImmutableList.empty());
	}
	
	/**
	 * Removes and returns the first item in this list, if any
	 * @return The first, now removed item in this list, if any
	 */
	public Option<T> pop()
	{
		return pop(l -> l.headOption(), l -> l.tail());
	}
	
	/**
	 * Removes and returns the first item in this list that fulfills the provided condition
	 * @param where A condition for finding the correct item
	 * @return The removed item, if one was found
	 */
	public Option<T> popFirst(Predicate<? super T> where)
	{
		return pop(l -> 
		{
			Option<Integer> index = l.indexWhere(where);
			
			if (index.isDefined())
				return new Pair<>(Option.some(l.get(index.get())), 
						l.first(index.get()).plus(l.dropFirst(index.get() + 1)));
			else
				return new Pair<>(Option.<T>none(), l);
		});
	}
}
