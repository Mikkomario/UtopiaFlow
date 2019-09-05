package utopia.flow.structure;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import utopia.flow.function.FunctionUtils;
import utopia.flow.function.ThrowingConsumer;
import utopia.flow.function.ThrowingFunction;
import utopia.flow.structure.iterator.RichIterator;
import utopia.flow.structure.iterator.SkipFirstIterator;

/**
 * Classes implementing this interface will have access to methods additional to normal iterables
 * @author Mikko Hilpinen
 * @since 14.5.2018
 * @param <T> The type of item in this iterable
 */
public interface RichIterable<T> extends Iterable<T>
{
	// ATTRIBUTES	------------------------
	
	/**
	 * A check for whether two objects are equal. Null safe
	 */
	public static final BiPredicate<Object, Object> SAFE_EQUALS = FunctionUtils.SAFE_EQUALS;
	
	
	// ABSTRACT METHODS	--------------------
	
	@Override
	public RichIterator<T> iterator();
	
	
	// STATIC	----------------------------
	
	/**
	 * Finds the maximum value from the provided items
	 * @param items The items the maximum value is searched from
	 * @return The maximum value from the list. None if list is empty.
	 */
	public static <T extends Comparable<? super T>> Option<T> maxFrom(RichIterable<T> items)
	{
		return items.max((a, b) -> a.compareTo(b));
	}
	
	/**
	 * Finds the minimum value from the provided items
	 * @param items The items the minimum value is searched from
	 * @return The minimum value from the list. None if list is empty.
	 */
	public static <T extends Comparable<? super T>> Option<T> minFrom(RichIterable<T> items)
	{
		return items.min((a, b) -> a.compareTo(b));
	}
	
	
	// OTHER METHODS	--------------------
	
	/**
	 * @return Whether this iterable contains any items
	 */
	public default boolean nonEmpty()
	{
		return iterator().hasNext();
	}
	
	/**
	 * @return Whether this iterable is empty (contains no items)
	 */
	public default boolean isEmpty()
	{
		return !nonEmpty();
	}
	
	/**
	 * @return The first element in this iterable
	 * @throws NoSuchElementException If the iterable is empty
	 * @see #headOption()
	 */
	public default T head() throws NoSuchElementException
	{
		RichIterator<T> iterator = iterator();
		if (iterator.hasNext())
			return iterator.next();
		else
			throw new NoSuchElementException("This iterable is empty");
	}
	
	/**
	 * @return The first element in this iterable. None if this iterable is empty.
	 */
	public default Option<T> headOption()
	{
		return iterator().nextOption();
	}
	
	/**
	 * The first n items in this iterable
	 * @param n The number of items included (at maximum)
	 * @param makeBuilder A function for producing builder for the final collection
	 * @return Up to the first n items from this iterable
	 */
	public default <R> R first(int n, Supplier<? extends Builder<? extends R, ?, ? super T>> makeBuilder)
	{
		return iterator().take(n, makeBuilder);
	}
	
	/**
	 * Takes elements as long as they satisfy a predicate
	 * @param f A function used for testing the items
	 * @param makeBuilder A function for producing builder for the final collection
	 * @return The first n items that satisfy the provided predicate
	 */
	public default <R> R takeWhile(Predicate<? super T> f, 
			Supplier<? extends Builder<? extends R, ?, ? super T>> makeBuilder)
	{
		return iterator().takeWhile(f, makeBuilder);
	}
	
	/**
	 * Drops items as long as the specified predicate is fulfilled and returns the rest of this collection
	 * @param f A predicate for dropping items from the beginning of this list
	 * @param makeBuilder A function for producing a builder for the final collection
	 * @return A collection without the first n items that satisfy the predicate
	 */
	public default <R> R dropWhile(Predicate<? super T> f, 
			Supplier<? extends Builder<? extends R, ?, ? super T>> makeBuilder)
	{
		Builder<? extends R, ?, ? super T> builder = makeBuilder.get();
		RichIterator<T> iter = iterator();
		
		// Skips items while predicate holds
		while (iter.pollOption().exists(f))
		{
			iter.next();
		}
		
		// Reads remaining items
		builder.read(iter);
		return builder.build();
	}
	
