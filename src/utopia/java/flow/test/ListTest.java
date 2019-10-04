package utopia.java.flow.test;

import utopia.java.flow.structure.Duo;
import utopia.java.flow.structure.ImmutableList;
import utopia.java.flow.structure.ImmutableMap;
import utopia.java.flow.structure.ListBuilder;
import utopia.java.flow.util.Test;

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
		Test.checkEquals(groupedWords.size(), 4);
		Test.checkEquals(groupedWords.get(1).size(), 3);
		Test.checkEquals(groupedWords.get(3).size(), 2);
		Test.checkEquals(groupedWords.get(4).size(), 2);
		Test.checkEquals(groupedWords.get(5).size(), 4);
		Test.check(groupedWords.getOption(2).isEmpty());
		
		ImmutableList<String> words3 = ImmutableList.withValues("ant", "abc", "y", "aakkonen");
		
		ImmutableList<String> sortedByLetters = words3.sorted();
		ImmutableList<String> sortedByLength = words3.sortedBy(s -> s.length());
		
		ImmutableList<String> sortedByLengthAndLetters = words3.sortedWith(ImmutableList.withValues(
				(a, b) -> a.length() - b.length() , (a, b) -> a.compareTo(b)));
		
		System.out.println(sortedByLetters);
		System.out.println(sortedByLength);
		System.out.println(sortedByLengthAndLetters);
		Test.checkEquals(sortedByLetters.head(), "aakkonen");
		Test.check(sortedByLetters.last() == "y");
		Test.checkEquals(sortedByLength.head(), "y");
		Test.check(sortedByLength.last() == "aakkonen");
		Test.checkEquals(sortedByLengthAndLetters, ImmutableList.withValues("y", "abc", "ant", "aakkonen"));
		
		Test.checkEquals(words3.dropWhile(s -> s.length() == 3).size(), 2);
		
		System.out.println(words);
		System.out.println(words.first(3));
		System.out.println(words.dropFirst(3));
		
		ImmutableMap<Boolean, ImmutableList<String>> dividedWords = words.divideBy(w -> w.length() > 3);
		
		Test.checkEquals(dividedWords.get(true).size(), 5);
		Test.checkEquals(dividedWords.get(false).size(), 3);
		
		Duo<ImmutableList<String>> split1 = words3.splitAt(w -> !w.startsWith("a"));
		
		Test.checkEquals(split1.first().size(), 2);
		Test.checkEquals(split1.second().size(), 2);
		Test.checkEquals(split1.second().head(), "y");
		Test.checkEquals(split1, words3.splitAt(2));
		
		// Tests new map style
		ImmutableList<Integer> mapResult = words3.map(w -> w.length(), ListBuilder::new);
		Test.checkEquals(mapResult, ImmutableList.withValues(3, 3, 1, 8));
		
		// Tests recursive dropping
		System.out.println(iter(numbers));
		
		System.out.println("Success!");
	}
	
	private static ImmutableList<Integer> iter(ImmutableList<Integer> remaining)
	{
		System.out.println(remaining);
		if (remaining.size() < 2)
			return remaining;
		else
		{
			// Tests all possible combinations
			for (int i : remaining.indices())
			{
				int first = remaining.get(i);
				for (int second : remaining.dropFirst(i + 1))
				{
					int result = first + second;
					
					// Will recursively repeat whole process every time a connection is made
					ImmutableList<Integer> newData = remaining.dropIndex(i).without(second).plus(result);
					// System.out.println("Combination successful: Now at " + newData.size() + " items");
					return iter(newData);
				}
			}
			return remaining;
		}
	}
}
