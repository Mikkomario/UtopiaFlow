package utopia.java.flow.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.Collectors;

import utopia.java.flow.structure.iterator.RichIterator;
import utopia.java.flow.structure.iterator.StringCharIterator;
import utopia.java.flow.structure.AppendableSequence;
import utopia.java.flow.structure.ImmutableList;
import utopia.java.flow.structure.Option;
import utopia.java.flow.structure.RichComparable;
import utopia.java.flow.structure.RichIterable;
import utopia.java.flow.structure.Try;

/**
 * This class can be used for providing more features to basic strings
 * @author Mikko Hilpinen
 * @since 20.9.2018
 */
public class RichString implements RichIterable<Character>, StringRepresentable,
        AppendableSequence<Character, RichString, RichStringBuilder>, RichComparable<RichString>
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
	
	/**
	 * @param stream Source stream
	 * @param charset The source encoding
	 * @return The contents of the stream as a string
	 */
	public static Try<RichString> fromStream(InputStream stream, Charset charset)
	{
		try (InputStreamReader streamReader = new InputStreamReader(stream, charset))
		{
			try (BufferedReader bufferedReader = new BufferedReader(streamReader))
			{
				return Try.success(of(bufferedReader.lines().collect(Collectors.joining("\n"))));
			}
		}
		catch (Exception e)
		{
			return Try.failure(e);
		}
		
		// return Try.run(() -> of(new String(stream.readAllBytes(), charset)));
	}
	
	/**
	 * @param filePath A file path
	 * @param charset The file encoding
	 * @return The contents of the file as a string
	 */
	public static Try<RichString> fromFile(Path filePath, Charset charset)
	{
		return Try.run(() -> of(new String(Files.readAllBytes(filePath), charset)));
	}

	
	// IMPLEMENTED	-----------------

	@Override
	public RichStringBuilder newBuilder(Option<Integer> capacity)
	{
		return new RichStringBuilder(capacity);
	}

	@Override
	public RichString self()
	{
		return this;
	}
	
	@Override
	public boolean isEmpty()
	{
		return s.isEmpty();
	}
	
	@Override
	public boolean nonEmpty()
	{
		return !isEmpty();
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
		return Objects.hashCode(s);
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
		return s.equals(other.s);
	}
	
	@Override
	public RichString description()
	{
		return this;
	}
	
	@Override
	public int compareTo(RichString o)
	{
		return s.compareTo(o.s);
	}
	
	@Override
	public int size()
	{
		return s.length();
	}

	@Override
	public Character get(int index) throws IndexOutOfBoundsException
	{
		return s.charAt(index);
	}
	
	
	// OTHERS	--------------------

	/**
	 * @return The length of this string (number of characters)
	 */
	public int length()
	{
		return size();
	}
	
	/**
	 * Checks whether this string contains the specified string
	 * @param another Searched string
	 * @param caseSensitive Whether search should be case-sensitive (default = true)
	 * @return Whether this string contains the specified string
	 */
	public boolean contains(String another, boolean caseSensitive)
	{
		if (caseSensitive)
			return s.contains(another);
		else
			return s.toLowerCase().contains(another.toLowerCase());
	}
	
	/**
	 * Checks whether this string contains the specified string (case-sensitive)
	 * @param another Searched string
	 * @return Whether this string contains the specified string
	 */
	public boolean contains(String another)
	{
		return contains(another, true);
	}
	
	/**
	 * Checks whether this string contains all of the specified strings
	 * @param strings Searched strings
	 * @param caseSensitive Whether search should be case-sensitive (default = true)
	 * @return Whether this string containsl all of the specified strings
	 */
	public boolean containsAll(RichIterable<? extends String> strings, boolean caseSensitive)
	{
		return strings.forAll(s -> contains(s, caseSensitive));
	}
	
	/**
	 * Checks whether this string contains all of the specified strings (case-sensitive)
	 * @param first First string to search
	 * @param second Second string to search
	 * @param more More strings to search
	 * @return Whether this string containsl all of the specified strings
	 */
	public boolean containsAll(String first, String second, String... more)
	{
		return containsAll(ImmutableList.withValues(first, second, more), true);
	}
	
	/**
	 * @param str Searched string
	 * @param caseSensitive Whether search should be case-sensitive (default = true)
	 * @return Whether this string starts with the specified string
	 */
	public boolean startsWith(String str, boolean caseSensitive)
	{
		if (caseSensitive)
			return s.startsWith(str);
		else
			return s.toLowerCase().startsWith(str.toLowerCase());
	}
	
	/**
	 * @param str Searched string
	 * @return Whether this string starts with the specified string
	 */
	public boolean startsWith(String str)
	{
		return startsWith(str, true);
	}
	
	/**
	 * @param str Searched string
	 * @param caseSensitive Whether search should be case-sensitive (default = true)
	 * @return Whether this string ends with the specified string
	 */
	public boolean endsWith(String str, boolean caseSensitive)
	{
		if (caseSensitive)
			return s.endsWith(str);
		else
			return s.toLowerCase().endsWith(str.toLowerCase());
	}
	
	/**
	 * @param str Searched string
	 * @return Whether this string ends with the specified string
	 */
	public boolean endsWith(String str)
	{
		return endsWith(str, true);
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
	 * @return The lines forming this string
	 */
	public ImmutableList<RichString> lines()
	{
		return split("\\\n");
	}
	
	/**
	 * @return The words in this string, including special characters.
	 */
	public ImmutableList<RichString> words()
	{
		return split(" ").map(RichString::trimmed).filter(s -> !s.isEmpty());
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
		return toDouble().map(Double::intValue);
	}
	
	/**
	 * @param charset The byte encoding used
	 * @return An input stream from the contents of this string
	 */
	public ByteArrayInputStream toInputStream(Charset charset)
	{
		return new ByteArrayInputStream(s.getBytes(charset));
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
	public RichString letters()
	{
		return filter(Character::isLetter);
	}
	
	/**
	 * @return The digits in this string
	 */
	public RichString digits()
	{
		return filter(Character::isDigit);
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
	 * @param str The target string to be found
	 * @return The first index of the string in this string. None if this string doens't contain such 
	 * a string.
	 */
	public Option<Integer> indexOf(String str)
	{
		return Option.positiveInt(s.indexOf(str), true);
	}
	
	/**
	 * @param str The target string to be found
	 * @return The last index of the string in this string. None if this string doens't contain such 
	 * a string.
	 */
	public Option<Integer> lastIndexOf(String str)
	{
		return Option.positiveInt(s.lastIndexOf(str), true);
	}
	
	/**
	 * @param str The target string to be found
	 * @param inclusive Whether the string itself should be included
	 * @return The part of this string after the provided string
	 */
	public RichString afterFirst(String str, boolean inclusive)
	{
		int indexMod = inclusive ? 0 : str.length();
		return indexOf(str).map(i -> dropFirst(i + indexMod)).getOrElse(EMPTY);
	}
	
	/**
	 * @param str The target string to be found
	 * @return The part of this string after the provided string (exclusive)
	 */
	public RichString afterFirst(String str)
	{
		return afterFirst(str, false);
	}
	
	/**
	 * @param str The target string to be found
	 * @param inclusive Whether the string itself should be included
	 * @return The part of this string after the last occurrence of the provided string
	 */
	public RichString afterLast(String str, boolean inclusive)
	{
		int indexMod = inclusive ? 0 : str.length();
		return lastIndexOf(str).map(i -> dropFirst(i + indexMod)).getOrElse(EMPTY);
	}
	
	/**
	 * @param str The target string to be found
	 * @return The part of this string after the provided string (exclusive)
	 */
	public RichString afterLast(String str)
	{
		return afterLast(str, false);
	}
	
	/**
	 * @param str The target string to be found
	 * @param inclusive Whether the string itself should be included
	 * @return The part of this string before the provided string
	 */
	public RichString untilFirst(String str, boolean inclusive)
	{
		int indexMod = inclusive ? str.length() : 0;
		return indexOf(str).map(i -> firstChars(i + indexMod)).getOrElse(this);
	}
	
	/**
	 * @param str The target string to be found
	 * @param inclusive Whether the string itself should be included
	 * @return The part of this string before the last occurrence of the provided string
	 */
	public RichString untilLast(String str, boolean inclusive)
	{
		int indexMod = inclusive ? str.length() : 0;
		return lastIndexOf(str).map(i -> firstChars(i + indexMod)).getOrElse(this);
	}
	
	/**
	 * @param str The target string to be found
	 * @return The part of this string before the provided string
	 */
	public RichString untilFirst(String str)
	{
		return untilFirst(str, false);
	}
	
	/**
	 * @param str The target string to be found
	 * @return The part of this string before the last occurrence of the provided string
	 */
	public RichString untilLast(String str)
	{
		return untilLast(str, false);
	}
	
	/**
	 * @return The first word in this string
	 */
	public RichString firstWord()
	{
		return untilFirst(" ");
	}
	
	/**
	 * @return The last word in this string
	 */
	public RichString lastWord()
	{
		return afterLast(" ");
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
	 * @param another Another string
	 * @return A combination of these strings
	 */
	public RichString plus(RichString another)
	{
		return plus(another.s);
	}
	
	/**
	 * @param item an item that can be represented as a string
	 * @return A combination of this string and the item description
	 */
	public RichString plus(StringRepresentable item)
	{
		return plus(item.description().s);
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