	/**
	 * Splits this collection in half at a specified index
	 * @param index the split index
	 * @param makeBuilder A function for producing builders for the final collections
	 * @return First the items before the split, then the rest of the items (including specified index)
	 */
	public default <R> Duo<R> splitAt(int index, 
			Supplier<? extends Builder<? extends R, ?, ? super T>> makeBuilder)
	{
		RichIterator<T> iterator = iterator();
		R firstPart = iterator.take(index, makeBuilder);
		Builder<? extends R, ?, ? super T> secondPartBuilder = makeBuilder.get();
		secondPartBuilder.read(iterator);
		
		return new Duo<>(firstPart, secondPartBuilder.build());
	}
	
	/**
	 * Splits this collection in half at the first item accepted by the predicate
	 * @param find A predicate for finding split index
	 * @param makeBuilder A function for producing builders for final collections
	 * @return First the items before the split, then the rest of the items (including search result)
	 */
	public default <R> Duo<R> splitAt(Predicate<? super T> find, 
			Supplier<? extends Builder<? extends R, ?, ? super T>> makeBuilder)
	{
		RichIterator<T> iterator = iterator();
		R firstPart = iterator.takeWhile(item -> !find.test(item), makeBuilder);
		Builder<? extends R, ?, ? super T> remainingBuilder = makeBuilder.get();
		remainingBuilder.read(iterator);
		
		return new Duo<>(firstPart, remainingBuilder.build());
	}
	
	/**
	 * Creates a view where the first n elements are skipped
	 * @param n The amount of elements to skip
	 * @return A view of the latter portion of this iterable
	 */
	public default View<T> dropFirstView(int n)
	{
		return new View<>(() -> new SkipFirstIterator<>(iterator(), n));
	}
	
	/**
	 * @return A view of all but the first item in this iterable
	 */
	public default View<T> tailView()
	{
		return dropFirstView(1);
	}
	
	/**
	 * Checks whether this list contains an element that satisfies the predicate
	 * @param f a predicate
	 * @return Is the predicate true for any of the elements in this list. False if empty.
	 */
	public default boolean exists(Predicate<? super T> f)
	{
		for (T element : this)
		{
			if (f.test(element))
				return true;
		}
		return false;
	}
	
	/**
	 * Checks whether this collection contains the provided item
	 * @param item An item
	 * @param equals A function for checking equality
	 * @return whether the item is contained within this collection
	 */
	public default <B> boolean contains(B item, BiPredicate<? super T, ? super B> equals)
	{
		return exists(o -> equals.test(o, item));
	}
	
	/**
	 * Checks whether this collection contains the provided item
	 * @param item An item
	 * @return whether the item is contained within this collection
	 */
	public default boolean contains(Object item)
	{
		return contains(item, SAFE_EQUALS);
	}
	
	/**
	 * Checks if a predicate is true for all elements in the list
	 * @param f a predicate
	 * @return Is the predicate true for all of the elements in this list. True if empty.
	 */
	public default boolean forAll(Predicate<? super T> f)
	{
		return !exists(f.negate());
	}
	
	/**
	 * Finds the first element that satisfies the predicate
	 * @param f A predicate
	 * @return The first element that satisfies the predicate
	 */
	public default Option<T> find(Predicate<? super T> f)
	{
		for (T element : this)
		{
			if (f.test(element))
				return Option.some(element);
		}
		return Option.none();
	}
	
	/**
	 * Counts the number of items that satisfy the provided predicate
	 * @param f A predicate
	 * @return The number of items that satisfy the provided predicate
	 */
	public default int count(Predicate<? super T> f)
	{
		int total = 0;
		for (T element : this)
		{
			if (f.test(element))
				total ++;
		}
		
		return total;
	}
	
	/**
	 * Divides this list into two categories
	 * @param f The filter function that is used for splitting this list
	 * @param makeBuilder Function for producing builders for final collections
	 * @return The filter results. One list for accepted values and one list for not accepted values
	 */
	public default <R> ImmutableMap<Boolean, R> divideBy(Predicate<? super T> f, 
			Supplier<? extends Builder<? extends R, ?, ? super T>> makeBuilder)
	{
		// Goes through this iterable, and groups the values
		Builder<? extends R, ?, ? super T> trues = makeBuilder.get();
		Builder<? extends R, ?, ? super T> falses = makeBuilder.get();
		
		forEach(item -> 
		{
			if (f.test(item))
				trues.add(item);
			else
				falses.add(item);
		});
		
		return ImmutableMap.build(result -> 
		{
			result.put(true, trues.build());
			result.put(false, falses.build());
		});
	}
	
