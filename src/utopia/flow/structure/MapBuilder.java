package utopia.flow.structure;

import java.util.HashMap;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * This class can be used for building an immutable map with buffered data. Map builders are not thread safe.
 * @author Mikko Hilpinen
 * @param <Key> The type of key in this builder
 * @param <Value> The type of value in this builder
 * @since 16.4.2018
 */
public class MapBuilder<Key, Value> extends Builder<ImmutableMap<Key, Value>, 
		IterableMapWrapper<Key, Value>, Pair<Key, Value>> implements BiIterable<Key, Value>
{
	// CONSTRUCTOR	-------------------
	
	/**
	 * Creates an empty buffer
	 */
	public MapBuilder()
	{
		super(new IterableMapWrapper<>(new HashMap<>()));
	}

	/**
	 * Creates a buffer with an initial value
	 * @param key The first key
	 * @param value The first value
	 */
	public MapBuilder(Key key, Value value)
	{
		super(new IterableMapWrapper<>(new HashMap<>()));
		put(key, value);
	}
	
	
	// IMPLEMENTED	--------------------
	
	@Override
	protected ImmutableMap<Key, Value> newResultFrom(IterableMapWrapper<Key, Value> buffer)
	{
		return new ImmutableMap<>(buffer.get());
	}

	@Override
	protected IterableMapWrapper<Key, Value> copyBuffer(IterableMapWrapper<Key, Value> old)
	{
		return new IterableMapWrapper<>(new HashMap<>(old.get()));
	}

	@Override
	protected void append(IterableMapWrapper<Key, Value> buffer, Pair<Key, Value> newItem)
	{
		buffer.get().put(newItem.getFirst(), newItem.getSecond());
	}
	
	
	// OTHER METHODS	----------------
	
	/**
	 * Adds a key value pair to this builder
	 * @param key The key to be added
	 * @param value The value to be added
	 */
	public void put(Key key, Value value)
	{
		add(new Pair<>(key, value));
	}
	
	/**
	 * @param key A key
	 * @return The current value for the specified key
	 */
	public Value get(Key key)
	{
		return getBuffer().get().get(key);
	}
	
	/**
	 * Adds a new value to this buffer, either inserting it with a new key or combining it with 
	 * an existing mapping
	 * @param key The target key
	 * @param value The inserted value (raw)
	 * @param toValue A function for converting the value into a new insertion 
	 * (used when no such key exists yet)
	 * @param combine A function for appending the value into an existing value 
	 * (used when key already exists)
	 */
	public <B> void mapOrCombine(Key key, B value, Function<? super B, ? extends Value> toValue, 
			BiFunction<? super Value, ? super B, ? extends Value> combine)
	{
		if (containsKey(key))
			put(key, combine.apply(get(key), value));
		else
			put(key, toValue.apply(value));
	}
	
	/**
	 * Adds a new value to this buffer, either inserting it with a new key or combining it with 
	 * an existing mapping
	 * @param key The target key
	 * @param value The inserted value
	 * @param combine A function for appending the value into an existing value 
	 * (used when key already exists)
	 */
	public void putOrCombine(Key key, Value value, 
			BiFunction<? super Value, ? super Value, ? extends Value> combine)
	{
		mapOrCombine(key, value, Function.identity(), combine);
	}
	
	/**
	 * @param key A key
	 * @return Whether this buffer already contains the specified key
	 */
	public boolean containsKey(Object key)
	{
		return getBuffer().get().containsKey(key);
	}
	
	/**
	 * Adds a new item to this map
	 * @param key The new key
	 * @param value The new value
	 * @return This map
	 * @deprecated Please move to using {@link #put(Object, Object)} instead
	 */
	public MapBuilder<Key, Value> plus(Key key, Value value)
	{
		add(key, value);
		return this;
	}
	
	/**
	 * Adds a new item to this map
	 * @param key The new key
	 * @param value The new value
	 * @deprecated Please move to using {@link #put(Object, Object)} instead
	 */
	public void add(Key key, Value value)
	{
		put(key, value);
	}
}
