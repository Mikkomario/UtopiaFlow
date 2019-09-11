package utopia.flow.parse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import utopia.flow.function.ThrowingConsumer;
import utopia.flow.function.ThrowingRunnable;
import utopia.flow.structure.ImmutableList;
import utopia.flow.structure.ImmutableMap;
import utopia.flow.structure.IntSet;
import utopia.flow.structure.Option;
import utopia.flow.structure.Try;
import utopia.flow.structure.View;
import utopia.flow.structure.range.IntRange;
import utopia.flow.util.Unit;

/**
 * These writers are used for writing xml element data to streams. The writer supports both 
 * sax (streaming) and dom (object-based) style writing
 * @author Mikko Hilpinen
 * @since 25.7.2018
 */
public class XmlWriter implements AutoCloseable
{
	// ATTRIBUTES	---------------------
	
	/**
	 * The default charset used by XmlWriters
	 */
	public static final Charset DEFAULT_CHARSET = XmlReader.DEFAULT_CHARSET;
	
	private static final IntSet INVALID_CHAR_RANGES = IntSet.ofRanges(
			ImmutableList.withValues(IntRange.inclusive(91, 94), IntRange.inclusive(123, 191), 
			IntRange.inclusive(768, 879), IntRange.inclusive(8192, 8203), IntRange.inclusive(8206, 8303), 
			IntRange.inclusive(8592, 11263), IntRange.inclusive(12272, 12288), IntRange.inclusive(55296, 63743), 
			IntRange.inclusive(64976, 65007), IntRange.inclusive(65534, 1114111)));
	private static final ImmutableList<Integer> INVALID_EXTRA_CHARS = 
			ImmutableList.withValues(34, 38, 39, 60, 62, 96, 215, 247, 894);
	
	private Charset charset;
	private XMLStreamWriter writer;
	
	
	// CONSTRUCTOR	---------------------
	
	/**
	 * Creates a new xml writer instance
	 * @param stream The target stream
	 * @param charset The charset used for encoding xml data
	 * @throws XMLStreamException If creation failed
	 * @throws FactoryConfigurationError If writer creation failed
	 */
	public XmlWriter(OutputStream stream, Charset charset) throws XMLStreamException, 
			FactoryConfigurationError
	{
		this.charset = charset;
		this.writer = XMLOutputFactory.newInstance().createXMLStreamWriter(
				new OutputStreamWriter(stream, charset));
	}
	
	
	// STATIC	-------------------------
	
	/**
     * Writes an xml document to the target stream
     * @param stream the targeted stream
     * @param charset the used charset (default = UTF-8)
     * @param writeContent the function that writes the document contents
     * @return The results of the operation
     */
	public static Try<Unit> writeToStream(OutputStream stream, Charset charset, 
			ThrowingConsumer<? super XmlWriter, ?> writeContent)
	{
		try (XmlWriter writer = new XmlWriter(stream, charset))
		{
			writer.writeDocument(() -> writeContent.accept(writer));
			return Try.success(Unit.getInstance());
		} 
		catch (XMLStreamException e)
		{
			return Try.failure(e);
		}
		catch (Exception e)
		{
			return Try.failure(e);
		}
	}
	
	/**
     * Writes an xml document to the target stream
     * @param stream the targeted stream
     * @param writeContent the function that writes the document contents
     * @return The results of the operation
     */
	public static Try<Unit> writeToStream(OutputStream stream, 
			ThrowingConsumer<? super XmlWriter, ?> writeContent)
	{
		return writeToStream(stream, DEFAULT_CHARSET, writeContent);
	}
	
	/**
     * Writes an xml document to the target stream
     * @param stream the targeted stream
     * @param element The root element that is written to the document
     * @param charset the charset to use (default = UTF-8)
     * @return The results of the operation
     */
	public static Try<Unit> writeElementToStream(OutputStream stream, XmlElement element, 
			Charset charset)
	{
		return writeToStream(stream, charset, w -> w.write(element));
	}
	
	/**
     * Writes an xml document to the target stream
     * @param stream the targeted stream
     * @param element The root element that is written to the document
     * @return The results of the operation
     */
	public static Try<Unit> writeElementToStream(OutputStream stream, XmlElement element)
	{
		return writeElementToStream(stream, element, DEFAULT_CHARSET);
	}
	
	/**
     * Writes an xml document to the target file
     * @param file the targeted file
     * @param charset the used charset (default = UTF-8)
     * @param writeContent the function that writes the document contents
     * @return The results of the operation
     */
	public static Try<Unit> writeFile(File file, Charset charset, 
			ThrowingConsumer<? super XmlWriter, ?> writeContent)
	{
		try (FileOutputStream stream = new FileOutputStream(file, false))
		{
			return writeToStream(stream, charset, writeContent);
		}
		catch (IOException e)
		{
			return Try.failure(e);
		}
	}
	
	/**
     * Writes an xml document to the target file
     * @param file the targeted file
     * @param writeContent the function that writes the document contents
     * @return The results of the operation
     */
	public static Try<Unit> writeFile(File file, ThrowingConsumer<? super XmlWriter, ?> writeContent)
	{
		return writeFile(file, DEFAULT_CHARSET, writeContent);
	}
	