	/**
	 * Maps the items in this collection and adds them to a new collection
	 * @param f A mapping function
	 * @param makeBuilder A function for producing a builder for the final collection
	 * @return Collection with mapped items
	 */
	public default <B, R> R map(Function<? super T, ? extends B> f, 
			Supplier<? extends Builder<? extends R, ?, ? super B>> makeBuilder)
	{
		Builder<? extends R, ?, ? super B> builder = makeBuilder.get();
		forEach(a -> builder.add(f.apply(a)));
		return builder.build();
	}
	
	/**
	 * Maps the items in this collection and adds them to a new collection
	 * @param f A mapping function that may return multiple items
	 * @param makeBuilder A function for producing a builder for the final collection
	 * @return Collection with mapped items
	 */
	public default <B, R> R flatMap(Function<? super T, ? extends Iterable<? extends B>> f, 
			Supplier<? extends Builder<? extends R, ?, ? super B>> makeBuilder)
	{
		Builder<? extends R, ?, ? super B> builder = makeBuilder.get();
		forEach(a -> builder.add(f.apply(a)));
		return builder.build();
	}
	
	/**
	 * Maps the items in this collection and adds them to a new collection. Throws on failure.
	 * @param f A mapping function
	 * @param makeBuilder A function for producing a builder for the final collection
	 * @return Collection with mapped items
	 * @throws E If mapping failed at any point
	 */
	public default <B, R, E extends Exception> R mapThrowing(ThrowingFunction<? super T, 
			? extends B, ? extends E> f, 
			Supplier<? extends Builder<? extends R, ?, ? super B>> makeBuilder) throws E
	{
		Builder<? extends R, ?, ? super B> builder = makeBuilder.get();
		forEachThrowing(a -> builder.add(f.throwingApply(a)));
		return builder.build();
	}
	
	/**
	 * Maps the items in this collection and adds them to a new collection. Throws on failure.
	 * @param f A mapping function that may return multiple items
	 * @param makeBuilder A function for producing a builder for the final collection
	 * @return Collection with mapped items
	 * @throws E If mapping failed at any point
	 */
	public default <B, R, E extends Exception> R flatMapThrowing(ThrowingFunction<? super T, 
			? extends Iterable<? extends B>, ? extends E> f, 
			Supplier<? extends Builder<? extends R, ?, ? super B>> makeBuilder) throws E
	{
		Builder<? extends R, ?, ? super B> builder = makeBuilder.get();
		forEachThrowing(a -> builder.add(f.throwingApply(a)));
		return builder.build();
	}
	
	/**
	 * Maps the items in this collection and adds them to a new collection. Returns a failure 
	 * if any mapping failed.
	 * @param f A function for mapping items. Returns success or failure.
	 * @param makeBuilder A function for producing a builder for the final collection
	 * @return Collection with mapped items or a failure
	 * @throws E 
	 */
	public default <B, R, E extends Exception> Try<R> tryMap(Function<? super T, 
			? extends Try<? extends B>> f, 
			Supplier<? extends Builder<? extends R, ?, ? super B>> makeBuilder) throws E
	{
		Builder<? extends R, ?, ? super B> builder = makeBuilder.get();
		for (T item : this)
		{
			Try<? extends B> mapResult = f.apply(item);
			if (mapResult.isSuccess())
				builder.add(mapResult.getSuccess());
			else
				return Try.failure(mapResult.getFailure());
		}
		return Try.success(builder.build());
	}
	
	/**
	 * Maps the items in this collection and adds them to a new collection. Returns a failure 
	 * if any mapping failed.
	 * @param f A function for mapping items. Returns success or failure.
	 * @param makeBuilder A function for producing a builder for the final collection
	 * @return Collection with mapped items or a failure
	 * @throws E 
	 */
	public default <B, R, E extends Exception> Try<R> tryFlatMap(Function<? super T, 
			? extends Try<? extends Iterable<? extends B>>> f, 
			Supplier<? extends Builder<? extends R, ?, ? super B>> makeBuilder) throws E
	{
		Builder<? extends R, ?, ? super B> builder = makeBuilder.get();
		for (T item : this)
		{
			Try<? extends Iterable<? extends B>> mapResult = f.apply(item);
			if (mapResult.isSuccess())
				builder.add(mapResult.getSuccess());
			else
				return Try.failure(mapResult.getFailure());
		}
		return Try.success(builder.build());
	}
	
