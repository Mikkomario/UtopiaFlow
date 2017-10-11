package utopia.flow.test;

import utopia.flow.util.Option;

class OptionTest
{
	public static void main(String[] args)
	{
		Option<Integer> i = Option.some(12);
		
		assert i.isDefined();
		assert Option.none().isEmpty();
		assert i.orElse(Option.none()).isDefined();
		assert Option.none().orElse(i).isDefined();
		assert i.getOrElse(1).equals(12);
		assert Option.none().getOrElse(99).equals(99);
		
		Option<String> s = i.map(n -> n + "a");
		
		assert s.isDefined();
		assert s.get().equals("12a");
		assert s.valueEquals("12a");
		
		assert i.flatMap(a -> Option.none()).isEmpty();
		assert i.flatMap(a -> Option.some(a)).valueEquals(12);
		assert i.exists(a -> a > 10);
		assert !i.exists(a -> a < 10);
		assert i.forAll(a -> a > 10);
		assert Option.none().forAll(a -> a.equals("a"));
		
		System.out.println("Success!");
	}
}
