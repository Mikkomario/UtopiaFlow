package utopia.flow.parse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import utopia.flow.function.ThrowingFunction;
import utopia.flow.function.ThrowingSupplier;
import utopia.flow.structure.ImmutableList;
import utopia.flow.structure.ImmutableMap;
import utopia.flow.structure.ListBuilder;
import utopia.flow.structure.Option;
import utopia.flow.structure.Pair;
import utopia.flow.structure.Try;

/**
 * This class is used for reading through xml data. This reader class can be used for both SAX 
 * (memory efficient streaming) and DOM (easy to use object oriented) style parsing.
 * @author Mikko Hilpinen
 * @since 25.7.2018
 */
public class XmlReader implements AutoCloseable
{
	// ATTRIBUTES	---------------------
	
	/**
	 * The default character set used with the xml reader
	 */
	public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
	
	private XMLStreamReader reader;
	
	
	// CONSTRUCTOR	---------------------
	
	/**
	 * Creates a new xml reader
	 * @param stream The XML stream the reader operates over
	 * @param charset The character set of the underlying stream
	 * @throws XMLStreamException Are thrown on read failures
	 * @throws FactoryConfigurationError If a reader couldn't be created
	 */
	public XmlReader(InputStream stream, Charset charset) throws XMLStreamException, 
			FactoryConfigurationError
	{
		this.reader = XMLInputFactory.newInstance().createXMLStreamReader(
				new InputStreamReader(stream, charset));
		
		_toNextElementStart(Option.none());
	}
	
	
	// STATIC	------------------------
	
	/**
     * Reads the contents of a stream using the specified reader function
     * @param stream the target stream
     * @param charset the charset of the stream contents
     * @param contentReader the function that uses the reader to parse the stream contents
     * @return The data parsed from the stream (may fail)
     */
	public static <T> Try<T> readStream(InputStream stream, Charset charset, 
			ThrowingFunction<? super XmlReader, ? extends T, ?> contentReader)
	{
		return Try.run(() -> 
		{
			try (XmlReader reader = new XmlReader(stream, charset))
			{
				return contentReader.throwingApply(reader);
			}
		});
	}
	
	/**
     * Reads the contents of a stream using the specified reader function
     * @param stream the target stream
     * @param contentReader the function that uses the reader to parse the stream contents
     * @return The data parsed from the stream (may fail)
     */
	public static <T> Try<T> readStream(InputStream stream, 
			ThrowingFunction<? super XmlReader, ? extends T, ?> contentReader)
	{
		return readStream(stream, DEFAULT_CHARSET, contentReader);
	}
	
	/**
     * Reads the contents of an xml file using the specified reader function
     * @param file the target file
     * @param charset the charset of the file contents
     * @param contentReader the function that uses the reader to parse the file contents
     * @return The data parsed from the file (may fail)
     */
	public static <T> Try<T> readFile(File file, Charset charset, 
			ThrowingFunction<? super XmlReader, ? extends T, ?> contentReader)
	{
		try (FileInputStream stream = new FileInputStream(file))
		{
			return readStream(stream, charset, contentReader);
		}
		catch (IOException e)
		{
			return Try.failure(e);
		}
	}
	
	/**
     * Reads the contents of an xml file using the specified reader function
     * @param file the target file
     * @param contentReader the function that uses the reader to parse the file contents
     * @return The data parsed from the file (may fail)
     */
	public static <T> Try<T> readFile(File file, 
			ThrowingFunction<? super XmlReader, ? extends T, ?> contentReader)
	{
		return readFile(file, DEFAULT_CHARSET, contentReader);
	}
	
	/**
     * Parses the contents of a stream into an xml element
     * @param stream the target stream
     * @param charset the charset of the stream contents
     * @return The element parsed from the stream (may fail)
     */
	public static Try<XmlElement> parseStream(InputStream stream, Charset charset)
	{
		return readStream(stream, charset, reader -> 
		{
			return reader.readElement().getOrFail(
					() -> new NoSuchElementException("No xml data in stream"));
		}); 
	}
	
