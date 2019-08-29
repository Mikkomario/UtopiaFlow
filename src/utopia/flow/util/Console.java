package utopia.flow.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Static utility class for reading user input from System.in
 * @author Mikko Hilpinen
 * @since 29.8.2019
 */
public class Console
{
	// CONSTRUCTOR	--------------------
	
	private Console() { }
	
	
	// OTHER	------------------------
	
	/**
	 * Reads console input until a specific condition is met
	 * @param continueCheck A function that checks whether read should continue. This will be called 
	 * before handling each line.
	 * @param lineHandler A function for reading & handling line contents.
	 * @throws IOException If read fails at any point
	 */
	public static void readWhile(Predicate<? super RichString> continueCheck, 
			Consumer<? super RichString> lineHandler) throws IOException
	{
		try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in)))
		{
			while (true)
			{
				RichString input;
				input = RichString.of(br.readLine().toLowerCase());
				if (!continueCheck.test(input))
					break;
				else
					lineHandler.accept(input);
			}
		}
	}
}
