package utopia.flow.structure.range;

import java.util.function.Function;

import utopia.flow.structure.View;
import utopia.flow.structure.Viewable;

/**
 * A range with no items
 * @author Mikko Hilpinen
 * @since 11.9.2019
 * @param <A> Type of item for this range
 */
public class EmptyRange<A extends Comparable<? super A>> implements Range<A>, Viewable<A>
{
	// IMPLEMENTED	--------------
	
	@Override
	public int direction()
	{
		return 0;
	}

	@Override
	public boolean isEmpty()
	{
		return true;
	}

	@Override
	public boolean contains(A item)
	{
		return false;
	}

	@Override
	public View<A> view(Function<? super A, ? extends A> move)
	{
		return View.empty();
	}

	@Override
	public View<A> view()
	{
		return View.empty();
	}
	
	@Override
	public String toString()
	{
		return "---";
	}
}
