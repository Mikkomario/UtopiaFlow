package utopia.flow.util;

/**
 * Test is used for asserting data in a test environment. The class is completely static.
 * @author Mikko Hilpinen
 * @since 4.6.2018
 */
public class Test
{
	// CONSTRUCTOR	---------------------
	
	/**
	 * Creates a new test
	 */
	protected Test() { }

	
	// OTHER METHODS	-----------------
	
	/**
	 * Checks that the result is true
	 * @param result A result
	 */
	public static void check(boolean result)
	{
		if (!result)
			throw new TestFailedException();
	}
	
	/**
	 * Checks that values equal
	 * @param a first value
	 * @param b second value
	 */
	public static void checkEquals(Object a, Object b)
	{
		if (a instanceof Integer && b instanceof Integer)
		{
			if ((int) a != (int) b)
				throw new TestFailedException("Test Failed: " + a + " == " + b);
		}
		else if (!a.equals(b))
			throw new TestFailedException("Test Failed: " + a + " == " + b);
	}
	
	
	// NESTED CLASSES	----------------
	
	/**
	 * These exceptions are thrown when a test fails
	 * @author Mikko Hilpinen
	 * @since 4.6.2018
	 */
	public static class TestFailedException extends RuntimeException
	{
		private static final long serialVersionUID = -4796651133973823193L;

		/**
		 * Creates an empty exception
		 */
		public TestFailedException() { }
		
		/**
		 * Creates a new exception
		 * @param message Message sent with exception
		 */
		public TestFailedException(String message) 
		{
			super(message);
		}
	}
}
