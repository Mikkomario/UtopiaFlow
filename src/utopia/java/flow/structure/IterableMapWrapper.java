package utopia.java.flow.structure;

import java.util.Map;

import utopia.java.flow.structure.iterator.RichIterator;

/**
 * This is a wrapper for maps that allows iterating with pairs
 * @author Mikko Hilpinen
 * @param <Key> The type for map keys
 * @param <Value> The type for map values
 * @since 3.8.2018
 */
public class IterableMapWrapper<Key, Value> implements Wrapper<Map<Key, Value>>, BiIterable<Key, Value>
{
	// ATTRIBUTES	----------------
	
	private Map<Key, Value> wrapped;
	
	
	// CONSTRUCTOR	----------------
	
	/**
	 * Wraps a map
	 * @param map The map to be wrapped
	 */
	public IterableMapWrapper(Map<Key, Value> map)
	{
		this.wrapped = map;
	}
	
	
	// IMPLEMENTED	----------------

	@Override
	public Map<Key, Value> get()
	{
		return wrapped;
	}

	@Override
	public RichIterator<Pair<Key, Value>> iterator()
	{
		return ImmutableMap.of(wrapped).iterator();
	}

	@Override
	public Option<Integer> estimatedSize()
	{
		return Option.some(wrapped.size());
	}
}
