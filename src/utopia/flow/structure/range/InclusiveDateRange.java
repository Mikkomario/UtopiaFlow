package utopia.flow.structure.range;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import utopia.flow.structure.Option;
import utopia.flow.structure.RichIterable;
import utopia.flow.structure.iterator.RangeIterator;
import utopia.flow.structure.iterator.RichIterator;

/**
 * Used for representing date ranges
 * @author Mikko Hilpinen
 * @since 27.9.2019
 */
public class InclusiveDateRange extends InclusiveRange<LocalDate> 
	implements RangeWithLength<LocalDate, Integer>, RichIterable<LocalDate>
{
	// CONSTRUCTOR	------------------
	
	/**
	 * Creates a new inclusive range of dates
	 * @param first The first included date
	 * @param last The last included date
	 */
	public InclusiveDateRange(LocalDate first, LocalDate last)
	{
		super(first, last);
	}
	
	
	// IMPLEMENTED	------------------

	@Override
	public LocalDate increase(LocalDate a, Integer amount)
	{
		return a.plusDays(amount);
	}

	@Override
	public LocalDate decrease(LocalDate a, Integer amount)
	{
		return a.minusDays(amount);
	}

	@Override
	public Integer distanceBetween(LocalDate min, LocalDate max)
	{
		return Math.abs((int) ChronoUnit.DAYS.between(min, max));
	}

	@Override
	public RichIterator<LocalDate> iterator()
	{
		if (isAscending())
			return RangeIterator.inclusive(first(), last(), d -> d.plusDays(1));
		else
			return RangeIterator.inclusive(first(), last(), d -> d.minusDays(1));
	}

	@Override
	public Option<Integer> estimatedSize()
	{
		return Option.some(length());
	}

	@Override
	public boolean isEmpty()
	{
		return super.isEmpty();
	}

	@Override
	public boolean nonEmpty()
	{
		return super.nonEmpty();
	}
}