	/**
     * Parses the contents of a stream into an xml element
     * @param stream the target stream
     * @return The element parsed from the stream (may fail)
     */
	public static Try<XmlElement> parseStream(InputStream stream)
	{
		return parseStream(stream, DEFAULT_CHARSET);
	}
	
	/**
     * Parses the contents of an xml file into an xml element
     * @param file the target file
     * @param charset the target charset
     * @return The element parsed from the file (may fail)
     */
	public static Try<XmlElement> parseFile(File file, Charset charset)
	{
		try (FileInputStream stream = new FileInputStream(file))
		{
			return parseStream(stream, charset);
		}
		catch (IOException e)
		{
			return Try.failure(e);
		}
	}
	
	/**
     * Parses the contents of an xml file into an xml element
     * @param file the target file
     * @return The element parsed from the file (may fail)
     */
	public static Try<XmlElement> parseFile(File file)
	{
		return parseFile(file, DEFAULT_CHARSET);
	}
	
	
	// IMPLEMENTED	--------------------
	
	@Override
	public void close() throws XMLStreamException
	{
		this.reader.close();
	}
	
	
	// OTHER	------------------------
	
	/**
     * @return Whether the reader has reached the end of the document
	 * @throws XMLStreamException 
     */
	public boolean isAtDocumentEnd() throws XMLStreamException
	{
		return !this.reader.hasNext();
	}
	
	/**
     * @return The name of the current element. None if at the end of the document
	 * @throws XMLStreamException 
     */
	public Option<String> currentElementName() throws XMLStreamException
	{
		return isAtDocumentEnd() ? Option.none() : Option.some(this.reader.getLocalName());
	}
	
	/**
     * @return The attributes in the current element in map format. An empty map if at the end of 
     * the document.
	 * @throws XMLStreamException 
     */
	public ImmutableMap<String, String> currentElementAttributes() throws XMLStreamException
	{
		if (isAtDocumentEnd())
			return ImmutableMap.empty();
		else
			return parseAttributes();
	}
	
	/**
     * Parses the contents of a single xml element, including all its children. this reader is then 
     * moved to the next sibling element or higher
     * @return the parsed element. None if (and only if) the reader was at document end already
	 * @throws XMLStreamException 
     */
	public Option<XmlElement> readElement() throws XMLStreamException
	{
		if (isAtDocumentEnd())
			return Option.none();
		else
			return Option.some(_readElement().getFirst().toXmlElement());
	}
	
	/**
     * Parses the contents of all remaining elements under the current parent element 
     * (including the current element). this reader is then moved to the parent's next sibling or 
     * higher
     * @return the parsed elements
	 * @throws XMLStreamException 
     */
	public ImmutableList<XmlElement> readSiblings() throws XMLStreamException
	{
		ListBuilder<XmlElement> buffer = new ListBuilder<>();
		int depth = 0;
		
		while (depth >= 0 && !isAtDocumentEnd())
		{
			Pair<UnfinishedElement, Integer> nextResult = _readElement();
			buffer.add(nextResult.getFirst().toXmlElement());
			depth += nextResult.getSecond();
		}
		
		return buffer.build();
	}
	
	/**
	 * Checks whether this reader is currently at an element with an acceptable name
	 * @param nameFilter A function used for testing element names
	 * @return Whether the reader is currently at an acceptable element
	 * @throws XMLStreamException
	 */
	public boolean isAtElementWithName(Predicate<? super String> nameFilter) throws XMLStreamException
	{
		return currentElementName().exists(nameFilter);
	}
	
	/**
	 * Checks whether this reader is currently at an element with the specified name (case-insensitive)
	 * @param name A name
	 * @return Whether this reader is at an element with the specified name
	 * @throws XMLStreamException
	 */
	public boolean isAtElementWithName(String name) throws XMLStreamException
	{
		return isAtElementWithName(name::equalsIgnoreCase);
	}
	
