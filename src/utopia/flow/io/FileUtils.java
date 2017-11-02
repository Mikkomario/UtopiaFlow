package utopia.flow.io;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Collection;

import utopia.flow.util.Filter;

/**
 * This is a static collection of methods that provide utility when dealing with files
 * @author Mikko Hilpinen
 * @since 14.5.2016
 */
public class FileUtils
{
	// CONSTRUCTOR	------------
	
	private FileUtils()
	{
		// The interface is static
	}

	
	// OTHER METHODS	--------
	
	/**
	 * Deletes a directory and all of its contents
	 * @param directory A directory
	 */
	public static void deleteDirectory(File directory)
	{
		if (!directory.isDirectory())
			return;
		
		for (File file : directory.listFiles())
		{
			if (file.isDirectory())
				deleteDirectory(file);
			else
				file.delete();
		}
	}
	
	/**
	 * Finds all of the names of the files under the provided directory path that are written 
	 * in a certain format
	 * @param directory The directory that is checked
	 * @param format The format the files should have in order to be selected
	 * @return All file names (no leading path included) that have the provided format 
	 * under the provided directory. Null if no such directory exists or if an IO error occurred
	 */
	public static String[] findFileNamesIn(File directory, String format)
	{
		FilenameFilter filter = new FilenameFilter()
		{
			@Override
			public boolean accept(File dir, String name)
			{
				return name.endsWith("." + format) || 
						name.endsWith("." + format.toLowerCase()) || 
						name.endsWith("." + format.toUpperCase());
			}
		};
		
		return directory.list(filter);
	}
	
	/**
	 * Finds all of the names of the files under the provided directory path that are written 
	 * in a certain format
	 * @param directory The directory that is checked
	 * @param nameFilter the filter that is applied to the files to determine if they would get selected
	 * @return All file names (no leading path included) that have the provided format 
	 * under the provided directory. Null if no such directory exists or if an IO error occurred
	 */
	public static String[] findFileNamesIn(File directory, Filter<String> nameFilter)
	{
		FilenameFilter filter = new FilenameFilter()
		{
			@Override
			public boolean accept(File dir, String name)
			{
				return nameFilter.includes(name);
			}
		};
		
		return directory.list(filter);
	}
	
	/**
	 * Changes the hidden attribute of a file denoted by a path
	 * @param path A path leading to an existing file
	 * @param hidden Should the file be a hidden file. One may not be able to write into a 
	 * hidden file.
	 * @throws IOException If the operation failed
	 */
	public static void setHidden(Path path, boolean hidden) throws IOException
	{
		Files.setAttribute(path, "dos:hidden", Boolean.valueOf(hidden), LinkOption.NOFOLLOW_LINKS);
	}
	
	/**
	 * Checks whether a file is hidden
	 * @param path A path leading to a file
	 * @return Is the file a hidden file
	 * @throws IOException If an io error occurs
	 */
	public static boolean isHidden(Path path) throws IOException
	{
		// if the file doesn't exist, it can't be hidden
		if (path.toFile().exists())
			return Files.getAttribute(path, "dos:hidden", 
					LinkOption.NOFOLLOW_LINKS).equals(Boolean.TRUE);
		else
			return false;
	}
	
	/**
	 * Writes a file containing the following lines
	 * @param path The path leading to the file
	 * @param lines The lines written to the file
	 * @param hidden Whether the file should be hidden afterwards
	 * @throws IOException If the operation failed
	 */
	public static void printFile(Path path, Collection<? extends String> lines, 
			boolean hidden) throws IOException
	{
		// If the file is hidden, removes that attribute for the duration of the write
		File file = path.toFile();
		if (isHidden(path))
			setHidden(path, false);
		
		PrintWriter writer = null;
		try
		{
			writer = new PrintWriter(file);
			for (String line : lines)
			{
				writer.println(line);
			}
			
			// May hide the file afterwards
			//Object hiddenStatus = Files.getAttribute(path, "dos:hidden", LinkOption.NOFOLLOW_LINKS);
			if (hidden)
				setHidden(path, true);
		}
		finally
		{
			if (writer != null)
				writer.close();
		}
	}
	
	/**
	 * Writes a file containing a single line
	 * @param path The path leading to the file
	 * @param line The line written to the file
	 * @param hidden Whether the file should be hidden afterwards
	 * @throws IOException If the operation failed
	 */
	/*
	public static void printFile(Path path, String line, boolean hidden) throws IOException
	{
		Collection<String> lines = new ArrayList<>();
		lines.add(line);
		printFile(path, lines, hidden);
	}
	*/
}
