package utopia.flow.structure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import utopia.flow.util.Option;

/**
 * This list cannot be modified after creation and is safe to pass around as a value
 * @author Mikko Hilpinen
 * @param <T> The type of element stored within this list
 * @since 2.11.2017
 */
public class ImmutableList<T> implements Iterable<T>
{
	// ATTRIBUTES	-------------------
	
	private static final BiPredicate<Object, Object> SAFE_EQUALS = (a, b) -> a == null ? b == null : a.equals(b);
	
	private List<T> list;
	
	
	// CONSTRUCTOR	-------------------
	
	private ImmutableList(List<T> list)
	{
		this.list = list;
	}
	
	/**
	 * Creates a copy of another immutable list
	 * @param list another immutable list
	 */
	public ImmutableList(ImmutableList<? extends T> list)
	{
		this.list = new ArrayList<>(list.size());
		this.list.addAll(list.list);
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
	 * Creates a combination of multiple immutable lists
	 * @param first The first list
	 * @param more More lists
	 * @return A combination of the lists
	 */
	@SafeVarargs
	public static <T> ImmutableList<T> of(ImmutableList<? extends T> first, ImmutableList<? extends T>... more)
	{
		int size = first.size();
		for (ImmutableList<?> list : more) { size += list.size(); }
		
		List<T> list = new ArrayList<>(size);
		list.addAll(first.list);
		for (ImmutableList<? extends T> m : more) { list.addAll(m.list); }
		
		return new ImmutableList<>(list);
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
		{
			if (other.list != null)
				return false;
		}
		else if (!this.list.equals(other.list))
			return false;
		return true;
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
		return this.list.size();
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
	public ImmutableList<T> plus(ImmutableList<? extends T> elements)
	{
		return plus(elements.list);
	}
	
	/**
	 * Adds multiple elements to this list but keeps it distinct. Doesn't add already existing elements.
	 * @param elements The new elements
	 * @param equals method for checking equality
	 * @return A combined list
	 */
	public ImmutableList<T> plusDistinct(ImmutableList<? extends T> elements, BiPredicate<? super T, ? super T> equals)
	{
		return plus(elements.filter(newElem -> !exists(oldElem -> equals.test(oldElem, newElem))));
	}
	
	/**
	 * Adds multiple elements to this list but keeps it distinct. Doesn't add already existing elements.
	 * @param elements The new elements
	 * @return A combined list
	 */
	public ImmutableList<T> plusDistinct(ImmutableList<? extends T> elements)
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
		return Option.positiveInt(this.list.indexOf(element));
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
	public <B> ImmutableList<B> flatMap(Function<? super T, Stream<? extends B>> f)
	{
		return new ImmutableList<>(stream().flatMap(f).collect(Collectors.toList()));
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
	 * @return A stream from this list
	 */
	public Stream<T> stream()
	{
		return this.list.stream();
	}
}
