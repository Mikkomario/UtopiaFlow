package utopia.flow.recording;

import java.util.HashMap;
import java.util.Map;

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
	private IDGenerator idGen;
	
	
	// CONSTRUCTOR	--------------------------------------
	
	/**
	 * Creates a new writer
	 */
	protected ObjectWriter()
	{
		this.writableIDs = new HashMap<>();
		this.idGen = new IDGenerator();
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
			this.writableIDs.put(writable, this.idGen.generateID());
		
		return this.writableIDs.get(writable);
	}
}
