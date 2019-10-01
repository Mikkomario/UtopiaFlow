package utopia.flow.structure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import utopia.flow.function.ThrowingFunction;
import utopia.flow.structure.iterator.RichIterator;
import utopia.flow.structure.iterator.StringCharIterator;
import utopia.flow.util.StringRepresentable;

/**
 * This list cannot be modified after creation and is safe to pass around as a value
 * @author Mikko Hilpinen
 * @param <T> The type of element stored within this list
 * @since 2.11.2017
 */
public class ImmutableList<T> implements RichIterable<T>, StringRepresentable, 
	AppendableSequence<T, ImmutableList<T>, ListBuilder<T>>
{
	// ATTRIBUTES	-------------------
	
	private final List<T> list;
	private final Lazy<Integer> size;
	
	
	// CONSTRUCTOR	-------------------
	
	/**
	 * Wraps a list into an immutable list.
	 * NB: The list must not be modified after this method is called
	 * @param list A list to be wrapped
	 */
	ImmutableList(List<T> list)
	{
		this.list = list;
		this.size = new Lazy<>(() -> this.list.size());
	}
	
	/**
	 * Creates a copy of another immutable list
	 * @param list another immutable list
	 */
	public ImmutableList(ImmutableList<? extends T> list)
	{
		this.list = new ArrayList<>(list.size());
		this.list.addAll(list.list);
		this.size = new Lazy<>(() -> this.list.size());
	}
	
	/**
	 * @return An empty list
	 */
	public static <T> ImmutableList<T> empty()
	{
		return new ImmutableList<>(new ArrayList<>());
	}
	
	/**
	 * Creates an immutable list with a single value
	 * @param element The element that is stored in this list
	 * @return A list with a single value
	 */
	public static <T> ImmutableList<T> withValue(T element)
	{
		List<T> list = new ArrayList<>(1);
		list.add(element);
		return new ImmutableList<>(list);
	}
	
	/**
	 * Creates an immutable list with the specified values
	 * @param first The first value
	 * @param second The second value
	 * @param more More values
	 * @return A list with multiple values
	 */
	@SafeVarargs
	public static <T> ImmutableList<T> withValues(T first, T second, T... more)
	{
		List<T> list = new ArrayList<>(more.length + 2);
		list.add(first);
		list.add(second);
		for (T element : more) { list.add(element); }
		return new ImmutableList<>(list);
	}
	
	/**
	 * Creates a new list with a single value
	 * @param first The first value
	 * @return A list with the specified value
	 * @deprecated Please use {@link #withValue(Object)} instead. This is just to provide support for old style 
	 * {@link #withValues(Object, Object, Object...)}
	 */
	public static <T> ImmutableList<T> withValues(T first)
	{
		return withValue(first);
	}
	
	/**
	 * Copies a collection into an immutable list
	 * @param collection a mutable collection
	 * @return an immutable copy of the specified collection
	 */
	public static <T> ImmutableList<T> of(Collection<? extends T> collection)
	{
		return new ImmutableList<>(new ArrayList<>(collection));
	}
	
	/**
	 * Copies an array into an immutable list
	 * @param array An array
	 * @return An immutable copy of the array
	 */
	public static <T> ImmutableList<T> of(T[] array)
	{
		List<T> list = new ArrayList<>(array.length);
		for (T item : array)
		{
			list.add(item);
		}
		
		return new ImmutableList<>(list);
	}
	
	/**
	 * @param s a string
	 * @return A list of all the characters in the string
	 */
	public static ImmutableList<Character> of(String s)
	{
		return readWith(new StringCharIterator(s));
	}
	
	/**
	 * Copies another immutable list (changing the typing). Please note that as long as the type remains the same, an 
	 * immutable list can freely be passed without copying.
	 * @param other Another immutable list
	 * @return A copy of the original list
	 */
	public static <T> ImmutableList<T> of(ImmutableList<? extends T> other)
	{
		List<T> list = new ArrayList<>(other.size());
		list.addAll(other.list);
		return new ImmutableList<>(list);
	}
	
	/**
	 * Flattens a list of lists
	 * @param list a list of lists
	 * @return a list containing all elements in the lists
	 */
	public static <T> ImmutableList<T> flatten(Iterable<? extends Iterable<? extends T>> list)
	{
		// int size = list.fold(0, (total, elem) -> total + elem.size());
		List<T> mutable = new ArrayList<>();
		list.forEach(elem -> elem.forEach(mutable::add));
		
		return new ImmutableList<>(mutable);
	}
	
	/**
	 * Creates a combination of multiple immutable lists
	 * @param first The first list
	 * @param more More lists
	 * @return A combination of the lists
	 */
	@SafeVarargs
	public static <T> ImmutableList<T> flatten(Iterable<? extends T> first, Iterable<? extends T>... more)
	{
		return flatten(ImmutableList.of(more).prepend(first));
	}
	
	/**
	 * Creates an immutable list that spans the provided range of integers
	 * @param minInclusive The smallest included number
	 * @param maxInclusive The largest included number
	 * @return A list that contains numbers in the provided range
	 */
	public static ImmutableList<Integer> range(int minInclusive, int maxInclusive)
	{
		if (maxInclusive < minInclusive)
			return empty();
		else
		{
			List<Integer> buffer = new ArrayList<>(maxInclusive - minInclusive + 1);
			for (int i = minInclusive; i <= maxInclusive; i++)
			{
				buffer.add(i);
			}
			return new ImmutableList<>(buffer);
		}
	}
	
	/**
	 * Reads the contents of an iterator into an immutable list
	 * @param iterator An iterator
	 * @return The items read from the iterator in list format
	 */
	public static <T> ImmutableList<T> readWith(Iterator<? extends T> iterator)
	{
		List<T> buffer = new ArrayList<>();
		while (iterator.hasNext())
		{
			buffer.add(iterator.next());
		}
		return new ImmutableList<>(buffer);
	}
	
	/**
	 * Fills a list with a value generator
	 * @param size The size of the list
	 * @param generator The generator that produces the items
	 * @return A list filled with items
	 */
	public static <T> ImmutableList<T> filledWith(int size, Supplier<? extends T> generator)
	{
		if (size <= 0)
			return empty();
		else
		{
			List<T> buffer = new ArrayList<>(size);
			for (int i = 0; i < size; i++)
			{
				buffer.add(generator.get());
			}
			return new ImmutableList<>(buffer);
		}
	}
	
	/**
	 * Fills a list with a single item repeated multiple times
	 * @param size The size of the list
	 * @param item The item that will fill the list
	 * @return A list filled with the item
	 */
	public static <T> ImmutableList<T> filledWith(int size, T item)
	{
		if (size <= 0)
			return empty();
		else
		{
			List<T> buffer = new ArrayList<>(size);
			for (int i = 0; i < size; i++)
			{
				buffer.add(item);
			}
			return new ImmutableList<>(buffer);
		}
	}
	
	/**
	 * @param capacity Capacity of builder
	 * @param fill A function for filling list contents
	 * @return A filled list
	 */
	public static <T> ImmutableList<T> build(Option<Integer> capacity, Consumer<? super ListBuilder<T>> fill)
	{
		ListBuilder<T> buffer = new ListBuilder<>(capacity);
		fill.accept(buffer);
		return buffer.result();
	}
	
	/**
	 * @param fill A function for filling list contents
	 * @return A filled list
	 */
	public static <T> ImmutableList<T> build(Consumer<? super ListBuilder<T>> fill)
	{
		return build(Option.none(), fill);
	}
	
	/**
	 * @param capacity Capacity of builder
	 * @param fill A function for filling list contents
	 * @return A filled list
	 */
	public static <T> ImmutableList<T> build(int capacity, Consumer<? super ListBuilder<T>> fill)
	{
		return build(Option.some(capacity), fill);
	}


	// IMPLEMENTED METHODS	-----------
	
	@Override
	public ImmutableList<T> self()
	{
		return this;
	}
	
	@Override
	public ListBuilder<T> newBuilder(Option<Integer> capacity)
	{
		return new ListBuilder<>(capacity);
	}
	
	@Override
	public int hashCode()
	{
		return this.list.hashCode();
	}


	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ImmutableList))
			return false;
		
		ImmutableList<?> other = (ImmutableList<?>) obj;
		if (this.list == null)
			return other.list == null;
		else if (size() != other.size())
			return false;
		else
		{
			for (int i = 0; i < size(); i++)
			{
				if (!SAFE_EQUALS.test(get(i), other.get(i)))
					return false;
			}
			
			return true;
		}
	}
	
	@Override
	public String toString()
	{
		StringBuilder s = new StringBuilder("[");
		appendAsString(", ", s);
		s.append("]");
		
		return s.toString();
	}

	@Override
	public RichIterator<T> iterator()
	{
		return RichIterator.wrap(this.list.iterator());
	}
	
	@Override
	public Stream<T> stream()
	{
		return this.list.stream();
	}
	
	@Override
	public int size()
	{
		return this.size.get();
	}
	
	@Override
	public T get(int index) throws IndexOutOfBoundsException
	{
		return this.list.get(index);
	}
	
	
	// OTHER METHODS	--------------
	
	/**
	 * @return A mutable copy of this list
	 */
	public ArrayList<T> toMutableList()
	{
		return new ArrayList<>(this.list);
	}
	
	/**
	 * Checks whether this list contains all of the elements from the specified collection
	 * @param elements A collection of elements
	 * @return Whether this list contains all elements from the specified collection
	 */
	public boolean containsAll(Collection<?> elements)
	{
		return this.list.containsAll(elements);
	}
	
	/**
	 * Creates a new list with the element appended. If the element already exists on the list, returns self
	 * @param element The element that will be included in the list
	 * @return If this list contains the element, return this list. Otherwise, creates a new list with the element added
	 * @deprecated Please use {@link #with(Object)} instead
	 */
	public ImmutableList<T> withElement(T element)
	{
		if (contains(element))
			return this;
		else
			return plus(element);
	}
	
	/**
	 * Merges this list with another list using a merge function. If the lists have different sizes, only the 
	 * beginning of one of the list will be used
	 * @param other Another list
	 * @param merge The merge function (left takes elements from this list, right takes elements from the other list and 
	 * the results are stored in the merged list)
	 * @return The merged list
	 */
	public <U, R> ImmutableList<R> mergedWith(RichIterable<? extends U> other, 
			BiFunction<? super T, ? super U, ? extends R> merge)
	{
		return mergedWith(other, merge, ListBuilder::new);
	}
	
	/**
	 * Merges this list with another by pairing the values. If sizes differ, the size of the shorter 
	 * collection is used.
	 * @param other Another collection
	 * @return A list with paired items
	 */
	public <B> ImmutableList<Pair<T, B>> zip(RichIterable<? extends B> other)
	{
		return zip(other, ListBuilder::new);
	}
	
	/**
	 * Merges this list with another list, creating a list of pairs. If the lists have different sizes, only the 
	 * beginning of one of the list will be used
	 * @param other Another list
	 * @return The merged list consisting of value pairs (left side value from this list, right side value from the 
	 * other list)
	 * @deprecated Please use {@link #zip(RichIterable)} instead
	 */
	public <U> ImmutableList<Pair<T, U>> mergedWith(RichIterable<? extends U> other)
	{
		return mergedWith(other, (a, b) -> new Pair<>(a, b));
	}
	
	/**
	 * Creates a sorted copy of this list
	 * @param c a comparator that sorts the list
	 * @return A sorted copy of this list
	 */
	public ImmutableList<T> sortedWith(Comparator<? super T> c)
	{
		ArrayList<T> mutable = toMutableList();
		mutable.sort(c);
		return new ImmutableList<>(mutable);
	}
	
	/**
	 * Sorts the lists by transforming the items to a comparable form
	 * @param f The transform function
	 * @return The sorted list
	 */
	public <K extends Comparable<? super K>> ImmutableList<T> sortedBy(Function<? super T, ? extends K> f)
	{	
		return sortedWith((a, b) -> f.apply(a).compareTo(f.apply(b)));
	}
	
	/**
	 * Sorts the list by using possibly multiple sort functions. Uses the more important comparators first and 
	 * tweaks the sorting using the less important comparators.
	 * @param comparators The comparators that are available. Ordered from the most important to the least important comparator.
	 * @return The list sorted with the comparators
	 */
	public ImmutableList<T> sortedWith(ImmutableList<? extends Comparator<? super T>> comparators)
	{
		Comparator<T> comparator = new Comparator<T>()
		{
			@Override
			public int compare(T a, T b)
			{
				Option<Integer> result = comparators.flatMapFirst(c -> Option.some(c.compare(a, b)).filter(i -> i != 0));
				return result.getOrElse(0);
			}
		};
		
		return sortedWith(comparator);
	}
	
	/**
	 * @return Sorts the elements based on their natural ordering. Returns the sorted version of the list
	 */
	public ImmutableList<T> sorted()
	{
		return sortedWith((Comparator<T>) null);
	}
	
	/**
	 * Maps this list
	 * @param f a mapping function
	 * @return The mapped list
	 */
	public <B> ImmutableList<B> map(Function<? super T, ? extends B> f)
	{
		// return new ImmutableList<>(stream().map(f).collect(Collectors.toList()));
		return mapToList(f);
	}
	
	/**
	 * Maps values to another type. May fail.
	 * @param f A function that maps a value, but may also fail.
	 * @return Mapped values if all conversions succeeded, failure otherwise.
	 */
	public <B> Try<ImmutableList<B>> tryMap(Function<? super T, ? extends Try<B>> f)
	{
		return tryMap(f, ListBuilder::new);
	}
	
	/**
	 * Maps items but throws error if mapping fails
	 * @param f A function for mapping items
	 * @return Mapped items
	 * @throws E An error when a mapping fails
	 */
	public <B, E extends Exception> ImmutableList<B> mapThrowing(ThrowingFunction<? super T, 
			? extends B, ? extends E> f) throws E
	{
		return mapThrowing(f, ListBuilder::new);
	}
	
	/**
	 * Maps this list, using item indices in the mapping operation
	 * @param f The mapping function
	 * @return A list with mapped values
	 */
	public <B> ImmutableList<B> mapWithIndex(BiFunction<? super T, ? super Integer, ? extends B> f)
	{
		return mapWithIndex(f, ListBuilder::new);
	}
	
	/**
	 * Maps only certain items
	 * @param where A function for determining mapped items
	 * @param map A mapping function
	 * @return A mapped list
	 */
	public ImmutableList<T> mapWhere(Predicate<? super T> where, Function<? super T, ? extends T> map)
	{
		return mapWhere(where, map, ListBuilder::new);
	}
	
	/**
	 * Flatmaps the list
	 * @param f a mapping function
	 * @return The mapped list
	 */
	public <B> ImmutableList<B> flatMap(Function<? super T, ? extends RichIterable<? extends B>> f)
	{
		return flatMap(f, ListBuilder::new);
		// return new ImmutableList<>(stream().flatMap(i -> f.apply(i).stream()).collect(Collectors.toList()));
	}
	
	/**
	 * Maps items but may fail
	 * @param f A function for mapping an item to possibly multiple values. May fail.
	 * @return Mapped items or a failure if any mapping failed.
	 */
	public <B> Try<ImmutableList<B>> tryFlatMap(
			Function<? super T, ? extends Try<? extends RichIterable<? extends B>>> f)
	{
		return tryFlatMap(f, ListBuilder::new);
	}
	
	/**
	 * Maps items in this list to multiple items. May throw
	 * @param f A mapping function that may throw and returns possibly multiple items
	 * @return Mapped results
	 * @throws E If any mapping failed
	 */
	public <B, E extends Exception> ImmutableList<B> flatMapThrowing(
			ThrowingFunction<? super T, ? extends RichIterable<? extends B>, ? extends E> f) throws E
	{
		return flatMapThrowing(f, ListBuilder::new);
	}
}
