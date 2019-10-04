package utopia.java.flow.test;

import utopia.java.flow.recording.AbstractConstructor;

/**
 * TestConstructor constructs testConstructables
 * 
 * @author Mikko Hilpinen
 * @since 26.11.2014
 * @deprecated Replaced with new generic classes and xml element parsing
 */
public class TestConstructor extends AbstractConstructor<TestConstructable>
{
	@Override
	protected TestConstructable createConstructable(String instruction)
	{
		return new TestConstructable(instruction);
	}
}
