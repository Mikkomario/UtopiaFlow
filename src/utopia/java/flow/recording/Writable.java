package utopia.java.flow.recording;

import java.util.Map;

/**
 * Writable objects can be written in text form. Writable interface is often used with 
 * Constructable interface.
 * 
 * @author Mikko Hilpinen
 * @since 25.11.2014
 * @deprecated Replaced with new generic classes and xml element parsing
 */
public interface Writable
{
	/**
	 * @return The attribute key value pairs that define this writable
	 */
	public Map<String, String> getAttributes();
	
	/**
	 * @return The link key value pairs that define this object's connection to others of 
	 * its kind. The returned writables should be of same type as this writable or at least 
	 * constructed by the same constructor.
	 */
	public Map<String, Writable> getLinks();
}
