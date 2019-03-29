package utopia.flow.structure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import utopia.flow.function.ThrowingPredicate;
import utopia.flow.structure.iterator.RichIterator;
import utopia.flow.structure.iterator.StringCharIterator;
import utopia.flow.util.StringRepresentable;

/**
 * This list cannot be modified after creation and is safe to pass around as a value
 * @author Mikko Hilpinen
 * @param <T> The type of element stored within this list
 * @since 2.11.2017
 */
public class ImmutableList<T> implements RichIterable<T>, StringRepresentable
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
	 * @param fill A function for filling list contents
	 * @return A filled list
	 */
	public static <T> ImmutableList<T> build(Consumer<? super ListBuilder<T>> fill)
	{
		ListBuilder<T> buffer = new ListBuilder<>();
		fill.accept(buffer);
		return buffer.build();
	}


	// IMPLEMENTED METHODS	-----------
	
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
		
		if (!isEmpty())
		{
			s.append(head());
			tail().forEach(item -> s.append(", " + item));
		}
		
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
	
	
	// OTHER METHODS	--------------
	
	/**
	 * @return A mutable copy of this list
	 */
	public ArrayList<T> toMutableList()
	{
		return new ArrayList<>(this.list);
	}
	
	private ArrayList<T> toMutableList(int extraCapacity)
	{
		ArrayList<T> mutable = new ArrayList<>(size() + extraCapacity);
		mutable.addAll(this.list);
		return mutable;
	}
	
	/**
	 * @return The size of this list
	 */
	public int size()
	{
		return this.size.get();
	}
	
	/**
	 * @return The range of the indices of this list
	 */
	public IntRange getRange()
	{
		return Range.fromUntil(0, size());
	}
	
	/**
	 * Retrieves a specific index from the list
	 * @param index an index from the list
	 * @return The element from the specified index
	 */
	public T get(int index)
	{
		return this.list.get(index);
	}
	
	/**
	 * Finds the item at specified index or none if this list doesn't have such an index
	 * @param index The target index
	 * @return An item from the specified index
	 */
	public Option<T> getOption(int index)
	{
		if (index >= 0 && index < size())
			return Option.some(get(index));
		else
			return Option.none();
	}
	
	/**
	 * Retrieves a range of items from this list
	 * @param from The minimum index (inclusive)
	 * @param to The maximum index (inclusive)
	 * @return Items within the specified range. The size of the list may be smaller than the 
	 * length of the range if the range was (partially) outside this list's range
	 */
	public ImmutableList<T> getInRange(int from, int to)
	{
		int realStart = Math.max(from, 0);
		int realEnd = Math.min(to, size() - 1);
		
		if (realEnd <= realStart)
			return ImmutableList.empty();
		else
		{
			ArrayList<T> buffer = new ArrayList<>(realEnd - realStart);
			for (int i = realStart; i <= realEnd; i ++)
			{
				buffer.add(get(i));
			}
			return new ImmutableList<>(buffer);
		}
	}
	
	/**
	 * Finds all items within a specified range
	 * @param range a range
	 * @return Items within the specified range. The size of the list may be smaller than the 
	 * length of the range if the range was (partially) outside this list's range
	 */
	public ImmutableList<T> get(Range<? extends Integer> range)
	{
		return getInRange(range.getStart(), range.getEnd());
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
	 * Checks whether this list contains all of the elements from the specified collection
	 * @param elements A collection of elements
	 * @return Whether this list contains all elements from the specified collection
	 */
	public boolean containsAll(ImmutableList<?> elements)
	{
		return containsAll(elements.list);
	}
	
	/**
	 * Checks whether this list contains all and only the elements from the other list. The order of the items 
	 * isn't checked in this operation.
	 * @param other Another list
	 * @return Whether this list contains the same elements as the other list, possibly in different order
	 * @see #equals(Object)
	 */
	public boolean containsExactly(ImmutableList<?> other)
	{
		return size() == other.size() && containsAll(other);
	}
	
	/**
	 * Checks whether the contents of this list can be considered equal with another list's contents
	 * @param other Another list
	 * @param equalityCheck A function used for checking equality between items
	 * @return Whether the contents of this list are considered equal with the other list's contents 
	 * (using specified function)
	 */
	public <B> boolean equals(ImmutableList<? extends B> other, BiPredicate<? super T, ? super B> equalityCheck)
	{
		if (size() != other.size())
			return false;
		else
		{
			for (int i = 0; i < size(); i++)
			{
				if (!equalityCheck.test(get(i), other.get(i)))
					return false;
			}
			
			return true;
		}
	}
	
	/**
	 * Creates a new list with the element appended
	 * @param element an element
	 * @return a list with the element appended
	 */
	public ImmutableList<T> plus(T element)
	{
		ArrayList<T> mutable = toMutableList(1);
		mutable.add(element);
		return new ImmutableList<>(mutable);
	}
	
	/**
	 * Creates a new list with the element appended
	 * @param element An element or none
	 * @return a list with the element appended. This list if there was no element to append.
	 */
	public ImmutableList<T> plus(Option<? extends T> element)
	{
		if (element.isDefined())
			return plus(element.get());
		else
			return this;
	}
	
	/**
	 * Creates a new list with the specified element added to a certain index
	 * @param element The element that is added
	 * @param index The index the element is added to
	 * @return A list with the element added
	 */
	public ImmutableList<T> plus(T element, int index)
	{
		if (index <= 0)
			return prepend(element);
		else if (index >= size() - 1)
			return plus(element);
		else
		{
			List<T> mutable = toMutableList();
			mutable.add(index, element);
			return new ImmutableList<>(mutable);
		}
	}
	
	/**
	 * Creates a new list with the elements appended
	 * @param elements multiple elements
	 * @return a list with the elements appended
	 */
	public ImmutableList<T> plus(Iterable<? extends T> elements)
	{	
		ArrayList<T> mutable = toMutableList();
		elements.forEach(mutable::add);
		
		if (mutable.size() == size())
			return this;
		else
			return new ImmutableList<>(mutable);
	}
	
	/**
	 * Creates a new list with the elements appended
	 * @param elements multiple elements
	 * @return a list with the elements appended
	 */
	public ImmutableList<T> plus(ImmutableList<? extends T> elements)
	{
		if (elements.isEmpty())
			return this;
		else
		{
			ArrayList<T> mutable = toMutableList(elements.size());
			mutable.addAll(elements.list);
			return new ImmutableList<>(mutable);
		}		
	}
	
	/**
	 * Creates a new list with the elements appended
	 * @param elements an array of elements
	 * @return a list with the elements appended
	 */
	public ImmutableList<T> plus(T[] elements)
	{
		return plus(ImmutableList.of(elements));
	}
	
	/**
	 * Adds multiple elements to this list but keeps it distinct. Doesn't add already existing elements.
	 * @param elements The new elements
	 * @param equals method for checking equality
	 * @return A combined list
	 */
	public ImmutableList<T> plusDistinct(RichIterable<? extends T> elements, BiPredicate<? super T, ? super T> equals)
	{
		return plus(elements.stream().filter(newElem -> !exists(oldElem -> equals.test(oldElem, newElem))).collect(Collectors.toList()));
	}
	
	/**
	 * Adds multiple elements to this list but keeps it distinct. Doesn't add already existing elements.
	 * @param elements The new elements
	 * @return A combined list
	 */
	public ImmutableList<T> plusDistinct(RichIterable<? extends T> elements)
	{
		return plusDistinct(elements, SAFE_EQUALS);
	}
	
	/**
	 * Adds an element to this list but keeps it distinct. Doesn't add already existing elements.
	 * @param element The new element
	 * @param equals method for checking equality
	 * @return A resulting list
	 */
	public ImmutableList<T> plusDistinct(T element, BiPredicate<? super T, ? super T> equals)
	{
		if (exists(oldElem -> equals.test(oldElem, element)))
			return this;
		else
			return plus(element);
	}
	
	/**
	 * Adds an element to this list but keeps it distinct. Doesn't add already existing elements.
	 * @param element The new element
	 * @return A resulting list
	 */
	public ImmutableList<T> plusDistinct(T element)
	{
		return plusDistinct(element, SAFE_EQUALS);
	}
	
	/**
	 * Adds multiple elements to this list but keeps it distinct. Overwrites old elements with new versions.
	 * @param elements The new elements
	 * @param equals method for checking equality
	 * @return A combined list
	 */
	public ImmutableList<T> overwrite(RichIterable<? extends T> elements, BiPredicate<? super T, ? super T> equals)
	{
		return filter(oldElem -> !elements.exists(newElem -> equals.test(oldElem, newElem))).plus(elements);
	}
	
	/**
	 * Adds multiple elements to this list but keeps it distinct. Overwrites old elements with new versions.
	 * @param elements The new elements
	 * @return A combined list
	 */
	public ImmutableList<T> overwrite(RichIterable<? extends T> elements)
	{
		return overwrite(elements, SAFE_EQUALS);
	}
	
	/**
	 * Adds an element to this list but keeps it distinct. Possibly overwrites old element with a new version.
	 * @param element The new element
	 * @param equals method for checking equality
	 * @return A resulting list
	 */
	public ImmutableList<T> overwrite(T element, BiPredicate<? super T, ? super T> equals)
	{
		return filter(oldElem -> !equals.test(oldElem, element)).plus(element);
	}
	
	/**
	 * Adds an element to this list but keeps it distinct. Possibly overwrites old element with a new version.
	 * @param element The new element
	 * @return A resulting list
	 */
	public ImmutableList<T> overwrite(T element)
	{
		return overwrite(element, SAFE_EQUALS);
	}
	
	/**
	 * Overwrites an element at a specified index
	 * @param element The new element
	 * @param index The index that is replaced
	 * @return The new version of the list
	 */
	public ImmutableList<T> overwrite(T element, int index)
	{
		if (index < 0)
			return prepend(element);
		else if (index >= size())
			return plus(element);
		else
		{
			List<T> mutable = toMutableList();
			mutable.set(index, element);
			return new ImmutableList<>(mutable);
		}
	}
	
	/**
	 * Creates a new list with the element appended. If the element already exists on the list, returns self
	 * @param element The element that will be included in the list
	 * @return If this list contains the element, return this list. Otherwise, creates a new list with the element added
	 */
	public ImmutableList<T> withElement(T element)
	{
		if (contains(element))
			return this;
		else
			return plus(element);
	}
	
	/**
	 * Creates a new list with the element prepended (to the beginning of the list)
	 * @param element an element
	 * @return a list with the element prepended
	 */
	public ImmutableList<T> prepend(T element)
	{
		ArrayList<T> mutable = new ArrayList<>(size() + 1);
		mutable.add(element);
		mutable.addAll(this.list);
		return new ImmutableList<>(mutable);
	}
	
	/**
	 * Creates a new list with the element prepended (to the beginning of the list).
	 * @param element an element
	 * @return a list with the element prepended. If an empty element was provided, this list is returned
	 */
	public ImmutableList<T> prepend(Option<? extends T> element)
	{
		return element.map(e -> prepend(e)).getOrElse(this);
	}
	
	/**
	 * Merges this list with another list using a merge function. If the lists have different sizes, only the 
	 * beginning of one of the list will be used
	 * @param other Another list
	 * @param merge The merge function (left takes elements from this list, right takes elements from the other list and 
	 * the results are stored in the merged list)
	 * @return The merged list
	 */
	public <U, R> ImmutableList<R> mergedWith(Iterable<? extends U> other, 
			BiFunction<? super T, ? super U, ? extends R> merge)
	{
		List<R> buffer = new ArrayList<>(size());
		RichIterator<T> myIter = iterator();
		Iterator<? extends U> otherIter = other.iterator();
		
		while (myIter.hasNext() && otherIter.hasNext())
		{
			buffer.add(merge.apply(myIter.next(), otherIter.next()));
		}
		
		return new ImmutableList<>(buffer);
	}
	
	/**
	 * Merges this list with another list, creating a list of pairs. If the lists have different sizes, only the 
	 * beginning of one of the list will be used
	 * @param other Another list
	 * @return The merged list consisting of value pairs (left side value from this list, right side value from the 
	 * other list)
	 */
	public <U> ImmutableList<Pair<T, U>> mergedWith(Iterable<? extends U> other)
	{
		return mergedWith(other, (a, b) -> new Pair<>(a, b));
	}
	
	/**
	 * Creates a new list without the specified element
	 * @param element an element to be removed from the list
	 * @return a copy of this list without the specified element. This list if it didn't contain the element
	 */
	public ImmutableList<T> minus(Object element)
	{
		ArrayList<T> mutable = toMutableList();
		if (mutable.remove(element))
			return new ImmutableList<>(mutable);
		else
			return this;
	}
	
	/**
	 * Creates a new list without the specified elements
	 * @param elements the elements to be removed from the list
	 * @return a copy of this list without the specified elements. This list if it didn't contain any of the elements
	 */
	public ImmutableList<T> minus(Collection<? extends Object> elements)
	{
		ArrayList<T> mutable = toMutableList();
		if (mutable.removeAll(elements))
			return new ImmutableList<>(mutable);
		else
			return this;
	}
	
	/**
	 * Creates a new list without the specified elements
	 * @param elements the elements to be removed from the list
	 * @return a copy of this list without the specified elements. This list if it didn't contain any of the elements
	 */
	public ImmutableList<T> minus(ImmutableList<? extends Object> elements)
	{
		return minus(elements.list);
	}
	
	/**
	 * Finds the index of a specified element in this list
	 * @param element an element
	 * @return The index of the element in this list or None if no such element exists
	 */
	public Option<Integer> indexOf(T element)
	{
		return Option.positiveInt(this.list.indexOf(element), true);
	}
	
	/**
	 * Finds the first index where the predicate is true
	 * @param f a predicate for finding the index
	 * @return The first index where the predicate is true or None if no such index exists
	 */
	public Option<Integer> indexWhere(Predicate<? super T> f)
	{
		for (int i = 0; i < size(); i++)
		{
			if (f.test(get(i)))
				return Option.some(i);
		}
		return Option.none();
	}
	
	/**
	 * Finds the first index where the predicate is true. May throw
	 * @param f The predicate for finding the index. May throw
	 * @return The first index where the predicate is true. None if no such index exists
	 * @throws E If the predicate failed at any point
	 */
	public <E extends Exception> Option<Integer> indexWhereThrowing(ThrowingPredicate<? super T, E> f) throws E
	{
		for (int i = 0; i < size(); i++)
		{
			if (f.test(get(i)))
				return Option.some(i);
		}
		
		return Option.none();
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
	 * @return A reversed version of this list
	 */
	public ImmutableList<T> reversed()
	{
		ArrayList<T> copy = new ArrayList<>(size());
		for (int i = size() - 1; i >= 0; i--)
		{
			copy.add(get(i));
		}
		
		return new ImmutableList<>(copy);
	}
	
	/**
	 * @param n A number of elements to be dropped
	 * @return A copy of this list without the first n elements
	 */
	public ImmutableList<T> dropFirst(int n)
	{
		if (isEmpty())
			return this;
		else if (n <= 0)
			return this;
		else
		{
			ArrayList<T> result = new ArrayList<>(Math.max(size() - n, 0));
			for (int i = n; i < size(); i++) { result.add(get(i)); }
			return new ImmutableList<>(result);
		}
	}
	
	/**
	 * @return A copy of this list without the first element
	 */
	public ImmutableList<T> tail()
	{
		return dropFirst(1);
	}
	
	/**
	 * @param n The number of elements to be dropped
	 * @return A copy of this list without the last n elements
	 */
	public ImmutableList<T> dropLast(int n)
	{
		if (isEmpty())
			return this;
		else if (n <= 0)
			return this;
		else
		{
			ArrayList<T> result = new ArrayList<>(Math.max(size() - n, 0));
			for (int i = 0; i < size() - n; i++) { result.add(get(i)); }
			return new ImmutableList<>(result);
		}
	}
	
	/**
	 * Drops items as long as the specified predicate is fulfilled and returns the rest of this list
	 * @param f A predicate for dropping items from the beginning of this list
	 * @return A list without the first n items that satisfy the predicate
	 */
	public ImmutableList<T> dropWhile(Predicate<? super T> f)
	{
		Predicate<? super T> negated = f.negate();
		
		if (isEmpty())
			return this;
		else
		{
			Option<Integer> firstIncludedIndex = indexWhere(negated);
			if (firstIncludedIndex.isDefined())
			{
				if (firstIncludedIndex.get().intValue() == 0)
					return this;
				else
					return dropFirst(firstIncludedIndex.get());
			}
			else
				return empty();
		}
	}
	
	/**
	 * @param n The number of elements to be included
	 * @return The last n elements of this list
	 */
	public ImmutableList<T> last(int n)
	{
		if (n <= 0)
			return empty();
		else if (n >= size())
			return this;
		else
		{
			ArrayList<T> result = new ArrayList<>(n);
			for (int i = size() - n; i < size(); i++) { result.add(get(i)); }
			return new ImmutableList<>(result);
		}
	}
	
	/**
	 * @return The first element in this list. None if the list is empty or the element is null.
	 */
	public Option<T> first()
	{
		return headOption();
	}
	
	/**
	 * @return The last element in this list. None if the list is empty or the element is null.
	 */
	public Option<T> last()
	{
		if (isEmpty())
			return Option.none();
		else
			return new Option<>(get(size() - 1));
	}
	
	/**
	 * Creates a filtered copy of this list
	 * @param f a filter function
	 * @return a copy of this list with only elements accepted by the filter
	 */
	public ImmutableList<T> filter(Predicate<? super T> f)
	{
		return new ImmutableList<>(stream().filter(f).collect(Collectors.toList()));
	}
	
	/**
	 * Filters this list preferring items selected by the provided predicate. However, if there are no such items, 
	 * returns this list instead
	 * @param f A filter / preference predicate
	 * @return A non-empty list of preferred items or this list
	 */
	public ImmutableList<T> prefer(Predicate<? super T> f)
	{
		if (isEmpty())
			return this;
		else
		{
			ImmutableList<T> filtered = filter(f);
			if (filtered.isEmpty())
				return this;
			else
				return filtered;
		}
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
	 * Maps this list, using item indices in the mapping operation
	 * @param f The mapping function
	 * @return A list with mapped values
	 */
	public <B> ImmutableList<B> mapWithIndex(BiFunction<? super T, ? super Integer, ? extends B> f)
	{
		ArrayList<B> buffer = new ArrayList<>(size());
		for (int i = 0; i < size(); i++)
		{
			buffer.add(f.apply(get(i), i));
		}
		return new ImmutableList<>(buffer);
	}
	
	/**
	 * Maps only a certain index. If index is out of bounds, returns this list instead.
	 * @param index The target index
	 * @param f A map function
	 * @return A copy of this list with one index mapped
	 */
	public ImmutableList<T> mapIndex(int index, Function<? super T, ? extends T> f)
	{
		if (index < 0 || index >= size())
			return this;
		else
		{
			ArrayList<T> buffer = new ArrayList<>(size());
			for (int i = 0; i < index; i++) { buffer.add(get(i)); }
			buffer.add(f.apply(get(index)));
			for (int i = index + 1; i < size(); i++) { buffer.add(get(i)); }
			
			return new ImmutableList<>(buffer);
		}
	}
	
	/**
	 * Maps only certain items
	 * @param where A function for determining mapped items
	 * @param map A mapping function
	 * @return A mapped list
	 */
	public ImmutableList<T> mapWhere(Predicate<? super T> where, Function<? super T, ? extends T> map)
	{
		ArrayList<T> buffer = new ArrayList<>(size());
		for (T item : this)
		{
			if (where.test(item))
				buffer.add(map.apply(item));
			else
				buffer.add(item);
		}
		
		return new ImmutableList<>(buffer);
	}
	
	/**
	 * Flatmaps the list
	 * @param f a mapping function
	 * @return The mapped list
	 */
	public <B> ImmutableList<B> flatMap(Function<? super T, ? extends RichIterable<? extends B>> f)
	{
		return new ImmutableList<>(stream().flatMap(i -> f.apply(i).stream()).collect(Collectors.toList()));
	}
	
	/**
	 * Filters the list so that it contains only unique elements. When filtering out elements, 
	 * the leftmost unique item is preserved. For example, when using distinct on [1, 2, 3, 4, 4, 3, 1], the 
	 * resulting list is [1, 2, 3, 4]
	 * @param equals A function that is used for checking equality between items
	 * @return A list containing only a single instance of each unique item from this list.
	 */
	public ImmutableList<T> distinct(BiPredicate<? super T, ? super T> equals)
	{
		List<T> distinctValues = new ArrayList<>();
		for (T item : this)
		{
			boolean isUnique = true;
			for (T existing : distinctValues)
			{
				if (equals.test(item, existing))
				{
					isUnique = false;
					break;
				}
			}
			
			if (isUnique)
				distinctValues.add(item);
		}
		
		return new ImmutableList<>(distinctValues);
	}
	
	/**
	 * Performs an operation on each item in this list. The index of each item is also provided for the operation.
	 * @param f A function that will be performed for each item index pair
	 */
	public void forEachWithIndex(BiConsumer<? super T, ? super Integer> f)
	{
		for (int i = 0; i < size(); i++)
		{
			f.accept(get(i), i);
		}
	}
	
	/**
	 * Filters the list so that it contains only unique elements. When filtering out elements, 
	 * the leftmost unique item is preserved. For example, when using distinct on [1, 2, 3, 4, 4, 3, 1], the 
	 * resulting list is [1, 2, 3, 4]
	 * @return Returns a distict (all values are unique) version of this list. Basic equals method is used for 
	 * checking equality between elements
	 */
	public ImmutableList<T> distinct()
	{
		return distinct(SAFE_EQUALS);
	}
	
	/**
	 * Finds the maximum value from the provided items
	 * @param items The items the maximum value is searched from
	 * @param comparator The comparator used for comparing the values
	 * @return The maximum value from the list. None if list is empty.
	 * @deprecated Please use {@link #max(Comparator)} instead
	 */
	public static <T> Option<T> maxFrom(RichIterable<? extends T> items, Comparator<? super T> comparator)
	{
		if (items.isEmpty())
			return Option.none();
		
		T top = items.head();
		for (T item : items.tailView())
		{
			if (comparator.compare(item, top) > 0)
				top = item;
		}
		
		return Option.some(top);
	}
	
	/**
	 * Finds the maximum value from the provided items
	 * @param items The items the maximum value is searched from
	 * @return The maximum value from the list. None if list is empty.
	 * @deprecated Please use {@link RichIterable#maxFrom(RichIterable)} instead
	 */
	public static <T extends Comparable<? super T>> Option<T> maxFrom(RichIterable<? extends T> items)
	{
		return maxFrom(items, (a, b) -> a.compareTo(b));
	}
	
	/**
	 * Finds the minimum value from the provided items
	 * @param items The items the minimum value is searched from
	 * @param comparator The comparator used for comparing the values
	 * @return The minimum value from the list. None if list is empty.
	 * @deprecated Please use {@link #min(Comparator)} instead
	 */
	public static <T> Option<T> minFrom(RichIterable<? extends T> items, Comparator<? super T> comparator)
	{
		if (items.isEmpty())
			return Option.none();
		
		T top = items.head();
		for (T item : items.tailView())
		{
			if (comparator.compare(item, top) < 0)
				top = item;
		}
		
		return Option.some(top);
	}
	
	/**
	 * Finds the minimum value from the provided items
	 * @param items The items the minimum value is searched from
	 * @return The minimum value from the list. None if list is empty.
	 * @deprecated Please use {@link RichIterable#minFrom(RichIterable)} instead
	 */
	public static <T extends Comparable<? super T>> Option<T> minFrom(RichIterable<? extends T> items)
	{
		return minFrom(items, (a, b) -> a.compareTo(b));
	}
}
