package flow_recording;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * IDGenerator generates locally unique ids.
 * 
 * @author Mikko Hilpinen
 * @since 26.11.2014
 */
public class IDGenerator
{
	// ATTRIBUTES	--------------------------------------
	
	/**
	 * A string in the beginning of every ID
	 */
	public static final String ID_INDICATOR = "#";
	
	private List<String> ids;
	
	
	// CONSTRUCTOR	--------------------------------------
	
	/**
	 * Creates a new generator
	 */
	public IDGenerator()
	{
		this.ids = new ArrayList<>();
	}

	
	// OTHER METHODS	----------------------------------
	
	/**
	 * Generates a random ID that isn't in use in this context yet
	 * @return A randomly generated (locally) unique id
	 */
	public String generateID()
	{
		Random r = new Random();
		String id = ID_INDICATOR + Long.toString(Math.abs(r.nextLong()), 36);
		
		if (this.ids.contains(id))
			return generateID();
		else
			return id;
	}
	
	/**
	 * Reserves an id so that it won't get generated
	 * @param id The id that is wouldn't be unique if it was generated
	 */
	public void reserveID(String id)
	{
		if (!this.ids.contains(id))
			this.ids.add(id);
	}
}
