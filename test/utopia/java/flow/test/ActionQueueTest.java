package utopia.java.flow.test;

import java.time.Duration;
import java.util.Random;

import utopia.java.flow.async.Promise;
import utopia.java.flow.async.ActionQueue;
import utopia.java.flow.structure.ImmutableList;
import utopia.java.flow.structure.ListBuilder;
import utopia.java.flow.util.Test;
import utopia.java.flow.util.Unit;
import utopia.java.flow.util.WaitUtils;

/**
 * Tests action queue
 * @author Mikko Hilpinen
 * @since 28.3.2019
 */
public class ActionQueueTest
{
	public static void main(String[] args)
	{
		/*
		// Creates the queue
		ActionQueue queue = new ActionQueue(1);
		
		// Creates actions
		ImmutableList<Runnable> actions = IntRange.inclusive(1, 5).mapToList(i -> () ->
		{
			System.out.println("Starting action " + i);
			WaitUtils.wait(Duration.ofSeconds(1), new Object());
			System.out.println("Finishing action " + i);
		});
		
		// Pushes actions to queue
		ImmutableList<Promise<Unit>> completions = actions.map(queue::push);
		
		Completion.ofMany(completions).waitFor();
		System.out.println("All actions completed. Exiting.");
		*/

		/*
		// Tests with limited thread availablility
		implicit val context: ExecutionContext = new ThreadPool("Test", 2, 4).executionContext

		val queue = new ActionQueue(5)

		val genLock = new AnyRef()
		var generatedItems = 0
		val maxGenItems = 1000
		val random = new Random()
		val completionsBuffer = new VectorBuilder[Future[Unit]]()

		def task() = WaitUtils.wait(100.millis, new AnyRef())

		println("Starting tasks")

		// Starts generating actions
		while (generatedItems < maxGenItems)
		{
			completionsBuffer += queue.push { task() }
			generatedItems += 1
			WaitUtils.wait(random.nextInt(10).millis, genLock)
		}

		// Checks the completions
		val completions = completionsBuffer.result()
		assert(completions.size == maxGenItems)

		println("All completions created")
		println(s"${ completions.count { _.isCompleted } } / $maxGenItems items completed")

		val successes = completions.waitForSuccesses()
		assert(successes.size == maxGenItems)
		println("All completed!")
		 */

		ActionQueue queue = new ActionQueue(5);

		Object genLock = new Object();
		int generatedItems = 0;
		int maxGenItems = 1000;
		Random random = new Random();
		ListBuilder<Promise<Unit>> completionsBuffer = new ListBuilder<>();

		Runnable task = () -> WaitUtils.wait(Duration.ofMillis(100), new Object());

		System.out.println("Starting tasks");

		while (generatedItems < maxGenItems)
		{
			completionsBuffer.add(queue.push(task));
			generatedItems += 1;
			WaitUtils.wait(Duration.ofMillis(random.nextInt(10)), genLock);
		}

		ImmutableList<Promise<Unit>> completions = completionsBuffer.result();
		Test.checkEquals(completions.size(), maxGenItems);

		System.out.println("All tasks queued");

		ImmutableList<Unit> successes = completions.map(Promise::waitFor);
		Test.checkEquals(successes.size(), maxGenItems);

		System.out.println("All completed");
	}
}
