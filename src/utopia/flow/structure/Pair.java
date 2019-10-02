package utopia.flow.structure;

import java.util.Objects;
import java.util.function.Function;

import utopia.flow.util.StringRepresentable;

/**
 * Pairs is an immutable object that holds two values that may have different data types
 * @author Mikko Hilpinen
 * @param <T1> The data type of the first value in the pair
 * @param <T2> The data type of the second value in the pair
 * @since 27.11.2015
 */
public class Pair<T1, T2> implements StringRepresentable
{
	// ATTRIBUTES	---------------------
	
	private T1 first;
	private T2 second;
	
	
	// CONSTRUCTOR	---------------------
	
	/**
	 * Creates a new pair
	 * @param first The first value of the pair
	 * @param second The second value of the pair
	 */
	public Pair(T1 first, T2 second)
	{
		this.first = first;
		this.second = second;
	}
	
	/**
	 * Creates a new pair by copying another
	 * @param other Another pair
	 * @deprecated Pair has value semantics so copying one is not necessary
	 */
	public Pair(Pair<T1, T2> other)
	{
		this.first = other.first;
		this.second = other.second;
	}
	
	/**
	 * Creates a new pair
	 * @param first The first value
	 * @param second The second value
	 * @return A new pair with the provided values
	 */
	public static <T1, T2> Pair<T1, T2> withValues(T1 first, T2 second)
	{
		return new Pair<>(first, second);
	}
	
	
	// IMPLEMENTED METHODS	------------
	
	@Override
	public int hashCode()
	{
		return Objects.hash(first, second);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Pair))
			return false;
		Pair<?, ?> other = (Pair<?, ?>) obj;
		return Objects.equals(first, other.first) && Objects.equals(second, other.second);
	}
	
	@Override
	public String toString()
	{
		return "(" + first + ", " + second + ")";
	}


	// ACCESSORS	--------------------

	/**
	 * @return The first value in this pair
	 */
	public T1 first() 
	{
		return first;
	}
	
	/**
	 * @return The second value in this pair
	 */
	public T2 second()
	{
		return second;
	}
	
	/**
	 * @return The first value in the pair
	 * @deprecated Renamed to {@link #first()}
	 */
	public T1 getFirst()
	{
		return this.first;
	}
	
	/**
	 * @return The second value in the pair
	 * @deprecated Renamed to {@link #second()}
	 */
	public T2 getSecond()
	{
		return this.second;
	}
	
	
	// OTHER METHODS	--------------
	
	/**
	 * Creates a new pair with the same second value as this
	 * @param newFirst The first value of the new pair
	 * @return A new pair with a shared second value
	 */
	public <K> Pair<K, T2> withFirst(K newFirst)
	{
		return new Pair<>(newFirst, second());
	}
	
	/**
	 * Creates a new pair with the same first value as this
	 * @param newSecond The second value of the new pair
	 * @return A new pair with a shared first value
	 */
	public <K> Pair<T1, K> withSecond(K newSecond)
	{
		return new Pair<>(first(), newSecond);
	}
	
	/**
	 * @param f A mapping function
	 * @return A copy of this pair with first value mapped
	 */
	public <B> Pair<B, T2> mapFirst(Function<? super T1, ? extends B> f)
	{
		return withFirst(f.apply(first()));
	}
	
	/**
	 * @param f A mapping function
	 * @return A copy of this pair with second value mapped
	 */
	public <B> Pair<T1, B> mapSecond(Function<? super T2, ? extends B> f)
	{
		return withSecond(f.apply(second()));
	}
}
