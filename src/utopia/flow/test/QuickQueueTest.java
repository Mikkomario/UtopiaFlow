package utopia.flow.test;

import java.time.Duration;
import java.util.Random;

import utopia.flow.async.Completion;
import utopia.flow.async.Queue;
import utopia.flow.structure.ImmutableList;
import utopia.flow.structure.ListBuilder;
import utopia.flow.util.Test;
import utopia.flow.util.WaitUtils;

/**
 * Tests queues in a crowded, asynchronous environment
 * @author Mikko Hilpinen
 * @since 14.5.2019
 */
public class QuickQueueTest
{
	
	@SuppressWarnings("javadoc")
	public static void main(String[] args)
	{
		Queue queue = new Queue(5);
		
		Object genLock = new Object();
		int generatedItems = 0;
		int maxGenItems = 1000;
		Random random = new Random();
		ListBuilder<Completion> completionsBuffer = new ListBuilder<>(maxGenItems);
		
		while (generatedItems < maxGenItems)
		{
			completionsBuffer.add(queue.push(QuickQueueTest::task));
			generatedItems += 1;
			WaitUtils.wait(Duration.ofMillis(random.nextInt(10)), genLock);
		}
		
		ImmutableList<Completion> completions = completionsBuffer.build();
		Test.check(completions.size() == maxGenItems);
		
		System.out.println("All completions created");
		System.out.println(completions.count(c -> c.isFulfilled()) + "/" + maxGenItems + " items completed");
		
		Completion allCompleted = Completion.ofMany(completions);
		allCompleted.waitFor();
		System.out.println("All completed!");
	}
	
	private static void task()
	{
		System.out.println("Processing");
		WaitUtils.wait(Duration.ofMillis(5), new Object());
	}
}
