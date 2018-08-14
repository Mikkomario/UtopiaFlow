package utopia.flow.test;

import utopia.flow.structure.Option;
import utopia.flow.util.Test;

class OptionTest
{
	public static void main(String[] args)
	{
		Option<Integer> i = Option.some(12);
		
		Test.check(i.isDefined());
		Test.check(Option.none().isEmpty());
		Test.check(i.orElse(Option.none()).isDefined());
		Test.check(i.getOrElse(1).equals(12));
		Test.check(Option.none().getOrElse(99).equals(99));
		
		Option<String> s = i.map(n -> n + "a");
		
		Test.check(s.isDefined());
		Test.check(s.get().equals("12a"));
		Test.check(s.valueEquals("12a"));
		
		Test.check(i.flatMap(a -> Option.none()).isEmpty());
		Test.check(i.flatMap(a -> Option.some(a)).valueEquals(12));
		Test.check(i.exists(a -> a > 10));
		Test.check(!i.exists(a -> a < 10));
		Test.check(i.forAll(a -> a > 10));
		Test.check(Option.none().forAll(a -> a.equals("a")));
		
		Test.check(i.filter(a -> a < 12).isEmpty());
		Test.check(i.filter(a -> a > 10).equals(i));
		
		Test.check(Option.max(Option.some(2), Option.none(), Option.some(5), Option.none(), Option.some(1)).contains(5));
		
		System.out.println("Success!");
	}
}
