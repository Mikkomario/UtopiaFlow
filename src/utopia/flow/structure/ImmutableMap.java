package utopia.flow.structure;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import utopia.flow.util.Lazy;
import utopia.flow.util.Option;

/**
 * This map doesn't allow it's contents to be modified and also supports use of options
 * @author Mikko Hilpinen
 * @param <Key> The type of the keys in the map
 * @param <Value> The types of the values stored in the map
 * @since 1.11.2017
 */
public class ImmutableMap<Key, Value> implements Iterable<Pair<Key, Value>>
{
	// ATTRIBUTES	------------------
	
	private final Map<Key, Value> map;
	
	private final Lazy<Integer> size;
	private final Lazy<ImmutableList<Key>> keys;
	private final Lazy<ImmutableList<Value>> values;
	private final Lazy<ImmutableList<Pair<Key, Value>>> list;
	
	
	// CONSTRUCTOR	-----------------
	
	/**
	 * Creates a new empty map
	 */
	public ImmutableMap()
	{
		this.map = new HashMap<>(0);
		
		this.keys = new Lazy<>(() -> ImmutableList.of(this.map.keySet()));
		this.values = new Lazy<>(() -> ImmutableList.of(this.map.values()));
		this.list = new Lazy<>(() -> ImmutableList.of(toSet()));
		this.size = new Lazy<>(() -> this.map.size());
	}
	
	/**
	 * Creates a new map with existing data
	 * @param data The key value pairs stored in the map
	 */
	public ImmutableMap(Collection<? extends Pair<Key, Value>> data)
	{
		this.map = new HashMap<>(data.size());
		for (Pair<Key, Value> pair : data)
		{
			this.map.put(pair.getFirst(), pair.getSecond());
		}
		
		this.keys = new Lazy<>(() -> ImmutableList.of(this.map.keySet()));
		this.values = new Lazy<>(() -> ImmutableList.of(this.map.values()));
		this.size = new Lazy<>(() -> this.map.size());
		this.list = new Lazy<>(() -> ImmutableList.of(toSet()));
	}
	
	/**
	 * Creates a new map with a mutable map<br>
	 * <b>NB: The provided map must not be owned, used or modified by any other object after this method is called</b>
	 * @param map A mutable map
	 */
	protected ImmutableMap(Map<Key, Value> map)
	{
		this.map = map;
		
		this.keys = new Lazy<>(() -> ImmutableList.of(this.map.keySet()));
		this.values = new Lazy<>(() -> ImmutableList.of(this.map.values()));
		this.size = new Lazy<>(() -> this.map.size());
		this.list = new Lazy<>(() -> ImmutableList.of(toSet()));
	}
	
	/**
	 * @return An empty map
	 */
	public static <Key, Value> ImmutableMap<Key, Value> empty()
	{
		return new ImmutableMap<>();
	}
	
	/**
	 * Creates a map with a single key value pair
	 * @param key The key for the value
	 * @param value The value stored in the map
	 * @return A map containing only a single key value pair
	 */
	public static <Key, Value> ImmutableMap<Key, Value> withValue(Key key, Value value)
	{
		Map<Key, Value> map = new HashMap<>(1);
		map.put(key, value);
		return new ImmutableMap<>(map);
	}
	
	/**
	 * Creates an immutable copy of a mutable map
	 * @param map a map
	 * @return an immutable copy of the mutable map
	 */
	public static <Key, Value> ImmutableMap<Key, Value> of(Map<Key, Value> map)
	{
		return new ImmutableMap<>(new HashMap<>(map));
	}
	
	/**
	 * @param data key value pairs
	 * @return A map of the key value pairs
	 */
	public static <Key, Value> ImmutableMap<Key, Value> of(ImmutableList<Pair<Key, Value>> data)
	{
		return new ImmutableMap<>(data.toMutableList());
	}
	
	
	// IMPLEMENTED METHODS	-------
	
