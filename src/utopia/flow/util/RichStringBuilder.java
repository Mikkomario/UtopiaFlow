package utopia.flow.util;

import utopia.flow.structure.Builder;
import utopia.flow.structure.iterator.RichIterator;
import utopia.flow.structure.iterator.StringCharIterator;

/**
 * Used for building rich strings from characters
 * @author Mikko Hilpinen
 * @since 5.9.2019
 */
public class RichStringBuilder extends Builder<RichString, StringBuilder, Character>
{
	// CONSTRUCTOR	-------------------
	
	/**
	 * Creates a new builder
	 */
	public RichStringBuilder()
	{
		super(new StringBuilder());
	}
	
	
	// IMPLEMENTED	------------------

	@Override
	protected RichString newResultFrom(StringBuilder buffer)
	{
		return RichString.of(buffer.toString());
	}

	@Override
	protected StringBuilder copyBuffer(StringBuilder old)
	{
		StringBuilder newBuilder = new StringBuilder();
		newBuilder.append(old.toString());
		return newBuilder;
	}

	@Override
	protected void append(StringBuilder buffer, Character newItem)
	{
		buffer.append(newItem);
	}

	@Override
	protected RichIterator<Character> iteratorFrom(StringBuilder buffer)
	{
		return new StringCharIterator(buffer.toString());
	}
	
	
	// OTHER	-----------------------
	
	/**
	 * Adds a string to this builder
	 * @param str A string to add
	 */
	public void add(String str)
	{
		getBuffer().append(str);
	}
}
