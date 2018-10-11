package utopia.flow.test;

import utopia.flow.structure.ImmutableList;
import utopia.flow.structure.ImmutableMap;
import utopia.flow.util.Test;

class ListTest
{
	public static void main(String[] args)
	{
		ImmutableList<Integer> numbers = ImmutableList.withValues(1, 2, 3, 4, 5, 5, 6, 6, 2);
		ImmutableList<String> letters = ImmutableList.withValues("a", "b", "c", "d", "e");
		
		Test.check(numbers.indexOf(10).isEmpty());
		Test.checkEquals(numbers.indexOf(2).get(), 1);
		Test.check(letters.indexOf("x").isEmpty());
		Test.checkEquals(letters.indexOf("c").get(), 2);
		
		Test.checkEquals(numbers.distinct(), ImmutableList.withValues(1, 2, 3, 4, 5, 6));
		
		ImmutableList<String> words = ImmutableList.withValues("b", "asd", "moi", "test", "koko", "asema", "tuhti", "omena");
		ImmutableMap<Integer, ImmutableList<String>> groupedWords = words.groupBy(w -> w.length());
		
		System.out.println(words);
		System.out.println(groupedWords);
		Test.checkEquals(groupedWords.size(), 4);
		
		// TODO: Change to use the test class
		Test.checkEquals(groupedWords.get(1).size(), 1);
		Test.checkEquals(groupedWords.get(3).size(), 2);
		Test.checkEquals(groupedWords.get(4).size(), 2);
		Test.checkEquals(groupedWords.get(5).size(), 3);
		Test.check(groupedWords.getOption(2).isEmpty());
		Test.checkEquals(groupedWords.get(3), ImmutableList.withValues("asd", "moi"));
		
		Test.checkEquals(groupedWords.get(5).size(), words.count(w -> w.length() == 5));
		
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
		
		Test.checkEquals(words3.dropWhile(s -> s.length() == 3).size(), 2);
		
		System.out.println(words);
		System.out.println(words.first(3));
		System.out.println(words.dropFirst(3));
		
		System.out.println("Success!");
	}
}
