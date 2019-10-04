package utopia.java.flow.test;

import java.time.Duration;

import utopia.flow.async.BackgroundProcessUtils;
import utopia.flow.async.Completion;
import utopia.flow.async.Volatile;
import utopia.flow.async.VolatileFlag;
import utopia.java.flow.structure.range.IntRange;
import utopia.java.flow.util.Test;
import utopia.java.flow.util.WaitUtils;

/**
 * This class is used for testing the volatile class and the volatile flag class
 * @author Mikko Hilpinen
 * @since 14.9.2018
 */
public class VolatileTest
{
	// CONSTRUCTOR	------------------
	
	private VolatileTest() { }

	
	// MAIN	--------------------------
	
	@SuppressWarnings("javadoc")
	public static void main(String[] args)
	{
		VolatileFlag flag = new VolatileFlag();
		
		System.out.println(flag.get());
		
		Completion completion = Completion.ofAsynchronous(() -> 
		{
			flag.runAndSet(() -> 
			{
				System.out.println("FLAG BEING SET");
			});
		});
		
		completion.waitFor();
		flag.runAndSet(() -> 
		{
			System.out.println("SETTING FLAG SECOND TIME");
			Test.fail("Shouldn't be able to set flag twice");
		});
		
		Volatile<Integer> counter = new Volatile<>(0);
		IntRange.exclusive(0, 50).forEach(i -> BackgroundProcessUtils.runInBackground(() ->
		{
			int totalIncrease = 0;
			while (totalIncrease < 50)
			{
				counter.update(old -> 
				{
					System.out.println(i + ": " + (old + 1));
					// WaitUtils.wait(Duration.ofMillis(500), i);
					return old + 1;
				});
				totalIncrease ++;
			}
			
			System.out.println(i + " done");
		}));
		
		System.out.println("Waiting 50 seconds before closing");
		WaitUtils.wait(Duration.ofSeconds(50), new String());
		System.out.println("DONE!");
	}
}
