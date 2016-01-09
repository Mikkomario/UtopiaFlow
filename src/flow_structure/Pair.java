package flow_structure;

/**
 * Pairs is an immutable object that holds two values that may have different data types
 * @author Mikko Hilpinen
 * @param <T1> The data type of the first value in the pair
 * @param <T2> The data type of the second value in the pair
 * @since 27.11.2015
 */
public class Pair<T1, T2>
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
	 */
	public Pair(Pair<T1, T2> other)
	{
		this.first = other.first;
		this.second = other.second;
	}
	
	
	// IMPLEMENTED METHODS	------------
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getFirst() == null) ? 0 : getFirst().hashCode());
		result = prime * result + ((getSecond() == null) ? 0 : getSecond().hashCode());
		return result;
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
		if (getFirst() == null)
		{
			if (other.getFirst() != null)
				return false;
		}
		else if (!getFirst().equals(other.getFirst()))
			return false;
		if (getSecond() == null)
		{
			if (other.getSecond() != null)
				return false;
		}
		else if (!getSecond().equals(other.getSecond()))
			return false;
		
		return true;
	}


	// ACCESSORS	--------------------

	/**
	 * @return The first value in the pair
	 */
	public T1 getFirst()
	{
		return this.first;
	}
	
	/**
	 * @return The second value in the pair
	 */
	public T2 getSecond()
	{
		return this.second;
	}
	
	/**
	 * Updates the first value of the pair
	 * @param first The new first value of the pair
	 */
	protected void setFirst(T1 first)
	{
		this.first = first;
	}
	
	/**
	 * Updates the second value of the pair
	 * @param second The new second value of the pair
	 */
	protected void setSecond(T2 second)
	{
		this.second = second;
	}
	
	
	// OTHER METHODS	--------------
	
	/**
	 * Creates a new pair with the same second value as this
	 * @param newFirst The first value of the new pair
	 * @return A new pair with a shared second value
	 */
	public Pair<T1, T2> withFirst(T1 newFirst)
	{
		return new Pair<>(newFirst, getSecond());
	}
	
	/**
	 * Creates a new pair with the same first value as this
	 * @param newSecond The second value of the new pair
	 * @return A new pair with a shared first value
	 */
	public Pair<T1, T2> withSecond(T2 newSecond)
	{
		return new Pair<>(getFirst(), newSecond);
	}
}
