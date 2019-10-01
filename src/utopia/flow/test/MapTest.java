package utopia.flow.test;

import utopia.flow.structure.Duo;
import utopia.flow.structure.ImmutableList;
import utopia.flow.structure.ImmutableMap;
import utopia.flow.structure.Pair;
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
		
		ImmutableList<Duo<Integer>> i1 = ImmutableList.withValues(1, 2, 3, 4, 5, 6, 7).map(
				i -> new Duo<>(i, i));
		ImmutableList<Duo<Integer>> i2 = ImmutableList.withValues(5, 7, 9, 11, 13).map(
				i -> new Duo<>(i, i * 2));
		ImmutableMap<Integer, ImmutableList<Integer>> timesMap = i1.plus(i2)
				.toListMap(s -> new Pair<>(s.first(), s.second()));
				
		ImmutableList<Duo<Integer>> schedule = timesMap.mapToList((day, times) -> times.size() > 1 ? 
				i1.find(d -> d.first() == day.intValue()).get() : new Duo<>(day, times.head()))
				.sortedBy(d -> d.first());
		
		Test.checkEquals(schedule.map(i -> i.second()), 
				ImmutableList.withValues(1, 2, 3, 4, 5, 6, 7, 18, 22, 26));
		
		System.out.println("DONE");
	}
}