	/**
     * Writes an xml document to the target file
     * @param file the targeted file
     * @param element The root element that is written to the document
     * @param charset the used charset (default = UTF-8)
     * @return The results of the operation
     */
	public static Try<Unit> writeElementToFile(File file, XmlElement element, Charset charset)
	{
		return writeFile(file, charset, w -> w.write(element));
	}
	
	/**
     * Writes an xml document to the target file
     * @param file the targeted file
     * @param element The root element that is written to the document
     * @return The results of the operation
     */
	public static Try<Unit> writeElementToFile(File file, XmlElement element)
	{
		return writeElementToFile(file, element, DEFAULT_CHARSET);
	}
	
	private static boolean charIsIllegal(char c)
	{
		int i = c;
		// Character is not allowed if it lies in an invalid char range or is specifically invalid
		return INVALID_CHAR_RANGES.contains(i) || INVALID_EXTRA_CHARS.contains(i);
	}

	
	// IMPLEMENTED	---------------------

	@Override
	public void close() throws XMLStreamException
	{
		this.writer.close();
	}
	
	
	// OTHER	-------------------------
	
	/**
     * Writes a complete xml document
     * @param contentWriter a function that is used for writing the contents of the document
	 * @throws E If contentWriter throws
	 * @throws XMLStreamException If writing fails
     */
	public <E extends Exception> void writeDocument(ThrowingRunnable<? extends E> contentWriter) 
			throws E, XMLStreamException
	{
		this.writer.writeStartDocument(this.charset.name(), "1.0");
		contentWriter.run();
		this.writer.writeEndDocument();
	}
	
	/**
     * Writes an element with content. Closes the element afterwards
     * @param elementName the name of the element
     * @param attributes the attributes written to element
     * @param text the text written to element (optional)
     * @param contentWriter the function that is used for writing the element contents (optional)
	 * @throws XMLStreamException 
     */
	public void writeElement(String elementName, ImmutableMap<String, String> attributes, 
			Option<String> text, 
			Option<? extends ThrowingRunnable<? extends XMLStreamException>> contentWriter) throws 
			XMLStreamException
	{
		// Writes element start, attributes & text
		this.writer.writeStartElement(elementName);
		attributes.forEachThrowing(p -> this.writer.writeAttribute(p.getFirst(), p.getSecond()));
		text.forEachThrowing(this::writeCharacters);
		
		// Writes other content
		contentWriter.forEachThrowing(w -> w.run());
		
		// Closes element
		this.writer.writeEndElement();
	}
	
	/**
     * Writes a simple element with only text data
     * @param elementName the name of the element
     * @param text the text written inside the element
	 * @param attributes The attributes written to the element (optional)
	 * @throws XMLStreamException 
     */
	public void writeTextElement(String elementName, String text, 
			ImmutableMap<String, String> attributes) throws XMLStreamException
	{
		writeElement(elementName, attributes, Option.some(text), Option.none());
	}
	
	/**
     * Writes a simple element with only text data
     * @param elementName the name of the element
     * @param text the text written inside the element
	 * @throws XMLStreamException 
     */
	public void writeTextElement(String elementName, String text) throws XMLStreamException
	{
		writeTextElement(elementName, text, ImmutableMap.empty());
	}
	
	/**
     * Writes an element with only attribute data
     * @param elementName the name of the element
     * @param attributes the attributes written to the element (optional)
	 * @throws XMLStreamException 
     */
	public void writeEmptyElement(String elementName, ImmutableMap<String, String> attributes) 
			throws XMLStreamException
	{
		writeElement(elementName, attributes, Option.none(), Option.none());
	}
	
	/**
     * Writes an element with only attribute data
     * @param elementName the name of the element
	 * @throws XMLStreamException 
     */
	public void writeEmptyElement(String elementName) throws XMLStreamException
	{
		writeEmptyElement(elementName, ImmutableMap.empty());
	}
	
	/**
	 * Writes an element with child content
	 * @param elementName The name of the element
	 * @param attributes The attributes for the element (optional)
	 * @param writeContent A function for writing element content
	 * @throws XMLStreamException
	 */
	public void writeElementWithContent(String elementName, ImmutableMap<String, String> attributes, 
			ThrowingRunnable<? extends XMLStreamException> writeContent) throws XMLStreamException
	{
		writeElement(elementName, attributes, Option.none(), Option.some(writeContent));
	}
	
	/**
	 * Writes an element with child content
	 * @param elementName The name of the element
	 * @param writeContent A function for writing element content
	 * @throws XMLStreamException
	 */
	public void writeElementWithContent(String elementName, 
			ThrowingRunnable<? extends XMLStreamException> writeContent) throws XMLStreamException
	{
		writeElementWithContent(elementName, ImmutableMap.empty(), writeContent);
	}
	
	/**
     * Writes a complete xml element tree to the document
     * @param element the element tree that is written
	 * @throws XMLStreamException 
     */
	public void write(XmlElement element) throws XMLStreamException
	{
		writeElement(element.getName(), element.getAttributes(), element.getTextOption(), 
				Option.some(() -> element.getChildren().forEachThrowing(this::write)));
	}
	
	private void writeCharacters(String text) throws XMLStreamException
	{
		// CDATA is used if necessary
		if (View.of(text).exists(XmlWriter::charIsIllegal))
			this.writer.writeCData(text);
		else
			this.writer.writeCharacters(text);
	}
}
