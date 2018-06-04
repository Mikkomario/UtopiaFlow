package utopia.flow.util;

import utopia.flow.structure.ImmutableList;

/**
 * This class is used for testing some list functions
 * @author Mikko Hilpinen
 * @since 4.6.2018
 */
public class ListTest2 extends Test
{
	private ListTest2() { }

	@SuppressWarnings("javadoc")
	public static void main(String[] args)
	{
		ImmutableList<Integer> numbers = ImmutableList.withValues(1, 2, 3, 4, 5, 6, 7, 8);
		checkEquals(numbers.size(), 8);
		
		checkEquals(numbers.takeWhile(i -> i < 5).size(), 4);
		
		System.out.println("DONE");
	}
}
