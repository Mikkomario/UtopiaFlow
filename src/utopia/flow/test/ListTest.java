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
		
		// Tests list map functions as well
		ImmutableList<String> words2 = ImmutableList.withValues("a", "c", "testi");
		groupedWords = ImmutableMap.append(groupedWords, words2.groupBy(w -> w.length()));
		
		System.out.println(groupedWords);
		assert groupedWords.size() == 4;
		assert groupedWords.get(1).size() == 3;
		assert groupedWords.get(3).size() == 2;
		assert groupedWords.get(4).size() == 2;
		assert groupedWords.get(5).size() == 4;
		assert groupedWords.getOption(2).isEmpty();
		
		ImmutableList<String> words3 = ImmutableList.withValues("ant", "abc", "y", "aakkonen");
		
		ImmutableList<String> sortedByLetters = words3.sorted();
		ImmutableList<String> sortedByLength = words3.sortedBy(s -> s.length());
		
		ImmutableList<String> sortedByLengthAndLetters = words3.sortedWith(ImmutableList.withValues(
				(a, b) -> a.length() - b.length() , (a, b) -> a.compareTo(b)));
		
		System.out.println(sortedByLetters);
		System.out.println(sortedByLength);
		System.out.println(sortedByLengthAndLetters);
		assert sortedByLetters.head().equals("aakkonen");
		assert sortedByLetters.last().valueEquals("y");
		assert sortedByLength.head().equals("y");
		assert sortedByLength.last().valueEquals("aakkonen");
		assert sortedByLengthAndLetters.equals(ImmutableList.withValues("y", "abc", "ant", "aakkonen"));
		
		System.out.println("Success!");
	}
}
