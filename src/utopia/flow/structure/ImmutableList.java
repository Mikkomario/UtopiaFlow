package utopia.flow.structure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import utopia.flow.util.Lazy;
import utopia.flow.util.Option;
import utopia.flow.util.Streamable;
import utopia.flow.util.ThrowingConsumer;

/**
 * This list cannot be modified after creation and is safe to pass around as a value
 * @author Mikko Hilpinen
 * @param <T> The type of element stored within this list
 * @since 2.11.2017
 */
public class ImmutableList<T> implements Iterable<T>, Streamable<T>
{
	// ATTRIBUTES	-------------------
	
	private static final BiPredicate<Object, Object> SAFE_EQUALS = (a, b) -> a == null ? b == null : a.equals(b);
	
	private final List<T> list;
	private final Lazy<Integer> size;
	
	
	// CONSTRUCTOR	-------------------
	
	private ImmutableList(List<T> list)
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
	 * @param more More values
	 * @return A list with multiple values
	 */
	@SafeVarargs
	public static <T> ImmutableList<T> withValues(T first, T... more)
	{
		List<T> list = new ArrayList<>(more.length + 1);
		list.add(first);
		for (T element : more) { list.add(element); }
		return new ImmutableList<>(list);
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
	public static <T> ImmutableList<T> flatten(Streamable<? extends Streamable<? extends T>> list)
	{
		// int size = list.fold(0, (total, elem) -> total + elem.size());
		List<T> mutable = new ArrayList<>();
		list.stream().forEach(elem -> elem.stream().forEach(mutable::add));
		
		return new ImmutableList<>(mutable);
	}
	
	/**
	 * Creates a combination of multiple immutable lists
	 * @param first The first list
	 * @param more More lists
	 * @return A combination of the lists
	 */
	@SafeVarargs
	public static <T> ImmutableList<T> flatten(Streamable<? extends T> first, Streamable<? extends T>... more)
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
		if (maxInclusive <= minInclusive)
			return empty();
		else
		{
			List<Integer> buffer = new ArrayList<>(maxInclusive - minInclusive);
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
		List<T> buffer = new ArrayList<>(size);
		for (int i = 0; i < size; i++)
		{
			buffer.add(generator.get());
		}
		return new ImmutableList<>(buffer);
	}
	
	/**
	 * Fills a list with a single item repeated multiple times
	 * @param size The size of the list
	 * @param item The item that will fill the list
	 * @return A list filled with the item
	 */
	public static <T> ImmutableList<T> filledWith(int size, T item)
	{
		List<T> buffer = new ArrayList<>(size);
		for (int i = 0; i < size; i++)
		{
			buffer.add(item);
		}
		return new ImmutableList<>(buffer);
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
	public Iterator<T> iterator()
	{
		return this.list.iterator();
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
	 * @return Whether this list is empty
	 */
	public boolean isEmpty()
	{
		return this.list.isEmpty();
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
	 * Checks whether this list contains the specified element
	 * @param element an element
	 * @return Whether this list contains the specified element
	 */
	public boolean contains(Object element)
	{
		return this.list.contains(element);
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
	public ImmutableList<T> plus(Collection<? extends T> elements)
	{
		if (elements.isEmpty())
			return this;
		else
		{
			ArrayList<T> mutable = toMutableList(elements.size());
			mutable.addAll(elements);
			return new ImmutableList<>(mutable);
		}
	}
	
	/**
	 * Creates a new list with the elements appended
	 * @param elements multiple elements
	 * @return a list with the elements appended
	 */
	public ImmutableList<T> plus(Streamable<? extends T> elements)
	{
		return plus(elements.stream().collect(Collectors.toList()));
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
	public ImmutableList<T> plusDistinct(Streamable<? extends T> elements, BiPredicate<? super T, ? super T> equals)
	{
		return plus(elements.stream().filter(newElem -> !exists(oldElem -> equals.test(oldElem, newElem))).collect(Collectors.toList()));
	}
	
	/**
	 * Adds multiple elements to this list but keeps it distinct. Doesn't add already existing elements.
	 * @param elements The new elements
	 * @return A combined list
	 */
	public ImmutableList<T> plusDistinct(Streamable<? extends T> elements)
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
	public ImmutableList<T> overwrite(ImmutableList<? extends T> elements, BiPredicate<? super T, ? super T> equals)
	{
		return filter(oldElem -> !elements.exists(newElem -> equals.test(oldElem, newElem))).plus(elements);
	}
	
	/**
	 * Adds multiple elements to this list but keeps it distinct. Overwrites old elements with new versions.
	 * @param elements The new elements
	 * @return A combined list
	 */
	public ImmutableList<T> overwrite(ImmutableList<? extends T> elements)
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
	public <U, R> ImmutableList<R> mergedWith(ImmutableList<U> other, BiFunction<? super T, ? super U, ? extends R> merge)
	{
		List<R> buffer = new ArrayList<>();
		for (int i = 0; i < size() && i < other.size(); i++)
		{
			buffer.add(merge.apply(get(i), other.get(i)));
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
	public <U> ImmutableList<Pair<T, U>> mergedWith(ImmutableList<U> other)
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
		Comparator<T> comparator = new Comparator<>()
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
	 * @return The first element in this list
	 * @throws NoSuchElementException If the list is empty
	 * @see #headOption()
	 */
	public T head() throws NoSuchElementException
	{
		return get(0);
	}
	
	/**
	 * @return The first element in this list. None if the list is empty or if the first element is null
	 */
	public Option<T> headOption()
	{
		if (isEmpty())
			return Option.none();
		else
			return new Option<>(get(0));
	}
	
	/**
	 * @param n A number of elements to be dropped
	 * @return A copy of this list without the first n elements
	 */
	public ImmutableList<T> dropFirst(int n)
	{
		if (n <= 0)
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
	 * @param n a number of elements
	 * @return The first n elements of this list
	 */
	public ImmutableList<T> first(int n)
	{
		if (n >= size())
			return this;
		else
		{
			ArrayList<T> result = new ArrayList<>(n);
			for (int i = 0; i < n; i++) { result.add(get(i)); }
			return new ImmutableList<>(result);
		}
	}
	
	/**
	 * Takes elements from the list as long as they satisfy a certain predicate
	 * @param f The predicate
	 * @return The first n elements in this list which all satisfy the predicate
	 */
	public ImmutableList<T> takeWhile(Predicate<? super T> f)
	{
		List<T> buffer = new ArrayList<>(size());
		for (T item : this)
		{
			if (f.test(item))
				buffer.add(item);
			else
				break;
		}
		
		return new ImmutableList<>(buffer);
	}
	
	/**
	 * @param n The number of elements to be dropped
	 * @return A copy of this list without the last n elements
	 */
	public ImmutableList<T> dropLast(int n)
	{
		if (n <= 0)
			return this;
		else
		{
			ArrayList<T> result = new ArrayList<>(Math.max(size() - n, 0));
			for (int i = 0; i < size() - n; i++) { result.add(get(i)); }
			return new ImmutableList<>(result);
		}
	}
	
	/**
	 * @param n The number of elements to be included
	 * @return The last n elements of this list
	 */
	public ImmutableList<T> last(int n)
	{
		if (n >= size())
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
	 * Finds the maximum value in this list based on a mapped value
	 * @param f A mapping function
	 * @return The maximum item in the list based on the mapped value. None if list is empty.
	 * @see #maxFrom(ImmutableList)
	 */
	public <K extends Comparable<? super K>> Option<T> maxBy(Function<? super T, ? extends K> f)
	{
		return maxFrom(this, (a, b) -> f.apply(a).compareTo(f.apply(b)));
	}
	
	/**
	 * Finds the minimum value in this list based on a mapped value
	 * @param f A mapping function
	 * @return The minimum item in the list based on the mapped value. None if list is empty.
	 * @see #minFrom(ImmutableList)
	 */
	public <K extends Comparable<? super K>> Option<T> minBy(Function<? super T, ? extends K> f)
	{
		return minFrom(this, (a, b) -> f.apply(a).compareTo(f.apply(b)));
	}
	
	/**
	 * Finds the first element that satisfies the predicate
	 * @param f A predicate
	 * @return The first element that satisfies the predicate
	 */
	public Option<T> find(Predicate<? super T> f)
	{
		for (T element : this)
		{
			if (f.test(element))
				return Option.some(element);
		}
		return Option.none();
	}
	
	/**
	 * Checks whether this list contains an element that satisfies the predicate
	 * @param f a predicate
	 * @return Is the predicate true for any of the elements in this list. False if empty.
	 */
	public boolean exists(Predicate<? super T> f)
	{
		for (T element : this)
		{
			if (f.test(element))
				return true;
		}
		return false;
	}
	
	/**
	 * Checks if a predicate is true for all elements in the list
	 * @param f a predicate
	 * @return Is the predicate true for all of the elements in this list. True if empty.
	 */
	public boolean forAll(Predicate<? super T> f)
	{
		return !exists(f.negate());
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
	 * Maps this list
	 * @param f a mapping function
	 * @return The mapped list
	 */
	public <B> ImmutableList<B> map(Function<? super T, B> f)
	{
		return new ImmutableList<>(stream().map(f).collect(Collectors.toList()));
	}
	
	/**
	 * Flatmaps the list
	 * @param f a mapping function
	 * @return The mapped list
	 */
	public <B> ImmutableList<B> flatMap(Function<? super T, Streamable<? extends B>> f)
	{
		return new ImmutableList<>(stream().flatMap(i -> f.apply(i).stream()).collect(Collectors.toList()));
	}
	
	/**
	 * Returns the first transformed item where the transformation is available. Similar to calling flatMap(f).first(), 
	 * except that this function doesn't transform unnecessary items.
	 * @param f The transformation function
	 * @return The first transformation result where the transformation is defined. None if none of this list's items 
	 * could be transformed.
	 */
	public <B> Option<B> flatMapFirst(Function<? super T, Option<B>> f)
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
	 * Performs a fold operation over this list, going from left to right
	 * @param start The starting value
	 * @param f A function that folds items into the result value
	 * @return The resulting value
	 */
	public <B> B fold(B start, BiFunction<? super B, ? super T, ? extends B> f)
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
	public T reduce(BiFunction<? super T, ? super T, ? extends T> f) throws NoSuchElementException
	{
		T result = head();
		for (T item : tail())
		{
			result = f.apply(result, item);
		}
		
		return result;
	}
	
	/**
	 * Performs a reduce over the list from left to right
	 * @param f The reduce function
	 * @return The reduce result. None if the list was empty
	 */
	public Option<T> reduceOption(BiFunction<? super T, ? super T, ? extends T> f)
	{
		if (isEmpty())
			return Option.none();
		else
			return Option.some(reduce(f));
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
	 * Performs a throwing operation on each of the elements in this list. Stops iterating on the first exception.
	 * @param f The function that is performed for each element in the list
	 * @throws Exception The first exception thrown by the function
	 */
	public void forEachThrowing(ThrowingConsumer<? super T> f) throws Exception
	{
		for (T item : this)
		{
			f.accept(item);
		}
	}
	
	/**
	 * Performs an operation on two lists at the same time
	 * @param other Another list
	 * @param f A function that operates on two values at the same time. The left values come from this list. 
	 * The right values come from the other list.
	 */
	public <U> void forEachSimultaneouslyWith(ImmutableList<U> other, BiConsumer<T, U> f)
	{
		mergedWith(other).forEach(p -> f.accept(p.getFirst(), p.getSecond()));
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
	 * Groups the contents of this list into subcategories based on mapping results
	 * @param f A mapping function
	 * @return The contents of this list grouped to categories based on the mapping function results
	 */
	public <B> ImmutableMap<B, ImmutableList<T>> groupBy(Function<? super T, ? extends B> f)
	{
		Map<B, List<T>> buffer = new HashMap<>();
		for (T item : this)
		{
			B category = f.apply(item);
			if (!buffer.containsKey(category))
				buffer.put(category, new ArrayList<>());
			buffer.get(category).add(item);
		}
		
		return ImmutableMap.of(buffer).mapValues(list -> new ImmutableList<>(list));
	}
	
	/**
	 * Creates a map based on the contents of this list and mapping results. Multiple values may be grouped 
	 * together under a single key
	 * @param f a function that maps the items in this list to key value pairs
	 * @return A map with values of mapped items
	 */
	public <Key, Value> ImmutableMap<Key, ImmutableList<Value>> toListMap(Function<? super T, Pair<Key, Value>> f)
	{
		return map(f).groupBy(keyValue -> keyValue.getFirst()).mapValues(keyValueList -> 
				keyValueList.map(keyValue -> keyValue.getSecond()));
	}
	
	/**
	 * Divides this list into two categories
	 * @param f The filter function that is used for splitting this list
	 * @return The filter results. One list for accepted values and one list for not accepted values
	 */
	public ImmutableMap<Boolean, ImmutableList<T>> divideBy(Predicate<? super T> f)
	{
		return ImmutableMap.withValue(true, filter(f)).plus(false, filter(f.negate()));
	}
	
	/**
	 * Finds the maximum value from the provided items
	 * @param items The items the maximum value is searched from
	 * @param comparator The comparator used for comparing the values
	 * @return The maximum value from the list. None if list is empty.
	 */
	public static <T> Option<T> maxFrom(ImmutableList<? extends T> items, Comparator<? super T> comparator)
	{
		if (items.isEmpty())
			return Option.none();
		
		T top = items.head();
		for (T item : items.tail())
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
	 */
	public static <T extends Comparable<? super T>> Option<T> maxFrom(ImmutableList<? extends T> items)
	{
		return maxFrom(items, (a, b) -> a.compareTo(b));
	}
	
	/**
	 * Finds the minimum value from the provided items
	 * @param items The items the minimum value is searched from
	 * @param comparator The comparator used for comparing the values
	 * @return The minimum value from the list. None if list is empty.
	 */
	public static <T> Option<T> minFrom(ImmutableList<? extends T> items, Comparator<? super T> comparator)
	{
		if (items.isEmpty())
			return Option.none();
		
		T top = items.head();
		for (T item : items.tail())
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
	 */
	public static <T extends Comparable<? super T>> Option<T> minFrom(ImmutableList<? extends T> items)
	{
		return minFrom(items, (a, b) -> a.compareTo(b));
	}
}
