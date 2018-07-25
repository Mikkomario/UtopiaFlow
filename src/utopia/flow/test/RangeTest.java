package utopia.flow.test;

import utopia.flow.structure.ImmutableList;
import utopia.flow.structure.IntRange;
import utopia.flow.structure.Range;
import utopia.flow.util.Test;

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
		IntRange r1 = Range.fromTo(1, 3);
		IntRange r2 = Range.fromUntil(5, 13);
		
		Test.check(r1.isSmallerThan(r2));
		Test.checkEquals(r1.toList(), ImmutableList.withValues(1, 2, 3));
		Test.checkEquals(r2.length(), 8);
		
		ImmutableList<Character> chars = ImmutableList.of("koira");
		
		Test.checkEquals(chars.get(r1), ImmutableList.of("oir"));
		
		System.out.println("Done");
	}
}
