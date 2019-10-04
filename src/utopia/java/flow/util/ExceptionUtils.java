package utopia.java.flow.util;

import utopia.java.flow.structure.ImmutableList;
import utopia.java.flow.structure.Option;

/**
 * Static class that offers utility methods related to exception / error handling
 * @author Mikko Hilpinen
 * @since 5.9.2019
 */
public class ExceptionUtils
{
	private ExceptionUtils() { }
	
	/**
	 * Collects exception stack trace
	 * @param e Exception
	 * @return Collected stack
	 */
	public static String stackTraceFrom(Exception e)
	{
		return stackTraceFrom(e, Option.none());
	}
	
	/**
	 * Collects exception stack trace
	 * @param e Exception
	 * @param maxLength Maximum length of collected stack (optional)
	 * @return Collected stack
	 */
	public static String stackTraceFrom(Exception e, int maxLength)
	{
		return stackTraceFrom(e, Option.some(maxLength));
	}
	
	/**
	 * Collects exception stack trace
	 * @param e Exception
	 * @param maxLength Maximum length of collected stack (optional)
	 * @return Collected stack
	 */
	public static String stackTraceFrom(Exception e, Option<Integer> maxLength)
	{
		ImmutableList<StackTraceElement> stack = stackTraceElementsFrom(e).filter(s -> !s.isNativeMethod());
		
		StringBuilder s = new StringBuilder();
		if (e.getMessage() != null)
			s.append(e.getMessage());
		
		int currentLength = s.length();
		for (StackTraceElement stackElement : stack)
		{
			String newPart = "\n" + stackElement.toString();
			int newPartLength = newPart.length();
			
			if (maxLength.isEmpty() || currentLength + newPartLength < maxLength.get())
			{
				s.append(newPart);
				currentLength += newPartLength;
			}
			else
				break;
		}
		
		return s.toString();
	}
	
	private static ImmutableList<StackTraceElement> stackTraceElementsFrom(Exception e)
	{
		return ImmutableList.of(e.getStackTrace()).plus(
				causeFor(e).handleMap(ExceptionUtils::stackTraceElementsFrom, ImmutableList::empty));
	}
	
	private static Option<Exception> causeFor(Exception e)
	{
		Throwable cause = e.getCause();
		if (cause != null && cause instanceof Exception)
			return Option.some((Exception) cause);
		else
			return Option.none();
	}
}