	/**
	 * Maps the items in this collection and adds them to a new collection. Will silently catch 
	 * all exceptions during mapping
	 * @param f A function for mapping items. May throw.
	 * @param makeBuilder A function for producing a builder for the final collection
	 * @return Collection with successfully mapped items
	 */
	public default <B, R> R mapCatching(ThrowingFunction<? super T, ? extends B, ?> f, 
			Supplier<? extends Builder<? extends R, ?, ? super B>> makeBuilder)
	{
		Builder<? extends R, ?, ? super B> builder = makeBuilder.get();
		forEach(item -> f.apply(item).success().forEach(builder::add));
		return builder.build();
	}
	
	/**
	 * Maps the items in this collection and adds them to a new collection. Will silently catch 
	 * all exceptions during mapping
	 * @param f A function for mapping items. May throw.
	 * @param makeBuilder A function for producing a builder for the final collection
	 * @return Collection with successfully mapped items
	 */
	public default <B, R> R flatMapCatching(ThrowingFunction<? super T, 
			? extends Iterable<? extends B>, ?> f, 
			Supplier<? extends Builder<? extends R, ?, ? super B>> makeBuilder)
	{
		Builder<? extends R, ?, ? super B> builder = makeBuilder.get();
		forEach(item -> f.apply(item).success().forEach(builder::add));
		return builder.build();
	}
	
	/**
	 * Maps only certain items
	 * @param where A function for determining mapped items
	 * @param f A mapping function
	 * @param makeBuilder A function for producing final collection builder
	 * @return A mapped collection
	 */
	public default <R> R mapWhere(Predicate<? super T> where, Function<? super T, ? extends T> f, 
			Supplier<? extends Builder<? extends R, ?, ? super T>> makeBuilder)
	{
		Builder<? extends R, ?, ? super T> builder = makeBuilder.get();
		forEach(a -> 
		{
			if (where.test(a))
				builder.add(f.apply(a));
			else
				builder.add(a);
		});
		return builder.build();
	}
	
	/**
	 * Returns the first transformed item where the transformation is available. Similar to calling flatMap(f).first(), 
	 * except that this function doesn't transform unnecessary items.
	 * @param f The transformation function
	 * @return The first transformation result where the transformation is defined. None if none of this list's items 
	 * could be transformed.
	 */
	public default <B> Option<B> flatMapFirst(Function<? super T, Option<B>> f)
	{
		for (T item : this)
		{
			Option<B> result = f.apply(item);
			if (result.isDefined())
				return result;
		}
		
		return Option.none();
	}
	
	/**
	 * @return An immutable list based on the values of this iterable
	 */
	public default ImmutableList<T> toList()
	{
		return ImmutableList.build(buffer -> buffer.add(this));
	}
	
	/**
	 * @param mapper A mapper function that transforms the values
	 * @return An immutable list based on transformed values of this iterable
	 */
	public default <B> ImmutableList<B> mapToList(Function<? super T, ? extends B> mapper)
	{
		return map(mapper, ListBuilder::new);
		// return ImmutableList.build(buffer -> forEach(item -> buffer.add(mapper.apply(item))));
	}
	
	/**
	 * @param mapper A mapper function that transforms the values into zero or more items
	 * @return An immutable list with all transformed results in sequential order
	 */
	public default <B> ImmutableList<B> flatMapToList(Function<? super T, ? extends Iterable<B>> mapper)
	{
		return flatMap(mapper, ListBuilder::new);
		// return ImmutableList.build(buffer -> forEach(item -> buffer.add(mapper.apply(item))));
	}
	
	/**
	 * Merges this collection with another using a merge function. If the collections have different 
	 * sizes, only the beginning of one of the larger collection will be used
	 * @param other Another collection
	 * @param merge The merge function (left takes elements from this list, right takes elements from the other list and 
	 * the results are stored in the merged list)
	 * @param makeBuilder A function for producing builder for the final collection
	 * @return The merged collection
	 */
	public default <B, C, R> R mergedWith(Iterable<? extends B> other, 
			BiFunction<? super T, ? super B, ? extends C> merge, 
			Supplier<? extends Builder<? extends R, ?, ? super C>> makeBuilder)
	{
		Builder<? extends R, ?, ? super C> buffer = makeBuilder.get();
		RichIterator<T> myIter = iterator();
		Iterator<? extends B> otherIter = other.iterator();
		
		while (myIter.hasNext() && otherIter.hasNext())
		{
			buffer.add(merge.apply(myIter.next(), otherIter.next()));
		}
		
		return buffer.build();
	}
	
