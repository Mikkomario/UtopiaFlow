package utopia.flow.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import utopia.flow.structure.ImmutableList;
import utopia.flow.structure.Option;
import utopia.flow.structure.Try;

/**
 * This is a static collection of utility methods for file handling
 * @author Mikko Hilpinen
 * @since 20.3.2018
 */
public class FileUtils
{
	// CONSTRUCTOR	-----------------------

	private FileUtils() { }

	
	// OTHER METHODS	-------------------
	
	/**
	 * Finds all files under a directory that have the provided file type
	 * @param directoryPath a path leading to a directory
	 * @param searchedFileType The accepted file type. Eg ".xml"
	 * @return A list of paths for the searched files. Failure (IOException) if file reading failed
	 */
	public static Try<ImmutableList<Path>> filesInDirectory(Path directoryPath, String searchedFileType)
	{
		String acceptedTypeString = searchedFileType.startsWith(".") ? searchedFileType : "." + searchedFileType;
		ImmutableList<String> acceptedTypes = ImmutableList.withValues(acceptedTypeString.toLowerCase(), 
				acceptedTypeString.toUpperCase());
		
		try (DirectoryStream<Path> paths = 
				Files.newDirectoryStream(directoryPath, path -> acceptedTypes.exists(path.getFileName().toString()::endsWith)))
		{
			return Try.success(ImmutableList.readWith(paths.iterator()));
		}
		catch (IOException e)
		{
			return Try.failure(e);
		}
	}
	
	/**
	 * Reads the first non-empty line of a file
	 * @param file The target file
	 * @return The first line of the file
	 */
	public static Option<String> readFirstFileLine(File file)
	{
		try (Scanner scanner = new Scanner(file))
		{
			while (scanner.hasNext())
			{
				String line = scanner.next();
				if (!line.isEmpty())
					return Option.some(line);
			}
			
			return Option.none();
		}
		catch (FileNotFoundException e)
		{
			return Option.none();
		}
	}
	
	/**
	 * Reads all non-empty lines from a file
	 * @param file The target file
	 * @return The lines from the file. Failure if the file didn't exist
	 */
	public static Try<ImmutableList<String>> linesFromFile(File file)
	{
		try (Scanner reader = new Scanner(file))
		{
			List<String> buffer = new ArrayList<>();
			while (reader.hasNext())
			{
				buffer.add(reader.nextLine());
			}
			
			return Try.success(ImmutableList.of(buffer).filter(line -> !line.isEmpty()));
		}
		catch (FileNotFoundException e)
		{
			return Try.failure(e);
		}
	}
	
	/**
	 * Hides a file denoted by a path
	 * @param path A path leading to an existing file
	 * @throws IOException If the operation failed
	 */
	public static void hideFile(Path path) throws IOException
	{
		Files.setAttribute(path, "dos:hidden", Boolean.TRUE, LinkOption.NOFOLLOW_LINKS);
	}
	
	/**
	 * Writes a file containing the following lines
	 * @param path The path leading to the file
	 * @param lines The lines written to the file
	 * @param hidden Whether the file should be hidden afterwards
	 * @throws IOException If the operation failed
	 */
	public static void printFile(Path path, Iterable<? extends String> lines, boolean hidden) throws IOException
	{
		// If the file is hidden, removes the old version first
		File file = path.toFile();
		if (hidden)
			file.delete();
		
		try (PrintWriter writer = new PrintWriter(file))
		{
			lines.forEach(writer::println);
			
			// Sets the file permissions for other users as well
			file.setReadable(true, false);
			file.setWritable(true, false);
			
			// May hide the file afterwards
			if (hidden)
				hideFile(path);
		}
	}
	
	/**
	 * Writes a file containing a single line
	 * @param path The path leading to the file
	 * @param line The line written to the file
	 * @param hidden Whether the file should be hidden afterwards
	 * @throws IOException If the operation failed
	 */
	public static void printFile(Path path, String line, boolean hidden) throws IOException
	{
		printFile(path, ImmutableList.withValue(line), hidden);
	}
}
