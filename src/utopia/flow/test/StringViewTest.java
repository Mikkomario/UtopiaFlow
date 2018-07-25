package utopia.flow.test;

import utopia.flow.structure.ImmutableList;
import utopia.flow.structure.View;
import utopia.flow.util.Test;

/**
 * This class is used for testing string character viewing and related features
 * @author Mikko Hilpinen
 * @since 25.7.2018
 */
public class StringViewTest
{
	@SuppressWarnings("javadoc")
	public static void main(String[] args)
	{
		String s = "koira";
		View<Character> view = View.of(s);
		ImmutableList<Character> list = view.force();
		
		Test.check(list.size() == s.length());
		Test.checkEquals(list, ImmutableList.withValues('k', 'o', 'i', 'r', 'a'));
		Test.checkEquals(view.count(c -> c.equals('i')), 1);
		
		System.out.println("Done");
	}
}
