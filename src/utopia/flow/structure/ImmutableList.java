package utopia.flow.structure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
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
	
	private List<T> list;
	
	
	// CONSTRUCTOR	-------------------
	
	private ImmutableList(List<T> list)
	{
		this.list = list;
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
	public boolean contains(T element)
	{
		return this.list.contains(element);
	}
	
	/**
	 * Checks whether this list contains all of the elements from the specified collection
	 * @param elements A collection of elements
	 * @return Whether this list contains all elements from the specified collection
	 */
	public boolean contains(Collection<? extends T> elements)
	{
		return this.list.containsAll(elements);
	}
	
	/**
	 * Checks whether this list contains all of the elements from the specified collection
	 * @param elements A collection of elements
	 * @return Whether this list contains all elements from the specified collection
	 */
	public boolean contains(ImmutableList<T> elements)
	{
		return contains(elements.list);
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
	 * Creates a new list with the elements appended
	 * @param elements multiple elements
	 * @return a list with the elements appended
	 */
	public ImmutableList<T> plus(Collection<? extends T> elements)
	{
		ArrayList<T> mutable = toMutableList(elements.size());
		mutable.addAll(elements);
		return new ImmutableList<>(mutable);
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
	public ImmutableList<T> minus(T element)
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
	public ImmutableList<T> minus(Collection<? extends T> elements)
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
	public ImmutableList<T> minus(ImmutableList<? extends T> elements)
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
	 * @return A stream from this list
	 */
	public Stream<T> stream()
	{
		return this.list.stream();
	}
}
