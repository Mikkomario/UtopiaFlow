package flow_test;

import java.util.HashMap;
import java.util.Map;

import flow_recording.Constructable;
import flow_recording.Writable;

/**
 * TestConstructable is used for testing object reading & writing
 * 
 * @author Mikko Hilpinen
 * @since 26.11.2014
 */
public class TestConstructable implements Constructable<TestConstructable>, Writable, 
		Comparable<TestConstructable>
{
	// ATTRIBUTES	---------------------------------
	
	private String bornUnder, message, name, id;
	private TestConstructable fella;
	
	
	// CONSTRUCTOR	---------------------------------
	
	/**
	 * Creates a new testConstructable
	 * @param bornUnder What instruction was active when the constructable was created
	 */
	public TestConstructable(String bornUnder)
	{
		this.bornUnder = bornUnder;
	}
	
	/**
	 * Creates a new testConstructable with complete data
	 * @param bornUnder The instruction
	 * @param message The message
	 * @param name The name
	 * @param fella The companion of this constructable
	 */
	public TestConstructable(String bornUnder, String message, String name, TestConstructable fella)
	{
		this.bornUnder = bornUnder;
		this.message = message;
		this.name = name;
		this.fella = fella;
	}

	
	// IMPLEMENTED METHODS	-------------------------
	
	@Override
	public String getID()
	{
		return this.id;
	}

	@Override
	public void setID(String id)
	{
		this.id = id;
	}

	@Override
	public void setAttribute(String attributeName, String attributeValue)
	{
		switch (attributeName)
		{
			case "message": this.message = attributeValue; break;
			case "name": this.name = attributeValue; break;
		}
	}

	@Override
	public void setLink(String linkName, TestConstructable target)
	{
		this.fella = target;
	}
	
	@Override
	public String toString()
	{
		return this.bornUnder + ":" + this.message + this.name + 
				(this.fella != null ? " and also " + this.fella.name : "");
	}

	@Override
	public Map<String, String> getAttributes()
	{
		Map<String, String> attributes = new HashMap<>();
		
		attributes.put("message", this.message);
		attributes.put("name", this.name);
		
		return attributes;
	}

	@Override
	public Map<String, Writable> getLinks()
	{
		Map<String, Writable> links = new HashMap<>();
		
		if (this.fella != null)
			links.put("fella", this.fella);
		
		return links;
	}

	@Override
	public int compareTo(TestConstructable o)
	{
		if (this.bornUnder == null)
			return -1;
		else if (o.bornUnder == null)
			return 1;
		
		return this.bornUnder.compareTo(o.bornUnder);
	}
	
	
	// GETTERS & SETTERS 	--------------------------
	
	/**
	 * @return Which mode the constructable was born under
	 */
	public String getBornUnder()
	{
		return this.bornUnder;
	}
}
