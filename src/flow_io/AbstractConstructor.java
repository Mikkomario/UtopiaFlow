package flow_io;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Constructors are used for constructing other objects from stream-like data.
 * 
 * @author Mikko Hilpinen
 * @param <T> The type of constrcutable this constructor constructs
 * @since 24.11.2014
 */
public abstract class AbstractConstructor<T extends Constructable<T>>
{
	// ATTRIBUTES	-------------------------------
	
	/**
	 * A string that indicates which character is used to differentiate an id from a normal 
	 * value
	 */
	public static final String ID_INDICATOR = "#";
	
	private Map<String, T> constructs;
	// Map<ID, Map<Receiver, List<AttributeName>>
	private Map<String, Map<T, List<String>>> idQuery;
	private T latestConstruct;
	
	
	// CONSTRUCTOR	-------------------------------
	
	/**
	 * Creates a new constructor
	 */
	public AbstractConstructor()
	{
		// Initializes attributes
		this.constructs = new HashMap<>();
		this.idQuery = new HashMap<>();
		this.latestConstruct = null;
	}
	
	
	// ABSTRACT METHODS	----------------------------
	
	/**
	 * @return a new Constructable
	 */
	protected abstract T createConstructable();
	
	
	// GETTERS & SETTERS	------------------------
	
	/**
	 * @return The last object that was constructed
	 */
	public T getLatestConstruct()
	{
		return this.latestConstruct;
	}
	
	/**
	 * Changes the construct that is currently being modified
	 * @param construct The construct that will be considered the latest construct from 
	 * now on
	 */
	public void moveTo(T construct)
	{
		if (construct != null)
			this.latestConstruct = construct;
	}
	
	/**
	 * Changes the construct that is currently being modified
	 * @param id The id of the construct that will be considered the latest construct from 
	 * now on
	 */
	public void moveTo(String id)
	{
		moveTo(this.constructs.get(id));
	}

	
	// OTHER METHODS	----------------------------
	
	/**
	 * Creates a new construct with the given ID
	 * 
	 * @param id The id that will be given to the construct
	 */
	public void create(String id)
	{
		if (this.constructs.containsKey(id))
			throw new ConstructorException("IDs must be unique.");
		
		T newConstruct = createConstructable();
		
		newConstruct.setID(id);
		this.constructs.put(id, newConstruct);
		
		// Checks if someone had qued the construct
		if (this.idQuery.containsKey(id))
		{
			Map<T, List<String>> informedConstructs = this.idQuery.get(id);
			for (T construct : informedConstructs.keySet())
			{
				for (String attribute : informedConstructs.get(construct))
				{
					construct.setLink(attribute, newConstruct);
				}
			}
			
			this.idQuery.remove(id);
		}
		
		this.latestConstruct = newConstruct;
	}
	
	/**
	 * Adds a new attribute to the latest construct
	 * @param attributeName The name of the attribute
	 * @param attributeValue The value the attribute will have
	 */
	public void addAttribute(String attributeName, String attributeValue)
	{
		if (getLatestConstruct() == null)
			throw new ConstructorException("No objects constructed yet");
		
		getLatestConstruct().setAttribute(attributeName, attributeValue);
	}
	
	/**
	 * Adds a new link to the latest construct
	 * @param attributeBame The name of the link
	 * @param linkID The id of the linked construct
	 */
	public void addLink(String attributeBame, String linkID)
	{
		// Simply adds the link or makes a new query
		if (this.constructs.containsKey(linkID))
			getLatestConstruct().setLink(attributeBame, this.constructs.get(linkID));
		else
		{
			if (!this.idQuery.containsKey(linkID))
				this.idQuery.put(linkID, new HashMap<>());
			
			Map<T, List<String>> queries = this.idQuery.get(linkID);
			if (!queries.containsKey(getLatestConstruct()))
				queries.put(getLatestConstruct(), new ArrayList<>());
			
			queries.get(getLatestConstruct()).add(attributeBame);
		}
	}
	
	
	// SUBCLASSES	--------------------------------
	
	/**
	 * ConstructorExceptions can be caused by incorrect use of Constructors. They often cause 
	 * fatal problems in the constructed instances.
	 * 
	 * @author Mikko Hilpinen
	 * @since 24.11.2014
	 */
	public static class ConstructorException extends RuntimeException
	{
		private static final long serialVersionUID = 5504272340302756944L;

		/**
		 * Creates a new constructorException with the given message
		 * @param message The message that will be added to this exception
		 */
		public ConstructorException(String message)
		{
			super(message);
		}
	}
}