	/**
     * Moves this reader to the next element (child, sibling, etc.)
     * @return how much the 'depth' of this reader changed in the process (1 for child, 
     * 0 for sibling, -1 for parent level and so on)
	 * @throws XMLStreamException 
     */
	public int toNextElement() throws XMLStreamException
	{
		return _toNextElementStart(Option.none());
	}
	
	/**
     * Moves this reader to the next element (child, sibling, etc.) with a name that is accepted 
     * by the provided filter
     * @param nameFilter a filter that determines whether the name is accepted or not
	 * @param checkCurrentElement Whether the current element should be checked as well. If true and the current element 
	 * name is accepted, doesn't move the reader
     * @return how much the 'depth' of this reader changed in the process (1 for child, 
     * 0 for sibling, -1 for parent level and so on)
	 * @throws XMLStreamException 
     */
	public int toNextElementWithName(Predicate<? super String> nameFilter, 
			boolean checkCurrentElement) throws XMLStreamException
	{
		if (checkCurrentElement && isAtElementWithName(nameFilter))
			return 0;
		else
			return toNextWithName(nameFilter, i -> true);
	}
	
	/**
     * Moves this reader to the next element (child, sibling, etc.) with a name that is accepted 
     * by the provided filter
     * @param nameFilter a filter that determines whether the name is accepted or not
     * @return how much the 'depth' of this reader changed in the process (1 for child, 
     * 0 for sibling, -1 for parent level and so on)
	 * @throws XMLStreamException 
     */
	public int toNextElementWithName(Predicate<? super String> nameFilter) throws XMLStreamException
	{
		return toNextElementWithName(nameFilter, false);
	}
	
	/**
     * Moves this reader to the next element (child, sibling, etc.) with the specified name
     * @param searchedName the name the targeted element must have (case-insensitive)
	 * @param checkCurrentElement Whether the current element should be checked as well. If true and the current element 
	 * has the provided name, doesn't move the reader
     * @return how much the 'depth' of this reader changed in the process (1 for child, 
     * 0 for sibling, -1 for parent level and so on)
	 * @throws XMLStreamException 
     */
	public int toNextElementWithName(String searchedName, boolean checkCurrentElement) throws XMLStreamException
	{
		return toNextElementWithName(searchedName::equalsIgnoreCase, checkCurrentElement);
	}
	
	/**
     * Moves this reader to the next element (child, sibling, etc.) with the specified name
     * @param searchedName the name the targeted element must have (case-insensitive)
     * @return how much the 'depth' of this reader changed in the process (1 for child, 
     * 0 for sibling, -1 for parent level and so on)
	 * @throws XMLStreamException 
     */
	public int toNextElementWithName(String searchedName) throws XMLStreamException
	{
		return toNextElementWithName(searchedName, false);
	}
	
	/**
     * Moves this reader to the next element with a name accepted by the provided filter. Limits the 
     * search to elements under the current element (children, grand children, etc.). If no such 
     * element is found, stops at the next sibling, parent or higher.
     * @param nameFilter the filter that defines whether an element name is accepted
     * @return Whether such a child element was found (if true, this reader is now at the searched 
     * element)
	 * @throws XMLStreamException 
     */
	public boolean toNextChildWithName(Predicate<? super String> nameFilter) throws XMLStreamException
	{
		return toNextWithName(nameFilter, depth -> depth > 0) > 0;
	}
	
	/**
     * Moves this reader to the next element with the specified name. Limits the 
     * search to elements under the current element (children, grand children, etc.). If no such 
     * element is found, stops at the next sibling, parent or higher.
     * @param searchedName the name of the searched element (case-insensitive)
     * @return Whether such a child element was found (if true, this reader is now at the searched 
     * element)
	 * @throws XMLStreamException 
     */
	public boolean toNextChildWithName(String searchedName) throws XMLStreamException
	{
		return toNextChildWithName(searchedName::equalsIgnoreCase);
	}
	
