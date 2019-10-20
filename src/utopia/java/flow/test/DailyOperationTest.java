package utopia.java.flow.test;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Random;

import utopia.java.flow.async.BackgroundProcessUtils;
import utopia.java.flow.async.Completion;
import utopia.java.flow.structure.ImmutableList;
import utopia.java.flow.structure.Option;
import utopia.java.flow.util.Unit;

/**
 * Tests daily operation repeat as much as is reasonably possible
 * @author Mikko Hilpinen
 * @since 16.8.2019
 */
public class DailyOperationTest
{
	@SuppressWarnings("javadoc")
	public static void main(String[] args)
	{
		Random random = new Random();
		LocalTime now = LocalTime.now();
		
		BackgroundProcessUtils.repeatDaily(() -> System.out.println("Wild-card"), 0, 15);
		
		System.out.println("Scheduling 25 completions");
		ImmutableList<Completion> completions = ImmutableList.filledWith(25,
				() -> now.plusMinutes(random.nextInt(2)).plusSeconds(random.nextInt(60)))
				.map(DailyOperationTest::schedule);
		System.out.println("Waiting for completions to finish (should take about 3 mins max)");
		Option<Unit> result = Completion.ofMany(completions).waitFor(Duration.ofMinutes(4));
		
		if (result.isEmpty())
			System.err.println("Failed to get completion within 4 minutes");
		else
			System.out.println("Completed");
	}
	
	private static Completion schedule(LocalTime time)
	{
		Completion completed = new Completion();
		Runnable task = () -> 
		{
			System.out.println("Completing task " + time);
			completed.fulfill();
		};
		BackgroundProcessUtils.repeatDaily(task, time);
		
		return completed;
	}
}
