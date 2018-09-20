package utopia.flow.util;

import utopia.flow.structure.ImmutableList;
import utopia.flow.structure.IntRange;
import utopia.flow.structure.Option;
import utopia.flow.structure.Range;
import utopia.flow.structure.RichIterable;
import utopia.flow.structure.RichIterator;
import utopia.flow.structure.StringCharIterator;
import utopia.flow.structure.Try;

/**
 * This class can be used for providing more features to basic strings
 * @author Mikko Hilpinen
 * @since 20.9.2018
 */
public class RichString implements RichIterable<Character>
{
	// ATTRIBUTES	-----------------
	
	/**
	 * The empty string
	 */
	public static final RichString EMPTY = new RichString("");
	
	private final String s;
	
	
	// CONSTRUCTOR	-----------------
	
	/**
	 * Wraps a string into a rich string
	 * @param s A string
	 */
	public RichString(String s)
	{
		if (s == null)
			this.s = "";
		else
			this.s = s;
	}
	
	/**
	 * Wraps a character list to a rich string
	 * @param chars The characters that form the string
	 * @return A new string
	 */
	public static RichString of(Iterable<? extends Character> chars)
	{
		StringBuilder s = new StringBuilder();
		chars.forEach(s::append);
		return new RichString(s.toString());
	}
	
	/**
	 * Wraps a string into a rich string
	 * @param s A string
	 * @return a new string
	 */
	public static RichString of(String s)
	{
		return new RichString(s);
	}
	
	/**
	 * Merges multiple strings into one
	 * @param strings the strings
	 * @return a single string from the strings
	 */
	public static RichString merge(Iterable<? extends String> strings)
	{
		StringBuilder s = new StringBuilder();
		strings.forEach(s::append);
		return new RichString(s.toString());
	}
	
	/**
	 * Merges multiple strings into one
	 * @param strings the strings
	 * @param separator A separator placed between the strings
	 * @return A single string from the strings + separators
	 */
	public static RichString merge(ImmutableList<String> strings, String separator)
	{
		if (strings.isEmpty())
			return EMPTY;
		else
			return new RichString(strings.reduce((a, b) -> a + separator + b));
	}

	
	// IMPLEMENTED	-----------------

	@Override
	public boolean isEmpty()
	{
		return s.isEmpty();
	}
	
	@Override
	public String toString()
	{
		return s;
	}
	
