package flow_io;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * This constructor constructs objects by using an xml stream
 * 
 * @author Mikko Hilpinen
 * @param <T> The type of object constructed by this constructor
 */
public class XMLConstructorInstructor<T extends Constructable<T>>
{
	// ATTRIBUTES	--------------------------------------
	
	private String latestElementName;
	private AbstractConstructor<T> constructor;
	
	
	// CONSTRUCTOR	--------------------------------------
	
	/**
	 * Creates a new instructor that instructs the given constructor
	 * @param constructor The constructor that will be instructed
	 */
	public XMLConstructorInstructor(AbstractConstructor<T> constructor)
	{
		this.constructor = constructor;
		this.latestElementName = null;
	}
	
	
	// OTHER METHODS	-----------------------------------
	
	/**
	 * Creates constructs from the given xml stream. Attributes are not read, this constructor 
	 * only uses elements and character data
	 * 
	 * @param stream The xml stream the constructables are read from
	 * @throws UnsupportedEncodingException If the stream doesn't support UFT-8
	 * @throws XMLStreamException If the xml stream couldn't be read correctly
	 */
	public void constructFrom(InputStream stream) throws UnsupportedEncodingException, XMLStreamException
	{
		this.latestElementName = null;
		boolean rootElementPassed = false;
		
		XMLStreamReader reader = XMLIOAccessor.createReader(stream);
		
		while (reader.hasNext())
		{
			// A new element may be a constructable (contains other elements or attributes) or 
			// just an attribute or a link with a cdata value
			if (reader.isStartElement())
			{
				if (this.latestElementName != null)
					this.constructor.create(this.latestElementName);
				// Skips the first element introduction
				else if (!rootElementPassed)
				{
					rootElementPassed = true;
					continue;
				}
				
				this.latestElementName = reader.getLocalName();
			}
			// On character data, a link or an attribute value will be created
			else if (reader.isCharacters())
			{
				String value = reader.getText();
				if (value.startsWith(AbstractConstructor.ID_INDICATOR))
					this.constructor.addLink(this.latestElementName, value);
				else
					this.constructor.addAttribute(this.latestElementName, value);
			}
		}
		
		XMLIOAccessor.closeReader(reader);
	}
}
