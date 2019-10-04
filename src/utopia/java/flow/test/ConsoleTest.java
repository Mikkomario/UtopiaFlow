package utopia.java.flow.test;

import java.io.IOException;

import utopia.java.flow.util.Console;

/**
 * Tests console utility class
 * @author Mikko Hilpinen
 * @since 29.8.2019
 */
public class ConsoleTest
{
	@SuppressWarnings("javadoc")
	public static void main(String[] args)
	{
		System.out.println("I will repeat everything you write until you give an empty line.");
		try
		{
			Console.readWhile(s -> !s.isEmpty(), l -> System.out.println(l));
		}
		catch (IOException e)
		{
			System.err.println("Read failed");
			e.printStackTrace();
		}
		
		System.out.println("Bye!");
	}
}