	@Override
	public RichIterator<Character> iterator()
	{
		return new StringCharIterator(s);
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((s == null) ? 0 : s.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (obj instanceof String)
			return obj.equals(s);
		if (!(obj instanceof RichString))
			return false;
		RichString other = (RichString) obj;
		if (s == null)
		{
			if (other.s != null)
				return false;
		}
		else if (!s.equals(other.s))
			return false;
		return true;
	}
	
	
	// OTHERS	--------------------

	/**
	 * @return The length of this string (number of characters)
	 */
	public int length()
	{
		return s.length();
	}
	
	/**
	 * @return Upper case version of this string
	 */
	public RichString toUpperCase()
	{
		return of(s.toUpperCase());
	}
	
	/**
	 * @return Lower case version of this string
	 */
	public RichString toLowerCase()
	{
		return of(s.toLowerCase());
	}
	
	/**
	 * @return A capitalized version of this string
	 */
	public RichString capitalized()
	{
		if (isEmpty())
			return this;
		return firstChar().toUpperCase().plus(tail().toLowerCase());
	}
	
	/**
	 * @return A version of this string with leading and trailing whitespaces / newlines removed
	 */
	public RichString trimmed()
	{
		return of(s.trim());
	}
	
	/**
	 * @param regex A regex
	 * @param replacement Replacement string
	 * @return A string with parts replaced
	 */
	public RichString replaceAll(String regex, String replacement)
	{
		return of(s.replaceAll(regex, replacement));
	}
	
	/**
	 * @param regex A split regex
	 * @return Split parts of this string
	 */
	public ImmutableList<RichString> split(String regex)
	{
		return ImmutableList.of(s.split(regex)).map(RichString::of);
	}
	
	/**
	 * @return A normal string from this rich string. None if empty.
	 */
	public Option<String> toStringNotEmpty()
	{
		if (isEmpty())
			return Option.none();
		else
			return Option.some(s);
	}
	
	/**
	 * @return A double number from this string. None if the number couldn't be parsed
	 */
	public Option<Double> toDouble()
	{
		return Try.run(() -> Double.parseDouble(s)).success();
	}
	
	/**
	 * @return An integer number from this string. None if the number couldn't be parsed. Allows double format.
	 */
	public Option<Integer> toInt()
	{
		return toDouble().map(d -> d.intValue());
	}
	
	/**
	 * @return The characters in this string
	 */
	public ImmutableList<Character> characters()
	{
		return view().force();
	}
	
	/**
	 * @return The basic letters in this string
	 */
	public ImmutableList<Character> letters()
	{
		return characters().filter(Character::isLetter);
	}
	
	/**
	 * @return The digits in this string
	 */
	public ImmutableList<Character> digits()
	{
		return characters().filter(Character::isDigit);
	}
	
	/**
	 * @param n The number of characters included
	 * @return The first n characters of this string as a string
	 */
	public RichString firstChars(int n)
	{
		return of(first(n));
	}
	
	/**
	 * @param n The number of characters included
	 * @return The last n characters of this string as a string
	 */
	public RichString lastChars(int n)
	{
		return of(characters().last(n));
	}
	
	/**
	 * @return The first character of this string as a string
	 */
	public RichString firstChar()
	{
		return firstChars(1);
	}
	
	/**
	 * @return The first character of this string as a string. None if this string is empty.
	 */
	public Option<RichString> firstCharOption()
	{
		if (isEmpty())
			return Option.none();
		else
			return Option.some(firstChar());
	}
	
	/**
	 * @return The tail (all but the first character) of this string as a string
	 */
	public RichString tail()
	{
		return of(characters().tail());
	}
	
	/**
	 * @return The tail (all but the first character) of this string as a string. None if this string is 0-1 characters
	 */
	public Option<RichString> tailOption()
	{
		if (length() > 1)
			return Option.some(tail());
		else
			return Option.none();
	}
	
	/**
	 * @param n The amount of characters to drop
	 * @return A version of this string with first n characters removed
	 */
	public RichString dropFirst(int n)
	{
		return of(characters().dropFirst(n));
	}
	
	/**
	 * @param n The amount of characters to drop
	 * @return A version of this string with last n characters removed
	 */
	public RichString dropLast(int n)
	{
		return of(characters().dropLast(n));
	}
	
	/**
	 * @param range The target range
	 * @return The part of this string that overlaps with the provided range
	 */
	public RichString range(Range<? extends Integer> range)
	{
		return of(characters().get(range));
	}
	
	/**
	 * @param startInclusive The first included character index
	 * @param endInclusive The last included character index
	 * @return The part of this string that overlaps with the provided range
	 */
	public RichString range(int startInclusive, int endInclusive)
	{
		return range(new IntRange(startInclusive, endInclusive));
	}
	
	/**
	 * @param s Another string
	 * @return A combination of these two strings
	 */
	public RichString plus(String s)
	{
		return of(this.s + s);
	}
	
	/**
	 * @param other Another string
	 * @return A combination of these two strings
	 */
	public RichString plus(RichString other)
	{
		return plus(other.s);
	}
	
	/**
	 * @param first The first string to add
	 * @param second The second string to add
	 * @param more More strings to add
	 * @return A combination of all of these strings
	 */
	public RichString plus(String first, String second, String... more)
	{
		return plus(merge(ImmutableList.withValues(first, second, more)));
	}
}
