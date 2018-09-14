package utopia.flow.test;

import utopia.flow.async.Completion;
import utopia.flow.async.VolatileFlag;
import utopia.flow.util.Test;

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
		
		System.out.println("DONE!");
	}
}
