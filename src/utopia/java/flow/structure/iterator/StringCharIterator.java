package utopia.java.flow.structure.iterator;

/**
 * This class is used for iterating through characters in a string
 * @author Mikko Hilpinen
 * @since 25.7.2018
 */
public class StringCharIterator implements RichIterator<Character>
{
	// ATTRIBUTES	---------------------
	
	private String target;
	private int nextIndex = 0;
	
	
	// CONSTRUCTOR	---------------------
	
	/**
	 * Creates a new iterator for the target string
	 * @param target the iterated string
	 */
	public StringCharIterator(String target)
	{
		this.target = target;
	}
	
	
	// IMPLEMENTED	---------------------
	
	@Override
	public boolean hasNext()
	{
		return this.nextIndex < this.target.length();
	}

	@Override
	public Character next()
	{
		Character c = poll();
		this.nextIndex ++;
		return c;
	}

	@Override
	public Character poll()
	{
		return this.target.charAt(this.nextIndex);
	}
}
