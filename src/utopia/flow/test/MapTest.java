package utopia.flow.test;

import utopia.flow.structure.ImmutableMap;
import utopia.flow.util.Test;

/**
 * This test makes sure ImmutableMap class is working
 * @author Mikko Hilpinen
 * @since 26.6.2018
 */
public class MapTest
{
	@SuppressWarnings("javadoc")
	public static void main(String[] args)
	{
		ImmutableMap<String, Integer> a = ImmutableMap.empty();
		
		Test.check(a.isEmpty());
		
		a = a.plus("A", 1);
		a = a.plus("B", 2);
		
		Test.check(a.size() == 2);
		Test.checkEquals(a.get("A"), 1);
		Test.checkEquals(a.get("B"), 2);
		Test.check(a.getOption("C").isEmpty());
		Test.check(a.containsKey("A"));
		Test.check(!a.containsKey("C"));
		
		// Tests map merge
		ImmutableMap<String, Integer> b = ImmutableMap.build(m -> 
		{
			m.put("A", 3);
			m.put("C", 5);
		});
		
		ImmutableMap<String, Integer> merged = a.mergedWith(b, (v1, v2) -> v1 + v2);
		
		Test.checkEquals(merged.get("A"), 4);
		Test.checkEquals(merged.get("B"), 2);
		Test.checkEquals(merged.get("C"), 5);
		
		System.out.println("DONE");
	}
}
