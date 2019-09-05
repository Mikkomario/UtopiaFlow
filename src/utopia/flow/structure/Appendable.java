package utopia.flow.structure;

import java.util.Iterator;
import java.util.function.BiPredicate;

/**
 * Extended by structures that can be appended with new items
 * @author Mikko Hilpinen
 * @param <A> Type of item in this collection
 * @param <Repr> Concrete type for this collection
 * @param <DefaultBuilder> Builder used for creating concrete copies
 * @since 3.9.2019
 */
public interface Appendable<A, Repr extends Appendable<A, Repr, DefaultBuilder>, 
	DefaultBuilder extends Builder<? extends Repr, ?, A>> extends Filterable<A, Repr, DefaultBuilder>
{
	// OTHER	---------------
	
	/**
	 * @param item Item to add
	 * @return A copy of this collection with an item added
	 */
	public default Repr plus(A item)
	{
		DefaultBuilder builder = newBuilder();
		builder.add(this);
		builder.add(item);
		return builder.build();
	}
	
	/**
	 * @param items Items to add
	 * @return A copy of this collection with items added
	 */
	public default Repr plus(Iterable<? extends A> items)
	{
		Iterator<? extends A> iterator = items.iterator();
		if (iterator.hasNext())
		{
			DefaultBuilder builder = newBuilder();
			builder.add(this);
			builder.read(iterator);
			return builder.build();
		}
		else
			return self();
	}
	
	/**
	 * @param items Items to add
	 * @return A copy of this collection with items added
	 */
	public default Repr plus(A[] items)
	{
		return plus(ImmutableList.of(items));
	}
	
	/**
	 * @param first First item to add
	 * @param second Second item to add
	 * @param more More items to add
	 * @return A copy of this collection with all items added
	 */
	public default Repr plus(A first, A second, @SuppressWarnings("unchecked") A... more)
	{
		return plus(ImmutableList.withValues(first, second, more));
	}
	
	/**
	 * @param item Item to add
	 * @param equals Function for checking equality
	 * @return A copy of this collection with specified item included. If item already existed 
	 * in this collection, returns self
	 */
	public default Repr with(A item, BiPredicate<? super A, ? super A> equals)
	{
		if (contains(item, equals))
			return self();
		else
			return plus(item);
	}
	
	/**
	 * @param item Item to add
	 * @return A copy of this collection with specified item included. If item already existed 
	 * in this collection, returns self
	 */
	public default Repr with(A item)
	{
		return with(item, SAFE_EQUALS);
	}
	
	/**
	 * Adds multiple elements to this collection but keeps it distinct. Doesn't add already existing elements.
	 * @param elements The new elements
	 * @param equals method for checking equality
	 * @return A combined collection
	 */
	public default Repr plusDistinct(Iterable<? extends A> elements, BiPredicate<? super A, ? super A> equals)
	{
		ListBuilder<A> newItemsBuilder = new ListBuilder<>();
		elements.forEach(item -> 
		{
			if (!contains(item, equals))
				newItemsBuilder.add(item);
		});
		
		return plus(newItemsBuilder.build());
	}
	
	/**
	 * Adds multiple elements to this collection but keeps it distinct. Doesn't add already existing elements.
	 * @param elements The new elements
	 * @return A combined collection
	 */
	public default Repr plusDistinct(Iterable<? extends A> elements)
	{
		return plusDistinct(elements, SAFE_EQUALS);
	}
	
	/**
	 * Adds multiple elements to this list but keeps it distinct. Overwrites old elements with new versions.
	 * @param elements The new elements
	 * @param equals method for checking equality
	 * @return A combined list
	 */
	public default Repr overwriteAll(RichIterable<? extends A> elements, BiPredicate<? super A, ? super A> equals)
	{
		return minus(elements, equals).plus(elements);
	}
	
	/**
	 * Adds items to a copy of this collection. Any existing copies of the items will be removed first.
	 * @param elements Elements to add
	 * @return A copy of this collection with items added
	 */
	public default Repr overwriteAll(RichIterable<? extends A> elements)
	{
		return overwriteAll(elements, SAFE_EQUALS);
	}
	
	/**
	 * Replaces a single item in this collection
	 * @param item Item to add
	 * @param equals Function for checking equality
	 * @return Copy of this collection with a single copy of item included. If a copy existed before, 
	 * it is overwritten with provided copy.
	 */
	public default Repr overwrite(A item, BiPredicate<? super A, ? super A> equals)
	{
		return without(item, equals).plus(item);
	}
	
	/**
	 * Replaces a single item in this collection
	 * @param item Item to add
	 * @return Copy of this collection with a single copy of item included. If a copy existed before, 
	 * it is overwritten with provided copy.
	 */
	public default Repr overwrite(A item)
	{
		return overwrite(item, SAFE_EQUALS);
	}
}
