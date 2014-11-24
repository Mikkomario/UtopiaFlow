package flow_io;

/**
 * Constructable objects can be constructed piece by piece by object constructors
 * 
 * @author Mikko Hilpinen
 * @param <T> The type of this constructable
 * @since 24.11.2014
 */
public interface Constructable<T extends Constructable<T>>
{
	/**
	 * @return The unique identifier of this constructable
	 */
	public String getID();
	
	/**
	 * Changes the constructable's unique id
	 * @param id The object's new id
	 */
	public void setID(String id);
	
	/**
	 * Adds a new attribute value to the constructable
	 * @param attributeName The name / identifier of the attribute
	 * @param attributeValue The new value the attribute should receive, in string format
	 */
	public void setAttribute(String attributeName, String attributeValue);
	
	/**
	 * Adds a new linked constructable to this constructable
	 * @param linkName The name of the linking attribute
	 * @param target The constructable that will be the link's target
	 */
	public void setLink(String linkName, T target);
}
