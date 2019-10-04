package utopia.java.flow.test;

import utopia.java.flow.structure.IntSet;
import utopia.java.flow.structure.range.IntRange;
import utopia.java.flow.util.Test;

/**
 * Tests IntSet
 * @author Mikko Hilpinen
 * @since 7.6.2019
 */
public class IntSetTest
{
	@SuppressWarnings("javadoc")
	public static void main(String[] args)
	{
		IntSet a = IntSet.withValues(2, 3, 5);
		System.out.println(a);
		
		Test.checkEquals(a.size(), 3);
		Test.checkEquals(a.ranges().size(), 2);
		Test.checkEquals(a.ranges().head(), IntRange.inclusive(2, 3));
		Test.check(a.contains(2));
		Test.check(a.contains(3));
		Test.check(a.contains(5));
		Test.check(!a.contains(4));
		
		IntSet b = a.plus(4);
		System.out.println(b);
		
		Test.checkEquals(b.size(), 4);
		Test.checkEquals(b.ranges().size(), 1);
		Test.checkEquals(b.ranges().head(), IntRange.inclusive(2, 5));
		Test.check(b.contains(2));
		Test.check(b.contains(3));
		Test.check(b.contains(4));
		Test.check(b.contains(5));
		Test.check(!b.contains(6));
		Test.check(!b.contains(1));
		Test.check(!b.contains(123));
		
		IntSet c = b.minus(3);
		System.out.println(c);
		
		Test.checkEquals(c.size(), 3);
		Test.checkEquals(c.ranges().size(), 2);
		Test.checkEquals(c.ranges().head(), IntRange.inclusive(2, 2));
		
		Test.checkEquals(a.minus(10), a);
		
		IntSet d = a.plus(5, 6, 7, 8, 10);
		System.out.println(d);
		
		Test.checkEquals(d.size(), 7);
		Test.checkEquals(d.ranges().size(), 3);
		
		System.out.println("Success!");
	}
}
