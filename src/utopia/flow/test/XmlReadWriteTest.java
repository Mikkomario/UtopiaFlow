package utopia.flow.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import utopia.flow.generics.Value;
import utopia.flow.io.XmlElementReader;
import utopia.flow.io.XmlElementReader.EndOfStreamReachedException;
import utopia.flow.io.XmlElementWriter;
import utopia.flow.structure.Element;
import utopia.flow.structure.TreeNode;
import utopia.flow.util.StringFilter;

/**
 * This class tests the xml element writing & reading
 * @author Mikko Hilpinen
 * @since 30.4.2016
 */
class XmlReadWriteTest
{
	// MAIN METHOD	--------------------
	
	public static void main(String[] args)
	{
		try
		{
			// Writes simple element data into a file
			File firstFile = new File("data/xmlFirst.xml");
			writeSimple(firstFile);
			
			// Reads the same data in tree form
			TreeNode<Element> readTree = XmlElementReader.parseFile(firstFile, true);
			
			// Then writes it into the second file
			File secondFile = new File("data/xmlSecond.xml");
			XmlElementWriter.writeElementIntoFile(readTree, secondFile, false);
			
			// Reads some of the elements from the second file
			for (Element element : readSimple(secondFile))
			{
				System.out.println(element);
			}
			
			System.out.println("DONE");
		}
		catch (Exception e)
		{
			System.err.println("FAILED");
			e.printStackTrace();
		}
	}
	
	// OTHER METHODS	----------------
	
	private static void writeSimple(File targetFile) throws IOException, XMLStreamException
	{
		OutputStream stream = new FileOutputStream(targetFile);
		XmlElementWriter writer = null;
		try
		{
			writer = new XmlElementWriter(stream, true);
			writer.startElement("root");
			
			String[] subjects = {"math", "art"};
			for (String userName : new String[] {"Antti", "Nelli", "Liisa", "Matti"})
			{
				writer.startElement(createUserElement(userName));
				for (String subject : subjects)
				{
					writer.writeElement(createGradeElement(subject, 8));
				}
				writer.closeElement();
			}
			
			writer.writeElement(new Element("updated", Value.Date(LocalDate.now())));
			
			writer.closeElement();
		}
		finally
		{
			if (writer != null)
				writer.closeQuietly();
			stream.close();
		}
	}
	
	private static List<Element> readSimple(File file) throws IOException, XMLStreamException
	{
		InputStream stream = new FileInputStream(file);
		XmlElementReader reader = null;
		List<Element> elements = new ArrayList<>();
		try
		{
			reader = new XmlElementReader(stream, false);
			
			// Reads the root element and moves in
			elements.add(reader.toNextElement());
			// Reads the first user element, skipping to its sibling
			elements.add(reader.toNextSibling());
			// Next moves to the first grade of that element
			elements.add(reader.toNextElement());
			// Reads the current element and then moves to 'updated' element
			elements.add(reader.toNextElementWithName(true, new StringFilter("updated")));
			
			// Reads all remaining elements
			while (reader.hasNext())
			{
				elements.add(reader.toNextElement());
			}
			
			return elements;
		}
		catch (EndOfStreamReachedException e)
		{
			System.err.println("End of stream reached");
			e.printStackTrace();
			return elements;
		}
		finally
		{
			if (reader != null)
				reader.closeQuietly();
			stream.close();
		}
	}
	
	private static Element createGradeElement(String subject, int grade)
	{
		Element element = new Element("grade", Value.Integer(grade));
		element.addAttribute("subject", subject);
		return element;
	}
	
	private static Element createUserElement(String name)
	{
		Element element = new Element("user");
		element.addAttribute("name", name);
		return element;
	}
}
