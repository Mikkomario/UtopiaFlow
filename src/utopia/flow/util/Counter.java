package utopia.flow.util;

/**
 * A counter is used for counting numbers. The counter will count until integer maximum value
 * @author Mikko Hilpinen
 * @since 14.5.2018
 */
public class Counter extends Generator<Integer>
{
	/**
	 * Creates a new counter
	 * @param firstNumber The first number that will be given
	 * @param increment The increment between iterations
	 */
	public Counter(int firstNumber, int increment)
	{
		super(Lazy.wrap(firstNumber), i -> 
		{
			if (i <= Integer.MAX_VALUE - increment)
				return Option.some(i + increment);
			else
				return Option.none();
		});
	}
}
