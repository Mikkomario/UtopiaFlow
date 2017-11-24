package utopia.flow.test;

import utopia.flow.structure.ImmutableList;
import utopia.flow.structure.ImmutableMap;

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
		
		ImmutableList<String> words = ImmutableList.withValues("b", "asd", "moi", "test", "koko", "asema", "tuhti", "omena");
		ImmutableMap<Integer, ImmutableList<String>> groupedWords = words.groupBy(w -> w.length());
		
		System.out.println(words);
		System.out.println(groupedWords);
		assert groupedWords.size() == 4;
		assert groupedWords.get(1).size() == 1;
		assert groupedWords.get(3).size() == 2;
		assert groupedWords.get(4).size() == 2;
		assert groupedWords.get(5).size() == 3;
		assert groupedWords.getOption(2).isEmpty();
		assert groupedWords.get(3).equals(ImmutableList.withValues("asd", "moi"));
		
		System.out.println("Success!");
	}
}
