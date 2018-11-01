package utopia.flow.test;

import java.io.File;

import utopia.flow.generics.Value;
import utopia.flow.parse.XmlElement;
import utopia.flow.parse.XmlReader;
import utopia.flow.parse.XmlWriter;
import utopia.flow.structure.ImmutableList;
import utopia.flow.structure.Try;
import utopia.flow.util.Test;

/**
 * This class tests both xml writing and xml reading
 * @author Mikko Hilpinen
 * @since 25.7.2018
 */
public class XmlTest
{
	@SuppressWarnings("javadoc")
	public static void main(String[] args)
	{
		// Creates the xml elements
		XmlElement gChild1 = new XmlElement("c", "Test & Values")
				.withAttributeAdded("att1", "1").withAttributeAdded("att2", "b");
		XmlElement gChild2 = new XmlElement("d", Value.of(123456));
		XmlElement gChild3 = XmlElement.empty("e");
		
		XmlElement child = new XmlElement("b", ImmutableList.withValues(gChild1, gChild2, gChild3));
		XmlElement root = new XmlElement("a", ImmutableList.withValue(child))
				.withAttributeAdded("id", "34");
		
		// Tests some basic xml element methods
		Test.check(root.childrenWithName("b").contains(child));
		Test.checkEquals(root.get("b").get("d"), gChild2);
		
		// Test prints
		System.out.println(root);
		
		// Tries to write the xml data to a file
		File testFile = new File("test/XmlTest.xml");
		testFile.getParentFile().mkdirs();
		
		Test.check(XmlWriter.writeElementToFile(testFile, root).isSuccess());
		
		// Parses the contents of the file (dom)
		Try<XmlElement> parsed = XmlReader.parseFile(testFile);
		System.out.println(parsed.getSuccess());
		
		Test.checkEquals(parsed.getSuccess(), root);
		
		// Parses the contents of the file (sax)
		Try<XmlElement> parsedG1 = XmlReader.readFile(testFile, reader -> 
		{
			reader.toNextChildWithName("c");
			return reader.readElement().get();
		});
		
		System.out.println(parsedG1);
		
		Test.checkEquals(parsedG1.getSuccess(), gChild1);
		
		// Tests reading a test file
		/*
		try
		{
			String fileStr = readFile(Paths.get("test/CUSTOMERS2.xml"), StandardCharsets.UTF_8);
			System.out.println("|" + fileStr + "|");
		}
		catch (IOException e)
		{
			System.err.println(e.getMessage());
			e.printStackTrace();
			Test.fail("Stream read failed");
		}
		
		Try<XmlElement> parsed2 = RichString.fromFile(Paths.get("test/CUSTOMERS.XML"), 
				StandardCharsets.UTF_8).flatMap(XmlReader::parseString);
		parsed2.failure().forEach(e -> 
		{
			System.err.println(e.getMessage());
			e.printStackTrace();
		});
		
		Test.check(parsed2.isSuccess());
		
		System.out.println(parsed2.getSuccess());
		*/
		
		System.out.println("Done");
	}
}
