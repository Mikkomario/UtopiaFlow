package utopia.java.flow.test;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import utopia.java.flow.recording.TextConstructorInstructor;
import utopia.java.flow.recording.TextObjectWriter;
import utopia.java.flow.recording.XMLConstructorInstructor;
import utopia.java.flow.recording.XMLObjectWriter;
import utopia.java.flow.io.FileOutputAccessor;
import utopia.java.flow.io.XMLIOAccessor;

/**
 * ConstructrionTest tests object construction
 * 
 * @author Mikko Hilpinen
 * @since 26.11.2014
 * @deprecated Replaced with new generic classes and xml element parsing
 */
class ConstructionTest
{
	private ConstructionTest()
	{
		// The constructor is hidden since the interface is static
	}
	
	private static void printObjects(List<TestConstructable> objects)
	{
		for (TestConstructable t : objects)
		{
			System.out.println(t);
		}
	}
	
	private static List<TestConstructable> getConstructablesFromFile(File file)
	{
		TestConstructor constructor = new TestConstructor();
		TextConstructorInstructor instructor = new TextConstructorInstructor(constructor);
		
		// Reads the constructables
		try
		{
			instructor.constructFromFile(file, null);
		}
		catch (FileNotFoundException e)
		{
			System.err.println("Couldn't find the file");
			e.printStackTrace();
			return new ArrayList<>();
		}
		
		// Makes them into a list
		List<TestConstructable> objects = new ArrayList<>();
		objects.addAll(constructor.getConstructs().values());
		objects.sort(null);
		
		return objects;
	}
	
	private static List<TestConstructable> getConstructablesFromXML(ByteArrayInputStream stream) throws IOException
	{
		TestConstructor constructor = new TestConstructor();
		XMLConstructorInstructor instructor = new XMLConstructorInstructor(constructor);
		
		System.out.println("Instructor ready");
		
		try
		{
			instructor.constructFrom(stream);
			
			System.out.println("Construction ready");
		}
		catch (XMLStreamException e)
		{
			e.printStackTrace();
		}
		
		// Makes them into a list
		List<TestConstructable> objects = new ArrayList<>();
		objects.addAll(constructor.getConstructs().values());
		objects.sort(null);
		
		System.out.println("Object list ready");
		
		return objects;
	}
	
	/**
	 * Tests
	 * @param args not used
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException
	{
		// Creates a bunch of testConstructables
		List<TestConstructable> objects = new ArrayList<>();
		objects.add(new TestConstructable("START", "The first one is ", "Uuno", null));
		for (int i = 0; i < 10; i++)
		{
			objects.add(new TestConstructable("GROUP" + (1 + i / 5), "hello ", "Matti " + i, 
					objects.get(i)));
		}
		objects.sort(null);
		
		// Prints the starting status
		printObjects(objects);
		
		
		// Saves the objects into a file and also xml stream
		TextObjectWriter textWriter = new TextObjectWriter();
		BufferedWriter writer = null;
		XMLObjectWriter xmlWriter = new XMLObjectWriter();
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		XMLStreamWriter streamWriter = null;
		
		try
		{
			writer = FileOutputAccessor.openFile("ConstructionTest.txt");
			streamWriter = XMLIOAccessor.createWriter(stream);
			
			xmlWriter.openDocument("ConstructionTest", streamWriter);
			String lastUnder = null;
			for (TestConstructable t : objects)
			{
				if (lastUnder == null || !lastUnder.equals(t.getBornUnder()))
				{
					lastUnder = t.getBornUnder();
					TextObjectWriter.writeInstruction(lastUnder, writer);
					xmlWriter.openInstruction(lastUnder, streamWriter);
				}
				textWriter.writeInto(t, writer);
				xmlWriter.writeInto(t, streamWriter);
			}
			
			xmlWriter.closeDocument(streamWriter);
		}
		catch (XMLStreamException e)
		{
			e.printStackTrace();
		}
		finally
		{
			FileOutputAccessor.closeWriter(writer);
			XMLIOAccessor.closeWriter(streamWriter);
		}
		
		
		// Reads the objects from a file
		List<TestConstructable> readObjects = getConstructablesFromFile(
				new File("ConstructionTest.txt"));
		printObjects(readObjects);
		
		
		// Prints the xml
		byte[] xml = stream.toByteArray();
		System.out.println(new String(xml));
		
		// Reads the objects from the xml
		readObjects = getConstructablesFromXML(new ByteArrayInputStream(xml));
		printObjects(readObjects);
	}
}
