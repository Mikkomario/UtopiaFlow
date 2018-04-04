package utopia.flow.util;

/**
 * This filter is able to filter strings from each other
 * @author Mikko Hilpinen
 * @since 27.4.2016
 * @deprecated Please use Java 8 filters instead
 */
public class StringFilter implements Filter<String>
{
	// ATTRIBUTES	-------------
	
	private boolean caseSensitive;
	private boolean like;
	private String target;
	
	
	// CONSTRUCTOR	------------
	
	/**
	 * Creates a new case-insensitive filter
	 * @param target The string that is searched
	 */
	public StringFilter(String target)
	{
		this.target = target;
		this.caseSensitive = false;
		this.like = false;
	}
	
	/**
	 * Creates a new string filter
	 * @param target The string that is searched
	 * @param caseSensitive Should the filtering be case-sensitive
	 * @param startsWith Is it enough if a string starts with the target string
	 */
	public StringFilter(String target, boolean caseSensitive, boolean startsWith)
	{
		this.target = target;
		this.caseSensitive = caseSensitive;
		this.like = startsWith;
	}
	
	
	// IMPLEMENTED METHODS	----

	@Override
	public boolean includes(String e)
	{
		if (this.target == null)
			return e == null;
		else if (e == null)
			return false;
		
		if (this.like)
		{
			if (this.caseSensitive)
				return e.startsWith(this.target);
			else
				return e.toLowerCase().startsWith(this.target.toLowerCase());
		}
		else if (this.caseSensitive)
			return e.equals(this.target);
		else
			return e.equalsIgnoreCase(this.target);
	}
}