	/**
     * Moves this reader to the next sibling element that has a name that is accepted by the provided 
     * filter. If no such sibling is found, stops at the next parent level element or higher.
     * @param nameFilter the filter that determines whether an element name is accepted
	 * @param checkCurrentElement Whether the current element should be checked. If the current element name is 
	 * acceptable, keeps the reader at the same location and returns true.
     * @return Whether such a sibling was found (if true, this reader is now at the searched element)
	 * @throws XMLStreamException 
     */
	public boolean toNextSiblingWithName(Predicate<? super String> nameFilter, boolean checkCurrentElement) 
			throws XMLStreamException
	{
		if (checkCurrentElement && isAtElementWithName(nameFilter))
			return true;
		else
			return toNextWithName(nameFilter, depth -> depth == 0, this::skipElement) == 0;
	}
	
	/**
     * Moves this reader to the next sibling element that has a name that is accepted by the provided 
     * filter. If no such sibling is found, stops at the next parent level element or higher.
     * @param nameFilter the filter that determines whether an element name is accepted
     * @return Whether such a sibling was found (if true, this reader is now at the searched element)
	 * @throws XMLStreamException 
     */
	public boolean toNextSiblingWithName(Predicate<? super String> nameFilter) throws XMLStreamException
	{
		return toNextSiblingWithName(nameFilter, false);
	}
	
	/**
     * Moves this reader to the next sibling element that has the specified name. If no such 
     * sibling is found, stops at the next parent level element or higher.
     * @param searchedName the name of the searched element (case-insensitive)
     * @param checkCurrentElement Whether the current element should be checked. If the current element name is 
	 * acceptable, keeps the reader at the same location and returns true.
     * @return Whether such a sibling was found (if true, this reader is now at the searched element)
	 * @throws XMLStreamException 
     */
	public boolean toNextSiblingWithName(String searchedName, boolean checkCurrentElement) throws XMLStreamException
	{
		return toNextSiblingWithName(searchedName::equalsIgnoreCase, checkCurrentElement);
	}
	
	/**
     * Moves this reader to the next sibling element that has the specified name. If no such 
     * sibling is found, stops at the next parent level element or higher.
     * @param searchedName the name of the searched element (case-insensitive)
     * @return Whether such a sibling was found (if true, this reader is now at the searched element)
	 * @throws XMLStreamException 
     */
	public boolean toNextSiblingWithName(String searchedName) throws XMLStreamException
	{
		return toNextSiblingWithName(searchedName, false);
	}
	
	/**
     * Skips this element and moves to the next sibling, parent or higher
     * @return how much the 'depth' of this reader changed in the process 
     * (0 for sibling, -1 for parent level and so on)
	 * @throws XMLStreamException 
     */
	public int skipElement() throws XMLStreamException
	{
		return skip(0);
	}
	
	/**
     * Skips this element as well as any siblings this element may have and moves to the parent's 
     * next sibling or higher
     * @return how much the 'depth' of this reader changed in the process (-1 for parent level, 
     * -2 for grandparent level and so on)
	 * @throws XMLStreamException 
     */
	public int skipParent() throws XMLStreamException
	{
		return skip(-1);
	}
	
	private int toNextWithName(Predicate<? super String> nameFilter, 
			Predicate<? super Integer> depthRequirement) throws XMLStreamException
	{
		return toNextWithName(nameFilter, depthRequirement, this::toNextElement);
	}
	
	private int toNextWithName(Predicate<? super String> nameFilter, 
			Predicate<? super Integer> depthRequirement, 
			ThrowingSupplier<? extends Integer, ? extends XMLStreamException> move) 
			throws XMLStreamException
	{
		int depthChange = toNextElement();
		while (depthRequirement.test(depthChange) && 
				currentElementName().exists(name -> !nameFilter.test(name)))
		{
			depthChange += move.throwingGet();
		}
		return depthChange;
	}
	