	@Override
	public int hashCode()
	{
		return this.map.hashCode();
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ImmutableMap))
			return false;
		ImmutableMap<?, ?> other = (ImmutableMap<?, ?>) obj;
		if (this.map == null)
		{
			if (other.map != null)
				return false;
		}
		else if (!this.map.equals(other.map))
			return false;
		return true;
	}
	
	@Override
	public String toString()
	{
		StringBuilder s = new StringBuilder("");
		s.append("{");
		
		boolean isFirst = true;
		for (Pair<Key, Value> keyValuePair : toSet())
		{
			if (isFirst)
				isFirst = false;
			else
				s.append(", ");
			
			s.append(keyValuePair.getFirst());
			s.append(":");
			s.append(keyValuePair.getSecond());
		}
		
		s.append("}");
		return s.toString();
	}
	
	@Override
	public Iterator<Pair<Key, Value>> iterator()
	{
		return toSet().iterator();
	}
	
	
	// OTHER METHODS	-----------

	/**
	 * @return A mutable version of this immutable map
	 */
	public HashMap<Key, Value> toMutableMap()
	{
		return new HashMap<>(this.map);
	}
	
	private HashMap<Key, Value> toMutableMap(int extraCapacity)
	{
		HashMap<Key, Value> mutable = new HashMap<>(size() + extraCapacity);
		mutable.putAll(this.map);
		return mutable;
	}
	
	/**
	 * @return An immutable list representation of this map
	 */
	public ImmutableList<Pair<Key, Value>> toList()
	{
		return this.list.get();
	}
	
	/**
	 * @return This map as a set of key value pairs
	 */
	public Set<Pair<Key, Value>> toSet()
	{
		Set<Pair<Key, Value>> set = new HashSet<>(this.map.size());
		for (Key key : this.map.keySet())
		{
			set.add(new Pair<>(key, get(key)));
		}
		
		return set;
	}
	
	/**
	 * @return A set containing all keys of this map
	 */
	public ImmutableList<Key> keys()
	{
		return this.keys.get();
	}
	
	/**
	 * @return A set containing all values of this map
	 */
	public ImmutableList<Value> values()
	{
		return this.values.get();
	}
	
	/**
	 * @return Whether this map is empty
	 */
	public boolean isEmpty()
	{
		return this.map.isEmpty();
	}
	
	/**
	 * @return The size of this map
	 */
	public int size()
	{
		return this.size.get();
	}
	
	/**
	 * Retrieves a value from the map
	 * @param key A key
	 * @return A value from the map. Null if no such key exists
	 */
	public Value get(Key key)
	{
		return this.map.get(key);
	}
	
	/**
	 * Retrieves a value from the map
	 * @param key A key
	 * @return A value from the map. None if no such key / value exists
	 */
	public Option<Value> getOption(Key key)
	{
		return new Option<>(this.map.get(key));
	}
	
	/**
	 * Checks whether the map contains a key
	 * @param key A key
	 * @return Whether this map contains the key
	 */
	public boolean containsKey(Key key)
	{
		return this.map.containsKey(key);
	}
	
	/**
	 * Creates a new map with the specified key value pair appended
	 * @param key a key
	 * @param value a value for the key
	 * @return a new map with the key appended
	 */
	public ImmutableMap<Key, Value> plus(Key key, Value value)
	{
		Map<Key, Value> map = toMutableMap(1);
		map.put(key, value);
		return new ImmutableMap<>(map);
	}
	
	/**
	 * Creates a new map with multiple key value pairs appended
	 * @param data The data that is appended
	 * @return A map containing both this map's key value pairs and the provided pairs
	 */
	public ImmutableMap<Key, Value> plus(Collection<? extends Pair<Key, Value>> data)
	{
		Map<Key, Value> map = toMutableMap(data.size());
		for (Pair<Key, Value> pair : data)
		{
			map.put(pair.getFirst(), pair.getSecond());
		}
		return new ImmutableMap<>(map);
	}
	
	/**
	 * Creates a new map with multiple key value pairs appended
	 * @param data The data that is appended
	 * @return A map containing both this map's key value pairs and the provided pairs
	 */
	public ImmutableMap<Key, Value> plus(ImmutableList<? extends Pair<Key, Value>> data)
	{
		Map<Key, Value> map = toMutableMap(data.size());
		for (Pair<Key, Value> pair : data)
		{
			map.put(pair.getFirst(), pair.getSecond());
		}
		return new ImmutableMap<>(map);
	}
	
	/**
	 * Creates a new map with the other map's key value pairs appended
	 * @param other Another map
	 * @return A map containing both this map's key value pairs and the other map's key value pairs
	 */
	public ImmutableMap<Key, Value> plus(Map<? extends Key, ? extends Value> other)
	{
		Map<Key, Value> map = toMutableMap(other.size());
		map.putAll(other);
		return new ImmutableMap<>(map);
	}
	
	/**
	 * Creates a new map with the other map's key value pairs appended
	 * @param other Another map
	 * @return A map containing both this map's key value pairs and the other map's key value pairs
	 */
	public ImmutableMap<Key, Value> plus(ImmutableMap<? extends Key, ? extends Value> other)
	{
		return plus(other.map);
	}
	
	/**
	 * Creates a new map with the specified key removed
	 * @param key A key that is removed
	 * @return A map without the specified key
	 */
	public ImmutableMap<Key, Value> minus(Key key)
	{
		if (containsKey(key))
		{
			Map<Key, Value> map = toMutableMap();
			map.remove(key);
			return new ImmutableMap<>(map);
		}
		else
			return this;
	}
	
	/**
	 * Finds a key value pair from this map
	 * @param f The function that tests keys & values
	 * @return the first matching key value pair in the map or none if there were no matches
	 */
	public Option<Pair<Key, Value>> find(BiPredicate<? super Key, ? super Value> f)
	{
		return toList().find(p -> f.test(p.getFirst(), p.getSecond()));
	}
	
	/**
	 * Maps this map
	 * @param f a function that maps key value pairs
	 * @return A mapped map
	 */
	public <K, V> ImmutableMap<K, V> map(BiFunction<Key, Value, Pair<K, V>> f)
	{
		return new ImmutableMap<>(toSet().stream().map(
				keyValue -> f.apply(keyValue.getFirst(), keyValue.getSecond())).collect(Collectors.toSet()));
	}
	
	/**
	 * flatmaps this map
	 * @param f a function that maps key value pairs to collections
	 * @return A mapped map
	 */
	public <K, V> ImmutableMap<K, V> flatMap(BiFunction<Key, Value, Stream<Pair<K, V>>> f)
	{
		return new ImmutableMap<>(toSet().stream().flatMap(
				keyValue -> f.apply(keyValue.getFirst(), keyValue.getSecond())).collect(Collectors.toSet()));
	}
	
	/**
	 * Maps keys in this map
	 * @param f a function that maps keys
	 * @return A mapped map
	 */
	public <K> ImmutableMap<K, Value> mapKeys(Function<? super Key, K> f)
	{
		return map((k, v) -> new Pair<>(f.apply(k), v));
	}
	
	/**
	 * Maps values in this map
	 * @param f a function that maps values
	 * @return A mapped map
	 */
	public <V> ImmutableMap<Key, V> mapValues(Function<? super Value, V> f)
	{
		return map((k, v) -> new Pair<>(k, f.apply(v)));
	}
	
	/**
	 * Maps values in this map, may remove some values
	 * @param f a function that maps values to a new value or empty
	 * @return A mapped map
	 */
	public <V> ImmutableMap<Key, V> flatMapValues(Function<? super Value, Option<V>> f)
	{
		return flatMap((k, v) -> f.apply(v).map(newValue -> new Pair<>(k, newValue)).stream());
	}
	
	/**
	 * Filters this map so that it only contains elements accepted by the provided predicate
	 * @param f a predicate that determines whether a key value pair is kept in the map
	 * @return A filtered map
	 */
	public ImmutableMap<Key, Value> filter(BiPredicate<Key, Value> f)
	{
		return new ImmutableMap<>(toSet().stream().filter(keyValue -> 
				f.test(keyValue.getFirst(), keyValue.getSecond())).collect(Collectors.toSet()));
	}
	
	/**
	 * Performs a consumer for each key value pair in this map
	 * @param f a consumer for key value pairs
	 */
	public void forEach(BiConsumer<Key, Value> f)
	{
		for (Pair<Key, Value> keyValue : this)
		{
			f.accept(keyValue.getFirst(), keyValue.getSecond());
		}
	}
	
	/**
	 * @return A version of this map that has lists for values
	 */
	public ImmutableMap<Key, ImmutableList<Value>> toListMap()
	{
		return mapValues(v -> ImmutableList.withValue(v));
	}
	
	/**
	 * Combines two list maps with each other. the final list map will contain values from both maps
	 * @param first The first list map
	 * @param second The second list map
	 * @return The combined map
	 */
	public static <Key, Value> ImmutableMap<Key, ImmutableList<Value>> append(
			ImmutableMap<Key, ImmutableList<Value>> first, ImmutableMap<? extends Key, ? extends ImmutableList<Value>> second)
	{
		Map<Key, ImmutableList<Value>> buffer = first.toMutableMap(second.size());
		second.forEach((key, values) -> 
		{
			ImmutableList<Value> newValues = first.getOption(key).map(l -> l.plus(values)).getOrElse(values);
			buffer.put(key, newValues);
		});
		
		return new ImmutableMap<>(buffer);
	}
	
	/**
	 * Combines a list map with a set of values
	 * @param listMap A list map
	 * @param key The targeted key
	 * @param values The appended values
	 * @return A new map with the values appended
	 */
	public static <Key, Value> ImmutableMap<Key, ImmutableList<Value>> append(
			ImmutableMap<Key, ImmutableList<Value>> listMap, Key key, ImmutableList<Value> values)
	{
		ImmutableList<Value> newValues = listMap.getOption(key).map(l -> l.plus(values)).getOrElse(values);
		return listMap.plus(key, newValues);
	}
	
	/**
	 * Appends a value to a list map
	 * @param listMap A list map
	 * @param key The targeted key
	 * @param value The appended value
	 * @return A new map with the value appended
	 */
	public static <Key, Value> ImmutableMap<Key, ImmutableList<Value>> append(
			ImmutableMap<Key, ImmutableList<Value>> listMap, Key key, Value value)
	{
		ImmutableList<Value> newValues = listMap.getOption(key).map(l -> l.plus(value)).getOrElse(
				() -> ImmutableList.withValue(value));
		return listMap.plus(key, newValues);
	}
}
