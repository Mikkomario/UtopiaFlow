package utopia.flow.structure;

import java.util.HashMap;
import java.util.Map;

/**
 * This class can be used for building an immutable map with buffered data
 * @author Mikko Hilpinen
 * @param <Key> The type of key in this builder
 * @param <Value> The type of value in this builder
 * @since 16.4.2018
 */
public class MapBuilder<Key, Value>
{
	// ATTRIBUTES	-------------------
	
	private Map<Key, Value> map = new HashMap<>();
	
	
	// CONSTRUCTOR	-------------------
	
	/**
	 * Creates an empty buffer
	 */
	public MapBuilder() { }

	/**
	 * Creates a buffer with an initial value
	 * @param key The first key
	 * @param value The first value
	 */
	public MapBuilder(Key key, Value value)
	{
		this.map.put(key, value);
	}
	
	
	// OTHER METHODS	----------------
	
	/**
	 * Builds the map
	 * @return An immutable map based on builder state
	 */
	public ImmutableMap<Key, Value> build()
	{
		return ImmutableMap.of(this.map);
	}
	
	/**
	 * Adds a new item to this map
	 * @param key The new key
	 * @param value The new value
	 * @return This map
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
	 */
	public void add(Key key, Value value)
	{
		this.map.put(key, value);
	}
}
