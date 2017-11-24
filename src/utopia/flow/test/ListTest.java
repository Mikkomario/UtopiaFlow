package utopia.flow.test;

import utopia.flow.structure.ImmutableList;

class ListTest
{
	public static void main(String[] args)
	{
		ImmutableList<Integer> numbers = ImmutableList.withValues(1, 2, 3, 4, 5, 5, 6, 6, 2);
		ImmutableList<String> letters = ImmutableList.withValues("a", "b", "c", "d", "e");
		
		assert numbers.indexOf(6).isEmpty();
		assert numbers.indexOf(2).get() == 1;
		assert letters.indexOf("x").isEmpty();
		assert letters.indexOf("c").get() == 2;
		
		assert numbers.distinct().equals(ImmutableList.withValues(1, 2, 3, 4, 5, 6));
		
		System.out.println("Success!");
	}
}
