package utopia.flow.structure;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import utopia.flow.util.Option;

/**
 * This map doesn't allow it's contents to be modified and also supports use of options
 * @author Mikko Hilpinen
 * @since 1.11.2017
 */
public class ImmutableMap<Key, Value>
{
	// ATTRIBUTES	------------------
	
	private Map<Key, Value> map;
	
	
	// CONSTRUCTOR	-----------------
	
	public ImmutableMap(Collection<? extends Pair<Key, Value>> data)
	{
		this.map = new HashMap<>();
		for (Pair<Key, Value> pair : data)
		{
			this.map.put(pair.getFirst(), pair.getSecond());
		}
	}
	
	private ImmutableMap(Map<Key, Value> map)
	{
		this.map = map;
	}
	
	private ImmutableMap()
	{
		this.map = new HashMap<>();
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
	
	
	// OTHER METHODS	-----------

	public HashMap<Key, Value> toMutableMap()
	{
		HashMap<Key, Value> mutable = new HashMap<>();
		mutable.putAll(this.map);
		return mutable;
	}
	
	public Set<Pair<Key, Value>> toSet()
	{
		Set<Pair<Key, Value>> set = new HashSet<>(this.map.size());
		for (Key key : this.map.keySet())
		{
			set.add(new Pair<>(key, get(key)));
		}
		
		return set;
	}
	
	public Set<Key> keySet()
	{
		return this.map.keySet();
	}
	
	public Collection<Value> values()
	{
		return this.map.values();
	}
	
	public int size()
	{
		return this.map.size();
	}
	
	public Value get(Key key)
	{
		return this.map.get(key);
	}
	
	public Option<Value> getOption(Key key)
	{
		return new Option<>(this.map.get(key));
	}
	
	public boolean containsKey(Key key)
	{
		return this.map.containsKey(key);
	}
	
	public ImmutableMap<Key, Value> plus(Key key, Value value)
	{
		Map<Key, Value> map = toMutableMap();
		map.put(key, value);
		return new ImmutableMap<>(map);
	}
	
	public ImmutableMap<Key, Value> plus(Collection<? extends Pair<Key, Value>> data)
	{
		Map<Key, Value> map = toMutableMap();
		for (Pair<Key, Value> pair : data)
		{
			map.put(pair.getFirst(), pair.getSecond());
		}
		return new ImmutableMap<>(map);
	}
	
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
}
