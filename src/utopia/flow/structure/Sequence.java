package utopia.flow.structure;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import utopia.flow.function.ThrowingPredicate;

/**
 * Sequences are collections that have static ordering and can be accessed with index
 * @author Mikko Hilpinen
 * @since 6.9.2019
 * @param <A> Type of iterated item
 * @param <Repr> Concrete implementation of this interface
 * @param <DefaultBuilder> Builder used to construct 'Repr'
 */
public interface Sequence<A, Repr extends RichIterable<? extends A>, 
	DefaultBuilder extends Builder<? extends Repr, ?, A>> extends Filterable<A, Repr, DefaultBuilder>
{
	// ABSTRACT	--------------------
	
	/**
	 * @return The number of items in this sequence. Should be static.
	 */
	public int size();
	
	/**
	 * Finds an item in this sequence
	 * @param index Targeted item index
	 * @return Item at specified index
	 * @throws IndexOutOfBoundsException If index was outside this sequence's bounds (0 - size-1)
	 */
	public A get(int index) throws IndexOutOfBoundsException;
	
	
	// OTHER	--------------------
	
	/**
	 * @return The range of the indices of this list
	 */
	public default IntRange indices()
	{
		return Range.fromUntil(0, size());
	}
	
	/**
	 * Finds the item at specified index or none if this sequence doesn't have such an index
	 * @param index The target index
	 * @return An item from the specified index
	 */
	public default Option<A> getOption(int index)
	{
		if (index >= 0 && index < size())
			return Option.some(get(index));
		else
			return Option.none();
	}
	
	/**
	 * Retrieves a range of items from this sequence
	 * @param from The minimum index (inclusive)
	 * @param to The maximum index (inclusive)
	 * @return Items within the specified range. The size of the sequence may be smaller than the 
	 * length of the range if the range was (partially) outside this sequences's range
	 */
	public default Repr getInRange(int from, int to)
	{
		int realStart = Math.max(from, 0);
		int realEnd = Math.min(to, size() - 1);
		
		if (realEnd <= realStart)
			return newBuilder().build();
		else
		{
			DefaultBuilder builder = newBuilder();
			for (int i = realStart; i <= realEnd; i++)
			{
				builder.add(get(i));
			}
			return builder.build();
		}
	}
	
	/**
	 * @param range Targeted range
	 * @return All items in this sequence that lay within specified range
	 */
	public default Repr get(Range<? extends Integer> range)
	{
		return getInRange(range.getStart(), range.getEnd());
	}
	
	/**
	 * Checks whether this sequence contains all items and only items from another sequence
	 * @param other Another sequence
	 * @param equals function for checking equality
	 * @return Whether this sequence contains all of and only those of specified items
	 */
	public default <B> boolean containsExactly(Sequence<B, ?, ?> other, 
			BiPredicate<? super A, ? super B> equals)
	{
		return size() == other.size() && containsAll(other, equals);
	}
	
	/**
	 * Checks whether this sequence contains all items and only items from another sequence
	 * @param other Another sequence
	 * @return Whether this sequence contains all of and only those of specified items
	 */
	public default boolean containsExactly(Sequence<?, ?, ?> other)
	{
		return containsExactly(other, SAFE_EQUALS);
	}
	
	/**
	 * @param other Another sequence
	 * @param equals Equality check between items
	 * @return Whether this sequence contains all the same items in the same order as 'other'
	 */
	public default <B> boolean equals(Sequence<B, ?, ?> other, BiPredicate<? super A, ? super B> equals)
	{
		if (size() != other.size())
			return false;
		else
			return indices().forAll(i -> equals.test(get(i), other.get(i)));
	}
	
	/**
	 * @param other Another sequence
	 * @return Whether this sequence contains all the same items in the same order as 'other'
	 */
	public default boolean equals(Sequence<?, ?, ?> other)
	{
		return equals(other, SAFE_EQUALS);
	}
	
	/**
	 * Finds the first index where the predicate is true
	 * @param f a predicate for finding the index
	 * @return The first index where the predicate is true or None if no such index exists
	 */
	public default Option<Integer> indexWhere(Predicate<? super A> f)
	{
		return indices().find(i -> f.test(get(i)));
	}
	
	/**
	 * Finds first index where specified condition is met
	 * @param f A predicate for finding index
	 * @return Found index or none if no such index was found
	 * @throws E If predicate throws
	 */
	public default <E extends Exception> Option<Integer> indexWhereThrowing(
			ThrowingPredicate<? super A, ? extends E> f) throws E
	{
		return indices().findThrowing(i -> f.test(get(i)));
	}
	
	/**
	 * @param f A predicate for finding an item
	 * @return Index of the last item that satisfies the specified predicate
	 */
	public default Option<Integer> lastIndexWhere(Predicate<? super A> f)
	{
		return indices().reversed().find(i -> f.test(get(i)));
	}
	
	/**
	 * @param f A predicate for finding an item
	 * @return Index of the last item that satisfies the specified predicate
	 * @throws E If predicate throws
	 */
	public default <E extends Exception> Option<Integer> lastIndexWhereThrowing(
			ThrowingPredicate<? super A, ? extends E> f) throws E
	{
		return indices().reversed().findThrowing(i -> f.test(get(i)));
	}
	
	/**
	 * @param item Searched item
	 * @param equals A function for checking equality
	 * @return The first index of specified item in this sequence
	 */
	public default <B> Option<Integer> indexOf(B item, BiPredicate<? super A, ? super B> equals)
	{
		return indexWhere(a -> equals.test(a, item));
	}
	
	/**
	 * @param item Searched item
	 * @return The first index of specified item in this sequence
	 */
	public default Option<Integer> indexOf(Object item)
	{
		return indexOf(item, SAFE_EQUALS);
	}
	
	/**
	 * @param item Searched item
	 * @param equals A function for checking equality
	 * @return The last index of specified item in this sequence
	 */
	public default <B> Option<Integer> lastIndexOf(B item, BiPredicate<? super A, ? super B> equals)
	{
		return lastIndexWhere(a -> equals.test(a, item));
	}
	
	/**
	 * @param item Searched item
	 * @return The last index of specified item in this sequence
	 */
	public default Option<Integer> lastIndexOf(Object item)
	{
		return lastIndexOf(item, SAFE_EQUALS);
	}
	
	/**
	 * @param f A predicate
	 * @return The last item that satisfies the specified predicate
	 */
	public default Option<A> lastWhere(Predicate<? super A> f)
	{
		return reversedView().find(f);
	}
	
	/**
	 * @return A view of this sequence that traverses from end to beginning
	 */
	public default View<A> reversedView()
	{
		return indices().reversed().view().map(this::get);
	}
	
	/**
	 * @return A copy of this sequence where item order has been reversed
	 */
	public default Repr reversed()
	{
		DefaultBuilder builder = newBuilder();
		builder.add(reversedView());
		return builder.build();
	}

	/**
	 * @param n A number of elements to be dropped
	 * @return A copy of this sequence without the first n elements
	 */
	public default Repr dropFirst(int n)
	{
		if (isEmpty() || n <= 0)
			return self();
		else if (n >= size())
			return newBuilder().build();
		else
			return indices().withStart(n).map(this::get, this::newBuilder);
	}
	
	/**
	 * @return A copy of this sequence without the first element
	 */
	public default Repr tail()
	{
		return dropFirst(1);
	}
	
	/**
	 * @param n The number of elements to be dropped
	 * @return A copy of this sequence without the last n elements
	 */
	public default Repr dropLast(int n)
	{
		if (isEmpty() || n <= 0)
			return self();
		else if (n >= size())
			return newBuilder().build();
		else
			return indices().withEnd(size() - n - 1).map(this::get, this::newBuilder);
	}
	
	/**
	 * @param f A search function
	 * @return A copy of this sequence without the last item(s) that satisfy the provided predicate
	 */
	public default Repr dropLastWhile(Predicate<? super A> f)
	{
		return lastIndexWhere(f.negate()).map(i -> first(i + 1)).getOrElse(() -> newBuilder().build());
	}
	
	/**
	 * @param n The number of elements to be included
	 * @return The last n elements of this sequence
	 */
	public default Repr last(int n)
	{
		if (n <= 0)
			return newBuilder().build();
		else if (n >= size())
			return self();
		else
			return indices().withStart(size() - n).map(this::get, this::newBuilder);
	}
	
	/**
	 * @return The first element in this sequence. None if the list is empty or the element is null.
	 */
	public default Option<A> first()
	{
		return headOption();
	}
	
	/**
	 * @return The last element in this sequence. None if the list is empty or the element is null.
	 */
	public default Option<A> last()
	{
		if (isEmpty())
			return Option.none();
		else
			return new Option<>(get(size() - 1));
	}
	
	/**
	 * @param index Index to drop from this sequence
	 * @return A copy of this sequence without the item at specified index
	 */
	public default Repr dropIndex(int index)
	{
		if (index < 0 || index >= size())
			return self();
		else
		{
			DefaultBuilder builder = newBuilder();
			builder.add(first(index));
			builder.add(dropFirst(index + 1));
			return builder.build();
		}
	}
	
	/**
	 * Maps this sequence using item indices in the mapping operation
	 * @param f The mapping function
	 * @param makeBuilder Function for making builder for the final collection
	 * @return A list with mapped values
	 */
	public default <B, R> R mapWithIndex(BiFunction<? super A, ? super Integer, ? extends B> f, 
			Supplier<? extends Builder<? extends R, ?, ? super B>> makeBuilder)
	{
		Builder<? extends R, ?, ? super B> builder = makeBuilder.get();
		indices().forEach(i -> builder.add(f.apply(get(i), i)));
		return builder.build();
	}
	
	/**
	 * Maps only a certain index. If index is out of bounds, returns this sequence instead.
	 * @param index The target index
	 * @param f A map function
	 * @return A copy of this list with one index mapped
	 */
	public default Repr mapIndex(int index, Function<? super A, ? extends A> f)
	{
		if (index < 0 || index >= size())
			return self();
		else
		{
			DefaultBuilder builder = newBuilder();
			builder.add(first(index - 1));
			builder.add(f.apply(get(index)));
			builder.add(dropFirst(index + 1));
			return builder.build();
		}
	}
	
	/**
	 * Performs an operation on each item in this sequence. The index of each item is also provided for the operation.
	 * @param f A function that will be performed for each item index pair
	 */
	public default void forEachWithIndex(BiConsumer<? super A, ? super Integer> f)
	{
		indices().forEach(i -> f.accept(get(i), i));
	}
}
