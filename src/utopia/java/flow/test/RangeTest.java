package utopia.java.flow.test;

import java.time.LocalDate;

import utopia.java.flow.structure.ImmutableList;
import utopia.java.flow.structure.range.ExclusiveIntRange;
import utopia.java.flow.structure.range.InclusiveDateRange;
import utopia.java.flow.structure.range.InclusiveIntRange;
import utopia.java.flow.structure.range.IntRange;
import utopia.java.flow.util.Test;

/**
 * This test checks basic functions of range class
 * @author Mikko Hilpinen
 * @since 25.7.2018
 */
public class RangeTest
{
	@SuppressWarnings("javadoc")
	public static void main(String[] args)
	{
		InclusiveIntRange r1 = IntRange.inclusive(1, 3);
		ExclusiveIntRange r2 = IntRange.exclusive(5, 13);
		
		// Test.check(r1.isSmallerThan(r2));
		Test.checkEquals(r1.toList(), ImmutableList.withValues(1, 2, 3));
		Test.checkEquals(r2.length(), 8);
		
		ImmutableList<Character> chars = ImmutableList.of("koira");
		
		Test.checkEquals(chars.get(r1), ImmutableList.of("oir"));
		
		InclusiveDateRange r3 = new InclusiveDateRange(LocalDate.now(), LocalDate.now().plusDays(3));
		InclusiveDateRange r4 = new InclusiveDateRange(LocalDate.now().plusDays(1), LocalDate.now().plusDays(2));
		InclusiveDateRange r5 = new InclusiveDateRange(LocalDate.now().plusDays(4), LocalDate.now().plusDays(6));
		
		Test.checkEquals(r3.length(), 3);
		Test.check(r3.contains(r4));
		Test.check(r3.overlapsWith(r4));
		Test.check(r4.overlapsWith(r3));
		Test.checkEquals(r3.overlapLength(r4).get(), 1);
		Test.checkEquals(r3.distanceFrom(r5).get(), 1);
		
		System.out.println("Success!");
	}
}
