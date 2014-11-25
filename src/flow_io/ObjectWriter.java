package flow_io;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * ObjectWriters write writable objects into different formats
 * 
 * @author Mikko Hilpinen
 * @since 25.11.2014
 */
public class ObjectWriter
{
	// ATTRIBUTES	--------------------------------------
	
	private Map<Writable, String> writableIDs;
	
	
	// CONSTRUCTOR	--------------------------------------
	
	/**
	 * Creates a new writer
	 */
	protected ObjectWriter()
	{
		this.writableIDs = new HashMap<>();
	}
	
	
	// OTHER METHODS	----------------------------------
	
	/**
	 * Returns an id representing the given writable. If the writable wasn't registered before, 
	 * a new id will be generated.
	 * 
	 * @param writable The writable that needs an id.
	 * @return The unique id representing the given writable.
	 */
	protected String getIDForWritable(Writable writable)
	{
		// If the writable wasn't previously introduced, generates an id for it
		if (!this.writableIDs.containsKey(writable))
			this.writableIDs.put(writable, generateID());
		
		return this.writableIDs.get(writable);
	}
	
	/**
	 * Generates a random ID that isn't in use in this constructor yet
	 * @return A randomly generated id
	 */
	private String generateID()
	{
		Random r = new Random();
		String id = AbstractConstructor.ID_INDICATOR + Long.toString(Math.abs(r.nextLong()), 36);
		
		if (this.writableIDs.containsValue(id))
			return generateID();
		else
			return id;
	}
}