	/**
	 * Merges this collection with another. If the collections have different 
	 * sizes, only the beginning of one of the larger collection will be used
	 * @param other Another collection
	 * @param makeBuilder A function for producing builder for the final collection
	 * @return The merged collection which contains paired items
	 */
	public default <B, R> R zip(Iterable<? extends B> other, 
			Supplier<? extends Builder<? extends R, ?, ? super Pair<T, B>>> makeBuilder)
	{
		return mergedWith(other, (a, b) -> new Pair<>(a, b), makeBuilder);
	}
	
	/**
	 * Transforms this list into a map
	 * @param f A function that maps items to key value pairs
	 * @return A map based on this list's contents. If multiple items are mapped to the same key, only the last 
	 * item is included
	 */
	public default <Key, Value> ImmutableMap<Key, Value> toMap(Function<? super T, ? extends Pair<Key, Value>> f)
	{
		return map(f, MapBuilder::new);
		// return ImmutableMap.of(mapToList(f));
	}
	
	/**
	 * Creates a new map of this list by pairing items with values. The items in this list become keys in the new map
	 * @param pair A function that pairs items with values
	 * @return A new map based on the pairs
	 */
	public default <Value> ImmutableMap<T, Value> pairValues(Function<? super T, ? extends Value> pair)
	{
		return toMap(v -> new Pair<>(v, pair.apply(v)));
	}
	
	/**
	 * Creates a map based on the contents of this list and mapping results. Multiple values may be grouped 
	 * together under a single key
	 * @param f a function that maps the items in this list to key value pairs
	 * @return A map with values of mapped items
	 */
	public default <Key, Value> ImmutableMap<Key, ImmutableList<Value>> toListMap(Function<? super T, Pair<Key, Value>> f)
	{
		return ImmutableMap.<Key, ListBuilder<Value>>build(buffer -> 
		{
			forEach (item -> 
			{
				Pair<Key, Value> keyValue = f.apply(item);
				Key category = keyValue.getFirst();
				Value value = keyValue.getSecond();
				
				if (buffer.containsKey(category))
					buffer.get(category).add(value);
				else
					buffer.put(category, ListBuilder.withValue(value));
			});
			
		}).mapValues(b -> b.build());
	}
	
	/**
	 * Groups the contents of this list into subcategories based on mapping results
	 * @param f A mapping function
	 * @return The contents of this list grouped to categories based on the mapping function results
	 */
	public default <Key> ImmutableMap<Key, ImmutableList<T>> groupBy(Function<? super T, ? extends Key> f)
	{
		return toListMap(item -> new Pair<>(f.apply(item), item));
	}
	
	/**
	 * Performs a fold operation over this list, going from left to right
	 * @param start The starting value
	 * @param f A function that folds items into the result value
	 * @return The resulting value
	 */
	public default <B> B fold(B start, BiFunction<? super B, ? super T, ? extends B> f)
	{
		B result = start;
		for (T item : this)
		{
			result = f.apply(result, item);
		}
		
		return result;
	}
	
	/**
	 * Performs a reduce over the list from left to right
	 * @param f The reduce function
	 * @return The reduce result
	 * @throws NoSuchElementException If the list is empty
	 */
	public default T reduce(BiFunction<? super T, ? super T, ? extends T> f) throws NoSuchElementException
	{
		T result = null;
		for (T item : this)
		{
			if (result == null)
				result = item;
			else
				result = f.apply(result, item);
		}
		
		return result;
	}
	
	/**
	 * Performs a reduce over the list from left to right
	 * @param f The reduce function
	 * @return The reduce result. None if the list was empty
	 */
	public default Option<T> reduceOption(BiFunction<? super T, ? super T, ? extends T> f)
	{
		if (isEmpty())
			return Option.none();
		else
			return Option.some(reduce(f));
	}
	
	/**
	 * @param comparator A comparator
	 * @return The maximum value in this iterable, using the specified comparator
	 */
	public default Option<T> max(Comparator<? super T> comparator)
	{
		Option<T> first = headOption();
		if (first.isEmpty())
			return Option.none();
		else
		{
			T max = first.get();
			for (T item : tailView())
			{
				if (comparator.compare(item, max) > 0)
					max = item;
			}
			
			return Option.some(max);
		}
	}
	
