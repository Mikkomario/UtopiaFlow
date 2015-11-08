package flow_io;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * This fileReader uses modes, special lines that affect how the rest of the lines are read
 * 
 * @author Mikko Hilpinen
 * @since 21.11.2014
 */
public abstract class ModeUsingFileReader extends AbstractFileReader
{
	// ATTRIBUTES	---------------------------------
	
	private String[] modeIndicators;
	private List<String> currentModes;
	
	
	// CONSTRUCTOR	---------------------------------
	
	/**
	 * Creates a new file reader using the given mode indicators
	 * @param modeIndicators The strings that mark each line containing a mode. A mode 
	 * indicated by a string contains all of the following modes indicated by the following 
	 * strings.
	 */
	public ModeUsingFileReader(String[] modeIndicators)
	{
		// Initializes attributes
		this.modeIndicators = modeIndicators;
		this.currentModes = null;
	}
	
	/**
	 * Creates a new file reader using the given mode indicator
	 * @param modeIndicator The string that tells which line should be considered a mode
	 */
	public ModeUsingFileReader(String modeIndicator)
	{
		// Initializes attributes
		this.modeIndicators = new String[1];
		this.modeIndicators[0] = modeIndicator;
		this.currentModes = null;
	}
	
	/**
	 * Creates a new file reader that uses the default mode indicators. The default indicators 
	 * are: "&0:", "&1:", ... , "&9:".
	 */
	public ModeUsingFileReader()
	{
		// Initializes attributes
		this.currentModes = null;
		this.modeIndicators = new String[10];
		for (int i = 0; i < this.modeIndicators.length; i++)
		{
			this.modeIndicators[i] = "&" + i + ":";
		}
	}
	
	
	// ABSTRACT METHODS	----------------------------
	
	/**
	 * This method is called for each line that doen't introduce a new mode.
	 * @param line The line that was read from the file
	 * @param modes The currently active modes that should affect the interpretation
	 */
	protected abstract void onLine(String line, List<String> modes);
	
	/**
	 * This method is called each time a new mode is introduced
	 * @param newMode The mode that was just introduced
	 * @param modes The currently active modes (including the one that was introduced)
	 */
	protected abstract void onMode(String newMode, List<String> modes);
	
	
	// IMPLEMENTED METHODS	-----------------------

	@Override
	protected void onLine(String line)
	{
		// Checks if the line is a mode
		for (int modeIndex = 0; modeIndex < this.modeIndicators.length; modeIndex ++)
		{
			if (line.startsWith(this.modeIndicators[modeIndex]))
			{
				String modeName = line.substring(this.modeIndicators[modeIndex].length());
				this.currentModes.subList(modeIndex, this.currentModes.size()).clear();
				this.currentModes.add(modeName);
				List<String> activeModes = new ArrayList<>();
				activeModes.addAll(this.currentModes);
				onMode(modeName, activeModes);
				return;
			}
		}
		
		// If it wasn't informs the subclass about the line
		List<String> activeModes = new ArrayList<>();
		activeModes.addAll(this.currentModes);
		onLine(line, activeModes);
	}
	
	@Override
	public void readFile(File file, String commentIndicator) throws FileNotFoundException
	{
		// Clears the previous data
		this.currentModes = new ArrayList<String>();
		
		super.readFile(file, commentIndicator);
	}
}
