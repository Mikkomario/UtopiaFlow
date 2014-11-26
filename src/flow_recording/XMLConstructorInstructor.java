package flow_recording;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import flow_io.XMLIOAccessor;

/**
 * This constructor constructs objects by using an xml stream
 * 
 * @author Mikko Hilpinen
 */
public class XMLConstructorInstructor
{
	// ATTRIBUTES	--------------------------------------
	
	private AbstractConstructor<?> constructor;
	
	
	// CONSTRUCTOR	--------------------------------------
	
	/**
	 * Creates a new instructor that instructs the given constructor
	 * @param constructor The constructor that will be instructed
	 */
	public XMLConstructorInstructor(AbstractConstructor<?> constructor)
	{
		this.constructor = constructor;
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
		String attributeElementName = null;
		int depth = 0;
		
		XMLStreamReader reader = XMLIOAccessor.createReader(stream);
		
		while (reader.hasNext())
		{
			// A new element may be a constructable (contains other elements or attributes) or 
			// just an attribute or a link with a cdata value
			if (reader.isStartElement())
			{
				// Object elements contain their ID
				if (reader.getLocalName().startsWith(AbstractConstructor.ID_INDICATOR))
					this.constructor.create(reader.getLocalName());
				else
				{
					// The root element is skipped (depth 0)
					// If there is no current element, an instruction was given (depth 1)
					if (depth == 1)
						this.constructor.setInstruction(reader.getLocalName());
					// If no id is found, an element is added to the current object (depth 2)
					else if (depth == 2)
						attributeElementName = reader.getLocalName();
				}
				
				depth ++;
			}
			// On character data, a link or an attribute value will be created
			else if (reader.isCharacters())
			{
				// If there was no attribute element above this data, can't work properly
				if (attributeElementName == null)
					throw new AbstractConstructor.ConstructorException("The XML is not well valid");
				
				String value = reader.getText();
				if (value.startsWith(AbstractConstructor.ID_INDICATOR))
					this.constructor.addLink(attributeElementName, value);
				else
					this.constructor.addAttribute(attributeElementName, value);
			}
			else if (reader.isEndElement())
			{
				// Remembers that depth decreased
				depth --;
				attributeElementName = null;
			}
		}
		
		XMLIOAccessor.closeReader(reader);
	}
}