	/**
	 * @param comparator A comparator
	 * @return The minimum value in this iterable, using the specified comparator
	 */
	public default Option<T> min(Comparator<? super T> comparator)
	{
		return max(comparator.reversed());
	}
	
	/**
	 * Finds the maximum value in this list based on a mapped value
	 * @param f A mapping function
	 * @return The maximum item in the list based on the mapped value. None if list is empty.
	 * @see #maxFrom(RichIterable)
	 */
	public default <K extends Comparable<? super K>> Option<T> maxBy(Function<? super T, ? extends K> f)
	{
		return max((a, b) -> f.apply(a).compareTo(f.apply(b)));
	}
	
	/**
	 * Finds the minimum value in this list based on a mapped value
	 * @param f A mapping function
	 * @return The minimum item in the list based on the mapped value. None if list is empty.
	 * @see #minFrom(RichIterable)
	 */
	public default <K extends Comparable<? super K>> Option<T> minBy(Function<? super T, ? extends K> f)
	{
		return min((a, b) -> f.apply(a).compareTo(f.apply(b)));
	}
	
	/**
	 * Maps the items and finds the smallest mapped value
	 * @param map A mapping function
	 * @param comparator A comparator for mapped values
	 * @return The smallest mapped value
	 */
	public default <B> Option<B> mapMin(Function<? super T, ? extends B> map, Comparator<? super B> comparator)
	{
		Option<T> first = headOption();
		if (first.isEmpty())
			return Option.none();
		else
		{
			B min = map.apply(first.get());
			for (T next : tailView())
			{
				B mapped = map.apply(next);
				if (comparator.compare(mapped, min) < 0)
					min = mapped;
			}
			
			return Option.some(min);
		}
	}
	
	/**
	 * Maps the items and finds the smallest mapped value
	 * @param map A mapping function
	 * @return The smallest mapped value
	 */
	public default <B extends Comparable<? super B>> Option<B> mapMin(Function<? super T, ? extends B> map)
	{
		return mapMin(map, (a, b) -> a.compareTo(b));
	}
	
	/**
	 * Maps the items and finds the largest mapped value
	 * @param map A mapping function
	 * @param comparator A comparator for mapped values
	 * @return The largest mapped value
	 */
	public default <B> Option<B> mapMax(Function<? super T, ? extends B> map, Comparator<? super B> comparator)
	{
		return mapMin(map, comparator.reversed());
	}
	
	/**
	 * Maps the items and finds the largest mapped value
	 * @param map A mapping function
	 * @return The largest mapped value
	 */
	public default <B extends Comparable<? super B>> Option<B> mapMax(Function<? super T, ? extends B> map)
	{
		return mapMax(map, (a, b) -> a.compareTo(b));
	}
	
	/**
	 * Performs an operation on two lists at the same time
	 * @param other Another list
	 * @param f A function that operates on two values at the same time. The left values come from this list. 
	 * The right values come from the other list.
	 */
	public default <U> void forEachSimultaneouslyWith(Iterable<? extends U> other, BiConsumer<? super T, ? super U> f)
	{
		RichIterator<T> myIter = iterator();
		Iterator<? extends U> otherIter = other.iterator();
		
		while (myIter.hasNext() && otherIter.hasNext())
		{
			f.accept(myIter.next(), otherIter.next());
		}
	}
	
	/**
	 * Performs a throwing operation on each of the elements in this collection. Stops iterating on the first exception.
	 * @param f The function that is performed for each element in the list
	 * @throws Exception The first exception thrown by the function
	 */
	public default <E extends Exception> void forEachThrowing(ThrowingConsumer<? super T, ? extends E> f) throws E
	{
		for (T item : this)
		{
			f.accept(item);
		}
	}
	
	/*
	public default <U, R extends Iterable<? extends U>> R map(
			Supplier<? extends Builder<? extends R, ?, ? super U>> newBuffer, Function<? super T, ? extends U> map)
	{
		Builder<? extends R, ?, ? super U> buffer = newBuffer.get();
		forEach(item -> buffer.add(map.apply(item)));
		
		return buffer.build();
	}*/
	
	/**
	 * @return A stream from this object's contents
	 */
	public default Stream<T> stream()
	{
		return StreamSupport.stream(spliterator(), false);
	}
	
	/**
	 * @return A view for this iterable
	 */
	public default View<T> view()
	{
		return new View<>(this::iterator);
	}
}
