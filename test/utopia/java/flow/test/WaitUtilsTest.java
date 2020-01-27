package utopia.java.flow.test;

import java.time.LocalDateTime;

import utopia.java.flow.util.WaitUtils;

/**
 * This is a simple test for waitUtils.waitUntil
 * @author Mikko Hilpinen
 * @since 4.6.2018
 */
public class WaitUtilsTest
{
	// CONSTRUCTOR	------------------
	
	private WaitUtilsTest() { }

	
	// MAIN METHODS	------------------
	
	/**
	 * @param args Not used
	 */
	public static void main(String[] args)
	{
		Object a = "";
		
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime until = now.plusSeconds(7);
		
		System.out.println("Time before wait: " + now);
		WaitUtils.waitUntil(until, a);
		System.out.println("Time after wait: " + LocalDateTime.now());
		System.out.println("Expected: " + until);
	}
}
