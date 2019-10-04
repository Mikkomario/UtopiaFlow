package utopia.java.flow.structure.range;

import java.util.Objects;

/**
 * A range that includes both start and end point
 * @author Mikko Hilpinen
 * @since 11.9.2019
 * @param <A> Type of compared item
 */
public class InclusiveRange<A extends Comparable<? super A>> implements DefinedRange<A>
{
	// ATTRIBUTES	-------------------
	
	private A first;
	private A last;
	
	
	// CONSTRUCTOR	-------------------
	
	/**
	 * @param first The first value inside this range
	 * @param last The last value inside this range
	 */
	public InclusiveRange(A first, A last)
	{
		this.first = first;
		this.last = last;
	}
	
	
	// IMPLEMENTED	-------------------

	@Override
	public A first()
	{
		return first;
	}

	@Override
	public A last()
	{
		return last;
	}

	@Override
	public String toString()
	{
		return first + "-" + last;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(first, last);
	}


	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof InclusiveRange))
			return false;
		InclusiveRange<?> other = (InclusiveRange<?>) obj;
		return Objects.equals(first, other.first) && Objects.equals(last, other.last);
	}
}
