package flow_test;

import flow_recording.AbstractConstructor;

/**
 * TestConstructor constructs testConstructables
 * 
 * @author Mikko Hilpinen
 * @since 26.11.2014
 */
public class TestConstructor extends AbstractConstructor<TestConstructable>
{
	@Override
	protected TestConstructable createConstructable(String instruction)
	{
		return new TestConstructable(instruction);
	}
}
