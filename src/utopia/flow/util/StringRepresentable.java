package utopia.flow.util;

/**
 * Classes implementing this interface can be represented as strings. Classes implementing this interface should 
 * override toString method
 * @author Mikko Hilpinen
 * @since 1.11.2018
 */
public interface StringRepresentable
{
	/**
	 * @return A string description of this item
	 */
	public default RichString description()
	{
		return RichString.of(toString());
	}
}
