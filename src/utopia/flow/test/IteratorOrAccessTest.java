package utopia.flow.test;

import utopia.flow.structure.ImmutableList;
import utopia.flow.structure.range.IntRange;

/**
 * Tests performance of iterator vs index access
 * @author Mikko Hilpinen
 * @since 11.9.2019
 */
public class IteratorOrAccessTest
{
	@SuppressWarnings("javadoc")
	public static void main(String[] args)
	{
		ImmutableList<Object> list = ImmutableList.filledWith(100000, new Object());
		
		long totalNanos = IntRange.exclusive(0, 1000).fold(0l, (total, i) -> 
		{
			long startNanos = System.nanoTime();
			
			// Test 1: Iterating with iterator
			// list.forEach(o -> {});
			
			// Test 2: Iterating with get(index)
			list.indices().forEach(index -> {});
			
			// Test 3: For loop
			// for (int index = 0; i < list.size(); i++) { list.get(index); }
			
			return total + (System.nanoTime() - startNanos);
		});
		
		System.out.println("Total time in nanosecons: " + totalNanos);
	}
}
