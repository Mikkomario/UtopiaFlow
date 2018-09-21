package utopia.flow.test;

/**
 * 
 * @author Mikkomario
 * @since 21.9.2018
 */
public class RunnableTest
{
	@SuppressWarnings("javadoc")
	public static void main(String[] args)
	{
		Runnable r = () -> 
		{
			System.out.println("Running");
		};
		
		new Thread(() -> 
		{
			System.out.println("Start");
			r.run();
			System.out.println("End");
		}).start();
	}

}