	private XmlReadEvent currentEvent() throws XMLStreamException
	{
		if (this.reader.isStartElement())
			return XmlReadEvent.ELEMENT_START;
		else if (this.reader.isEndElement())
			return XmlReadEvent.ELEMENT_END;
		else if (this.reader.isCharacters())
			return XmlReadEvent.TEXT;
		else
			return nextEvent();
	}
	
	private XmlReadEvent nextEvent() throws XMLStreamException
	{
		if (this.reader.hasNext())
		{
			this.reader.next();
			return currentEvent();
		}
		else
			return XmlReadEvent.DOCUMENT_END;
	}
	
	private int skip(int depthChangeRequirement) throws XMLStreamException
	{
		int depth = _toNextElementStart(Option.none());
		
		while (depth > depthChangeRequirement && !isAtDocumentEnd())
		{
			depth += _toNextElementStart(Option.none());
		}
		
		return depth;
	}
	
	private Pair<UnfinishedElement, Integer> _readElement() throws XMLStreamException
	{
		UnfinishedElement element = new UnfinishedElement(this.reader.getLocalName(), parseAttributes());
		int depthChange = _toNextElementStart(Option.some(element));
		
		while (depthChange > 0)
		{
			Pair<UnfinishedElement, Integer> nextResult = _readElement();
			element.addChild(nextResult.getFirst());
			depthChange += nextResult.getSecond();
		}
		
		return new Pair<>(element, depthChange);
	}
	
	private int _toNextElementStart(Option<UnfinishedElement> openElement) throws XMLStreamException
	{
		switch (nextEvent())
		{
			case ELEMENT_START: return 1;
			case ELEMENT_END: return _toNextElementStart(openElement) - 1;
			case TEXT: 
				openElement.forEach(e -> e.appendText(this.reader.getText()));
				return _toNextElementStart(openElement);
			case DOCUMENT_END: return 0;
		}
		
		return 0;
	}
	
	private ImmutableMap<String, String> parseAttributes()
	{
		int attCount = this.reader.getAttributeCount();
		return ImmutableMap.of(ImmutableList.range(0, attCount - 1).map(
				i -> new Pair<>(this.reader.getAttributeLocalName(i), this.reader.getAttributeValue(i))));
	}
	
	
	// ENUMS	-------------------------
	
	private static enum XmlReadEvent
	{
		ELEMENT_START, 
		ELEMENT_END, 
		TEXT, 
		DOCUMENT_END;
	}
	
	
	// NESTED CLASSES	-----------------
	
	private static class UnfinishedElement
	{
		// ATTRIBUTES	-----------------
		
		private String name;
		private ImmutableMap<String, String> attributes;
		
		private ImmutableList<UnfinishedElement> children = ImmutableList.empty();
		private String text = "";
		
		
		// CONSTRUCTOR	----------------
		
		public UnfinishedElement(String name, ImmutableMap<String, String> attributes)
		{
			this.name = name;
			this.attributes = attributes;
		}
		
		
		// ACCESSORS	-----------------
		
		public String getName()
		{
			return this.name;
		}
		
		public ImmutableMap<String, String> getAttributes()
		{
			return this.attributes;
		}
		
		public ImmutableList<UnfinishedElement> getChildren()
		{
			return this.children;
		}
		
		public void setChildren(ImmutableList<UnfinishedElement> children)
		{
			this.children = children;
		}
		
		public void addChild(UnfinishedElement child)
		{
			setChildren(getChildren().plus(child));
		}
		
		public String getText()
		{
			return this.text;
		}
		
		public void appendText(String text)
		{
			this.text += text;
		}
		
		
		// OTHER	---------------------
		
		public XmlElement toXmlElement()
		{
			return new XmlElement(getName(), Option.takeIf(getText(), !getText().isEmpty()), 
					getChildren().map(c -> c.toXmlElement()), getAttributes());
		}
	}
}
