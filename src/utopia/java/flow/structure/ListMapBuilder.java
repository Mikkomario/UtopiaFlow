package utopia.java.flow.structure;

import utopia.java.flow.structure.iterator.RichIterator;
import utopia.java.flow.util.ComparatorUtils;

/**
 * Used for building list maps
 * @author Mikko Hilpinen
 * @since 2.10.2019
 * @param <K> Type of map key
 * @param <V> Type of individual map value
 */
public class ListMapBuilder<K, V> extends Builder<ImmutableMap<K, ImmutableList<V>>, 
	MapBuilder<K, ListBuilder<V>>, Pair<K, ImmutableList<V>>>
{
	// ATTRIBUTES	-------------------
	
	private Option<Integer> valueCapacity;
	
	
	// CONSTRUCTOR	-------------------
	
	/**
	 * Creates a new builder
	 * @param keyCapacity Capacity for keys
	 * @param valueCapacity Capacity of values per key
	 */
	public ListMapBuilder(Option<Integer> keyCapacity, Option<Integer> valueCapacity)
	{
		super(new MapBuilder<>(keyCapacity));
		this.valueCapacity = valueCapacity;
	}
	
	/**
	 * Creates a new builder
	 * @param keyCapacity Capacity for keys
	 * @param valueCapacity Capacity of values per key
	 */
	public ListMapBuilder(int keyCapacity, int valueCapacity)
	{
		super(new MapBuilder<>(keyCapacity));
		this.valueCapacity = Option.some(valueCapacity);
	}
	
	/**
	 * Creates a new builder
	 */
	public ListMapBuilder()
	{
		super(new MapBuilder<>());
		this.valueCapacity = Option.none();
	}
	
	
	// IMPLEMENTED	--------------------

	@Override
	protected ImmutableMap<K, ImmutableList<V>> newResultFrom(MapBuilder<K, ListBuilder<V>> buffer)
	{
		return buffer.result().mapValues(b -> b.result());
	}

	@Override
	protected MapBuilder<K, ListBuilder<V>> copyBuffer(MapBuilder<K, ListBuilder<V>> old)
	{
		MapBuilder<K, ListBuilder<V>> newBuilder = new MapBuilder<>();
		newBuilder.add(old);
		return newBuilder;
	}

	@Override
	protected void append(MapBuilder<K, ListBuilder<V>> buffer, Pair<K, ImmutableList<V>> newItem)
	{
		add(newItem.first(), newItem.second());
	}

	@Override
	protected RichIterator<Pair<K, ImmutableList<V>>> iteratorFrom(MapBuilder<K, ListBuilder<V>> buffer)
	{
		return buffer.iterator().map(p -> p.mapSecond(l -> l.result()));
	}
	
	
	// OTHER	-----------------------
	
	/**
	 * Adds a new key-value-pair to this list map
	 * @param key Target key
	 * @param value New additional value for key
	 */
	public void add(K key, V value)
	{
		if (getBuffer().containsKey(key))
			getBuffer().get(key).add(value);
		else
		{
			ListBuilder<V> newValue = new ListBuilder<>(valueCapacity);
			newValue.add(value);
			getBuffer().put(key, newValue);
		}
	}
	
	/**
	 * Adds multiple values to a key
	 * @param key Target key
	 * @param values New additional values for key
	 */
	public void add(K key, RichIterable<? extends V> values)
	{
		if (getBuffer().containsKey(key))
			getBuffer().get(key).add(values);
		else
		{
			ListBuilder<V> newValue = new ListBuilder<>(ComparatorUtils.max(valueCapacity, 
					values.estimatedSize()));
			newValue.add(values);
			getBuffer().put(key, newValue);
		}
	}
	
	/**
	 * @param key targeted key
	 * @return Whether this builder already contains specified key
	 */
	public boolean containsKey(K key)
	{
		return getBuffer().containsKey(key);
	}
	
	/**
	 * @param key Targeted key
	 * @return Current value of specified key
	 */
	public ImmutableList<V> get(K key)
	{
		if (containsKey(key))
			return getBuffer().get(key).result();
		else
			return ImmutableList.empty();
	}
	
	/**
	 * @param key Targeted key
	 * @return A currently used list builder for the specified key
	 */
	public ListBuilder<V> getBuilder(K key)
	{
		if (containsKey(key))
			return getBuffer().get(key);
		else
		{
			ListBuilder<V> newBuilder = new ListBuilder<>(valueCapacity);
			getBuffer().put(key, newBuilder);
			return newBuilder;
		}
	}
}
