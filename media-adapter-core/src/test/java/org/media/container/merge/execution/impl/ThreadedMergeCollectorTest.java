package org.media.container.merge.execution.impl;

import org.junit.Test;
import org.media.container.exception.MergeCancelException;
import org.media.container.exception.MergeNotFoundException;
import org.media.container.exception.MergeStatusException;
import org.media.container.exception.MergeSubmitException;
import org.media.container.merge.MergeDefinition;
import org.media.container.merge.MergeFactory;
import org.media.container.merge.execution.Merge;
import org.media.container.merge.execution.MergeCollector;
import org.media.container.merge.execution.MergeExecutor;
import org.media.container.merge.execution.MergeExecutorFactory;
import org.media.container.merge.execution.MergeListener;
import org.media.container.merge.execution.MergeOperation;
import org.media.container.merge.execution.MergeStatus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Semaphore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class ThreadedMergeCollectorTest {

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	@Test
	public void shouldReceiveMergeEvents() throws Exception {
		final MergeCollector collector = collector(executor("test1"));
		final SampleListener listener = new SampleListener(
				merge(MergeOperation.CREATE, "test1", MergeStatus.PENDING),
				merge(MergeOperation.UPDATE, "test1", MergeStatus.RUNNING),
				merge(MergeOperation.UPDATE, "test1", MergeStatus.COMPLETED),
				merge(MergeOperation.DELETE, "test1", MergeStatus.COMPLETED)
		);
		collector.addMergeListener(listener);
		collector.addMerge(definition());
		assertEquals(1, collector.getMerges().length);
		listener.waitForTermination();
		assertEquals(0, collector.getMerges().length);
	}

	@Test
	public void shouldExecuteTwoMerge() throws Exception {
		final WaitMergeExecutor waitMergeExecutor = new WaitMergeExecutor("test1");
		final MergeCollector collector = collector(waitMergeExecutor, executor("test2"));
		final Semaphore firstMergeWaiter = new Semaphore(1);
		firstMergeWaiter.acquire();
		final SampleListener listener = new SampleListener(
				merge(MergeOperation.CREATE, "test1", MergeStatus.PENDING),
				merge(MergeOperation.UPDATE, "test1", MergeStatus.RUNNING),
				merge(MergeOperation.CREATE, "test2", MergeStatus.PENDING),
				merge(MergeOperation.UPDATE, "test1", MergeStatus.COMPLETED),
				merge(MergeOperation.DELETE, "test1", MergeStatus.COMPLETED),
				merge(MergeOperation.UPDATE, "test2", MergeStatus.RUNNING),
				merge(MergeOperation.UPDATE, "test2", MergeStatus.COMPLETED),
				merge(MergeOperation.DELETE, "test2", MergeStatus.COMPLETED)
		);
		collector.addMergeListener(listener);
		collector.addMergeListener(new MergeListener() {
			@Override
			public void onChange(MergeOperation operation, Merge merge) {
				if ( merge.getMessage().equals("test1") && merge.getStatus() == MergeStatus.RUNNING ) {
					firstMergeWaiter.release();
				}
			}
		});
		collector.addMerge(definition());
		firstMergeWaiter.acquire();
		collector.addMerge(definition());
		waitMergeExecutor.release();
		assertEquals(2, collector.getMerges().length);
		listener.waitForTermination();
		assertEquals(0, collector.getMerges().length);
	}

	@Test
	public void shouldFailOnOneMerge() throws Exception {
		final MergeCollector collector = collector(executorExecutionFail("test1"));
		final SampleListener listener = new SampleListener(
				merge(MergeOperation.CREATE, "test1", MergeStatus.PENDING),
				merge(MergeOperation.UPDATE, "test1", MergeStatus.RUNNING),
				merge(MergeOperation.UPDATE, "test1", MergeStatus.ERROR)
		);
		collector.addMergeListener(listener);
		collector.addMerge(definition());
		assertEquals(1, collector.getMerges().length);
		listener.waitForTermination();
		assertEquals(1, collector.getMerges().length);
		assertEquals("test1", collector.getMerges()[0].getMessage());
	}

	@Test
	public void shouldRemoveFailedMerge() throws Exception {
		final MergeCollector collector = collector(executorExecutionFail("test1"));
		final SampleListener listener = new SampleListener(
				merge(MergeOperation.CREATE, "test1", MergeStatus.PENDING),
				merge(MergeOperation.UPDATE, "test1", MergeStatus.RUNNING),
				merge(MergeOperation.UPDATE, "test1", MergeStatus.ERROR)
		);
		collector.addMergeListener(listener);
		collector.addMerge(definition());
		listener.waitForTermination();
		listener.addExpected(merge(MergeOperation.DELETE, "test1", MergeStatus.ERROR));
		collector.removeMerge(collector.getMerges()[0].getId());
		listener.waitForTermination();
		assertEquals(0, collector.getMerges().length);
	}

	@Test
	public void shouldNotReceiveEventForRemovedListener() throws Exception {
		final MergeListener listener = new MergeListener() {
			@Override
			public void onChange(MergeOperation operation, Merge merge) {
				fail();
			}
		};
		final WaitMergeExecutor mergeExecutor = new WaitMergeExecutor("test1");
		final MergeCollector collector = collector(mergeExecutor);
		collector.addMergeListener(listener);
		collector.removeMergeListener(listener);
		collector.addMerge(definition());
		mergeExecutor.release();
	}

	@Test(expected = MergeSubmitException.class)
	public void shouldFailWhenCollectorIsClosed() throws Exception {
		final MergeCollector collector = collector(executor("test"));
		collector.close();
		collector.addMerge(definition());
	}

	@Test(expected = MergeStatusException.class)
	public void shouldFailOnRemovingPendingMerge() throws Exception {
		final MergeCollector collector = collector(new WaitMergeExecutor("test1"), executor("test2"));
		collector.addMerge(definition());
		collector.addMerge(definition());
		collector.removeMerge(collector.getMerges()[1].getId());
	}

	@Test(expected = MergeCancelException.class)
	public void shouldThrowExceptionWhenCancelFails() throws Exception {
		final MergeCollector collector = collector(new WaitMergeExecutor("test1"));
		final Semaphore semaphore = new Semaphore(1);
		semaphore.acquire();
		collector.addMergeListener(new MergeListener() {
			@Override
			public void onChange(MergeOperation operation, Merge merge) {
				if ( merge.getStatus() == MergeStatus.RUNNING ) {
					semaphore.release();
				}
			}
		});
		collector.addMerge(definition());
		semaphore.acquire();
		collector.removeMerge(collector.getMerges()[0].getId());
	}

	@Test(expected = MergeNotFoundException.class)
	public void shouldFailWhenRemovingUnknownMerge() throws Exception {
		collector(executor("test1")).removeMerge(MergeFactory.id());
	}

	//==================================================================================================================
	// Private methods
	//==================================================================================================================

	private static MergeCollector collector(MergeExecutor... executors) throws InterruptedException {
		return new ThreadedMergeCollector(new SampleExecutorFactory(executors));
	}

	private static MergeDefinition definition() {
		return MergeFactory.merge(new File("/a/b/c"), new File("/d/e"));
	}

	private static MergeEvent merge(MergeOperation operation, String message, MergeStatus status) {
		return new MergeEvent(operation, message, status);
	}

	private static MergeExecutor executor(String message) throws Exception {
		final MergeExecutor executor = mock(MergeExecutor.class);
		when(executor.getMessage()).thenReturn(message);
		doNothing().when(executor).cancel();
		doNothing().when(executor).execute();
		return executor;
	}

	private static MergeExecutor executorExecutionFail(String message) throws Exception {
		final MergeExecutor executor = mock(MergeExecutor.class);
		when(executor.getMessage()).thenReturn(message);
		doNothing().when(executor).cancel();
		doThrow(new IOException()).when(executor).execute();
		return executor;
	}

	//==================================================================================================================
	// Private class
	//==================================================================================================================

	private static class MergeEvent {

		private MergeOperation operation;
		private String message;
		private MergeStatus status;

		private MergeEvent(MergeOperation operation, String message, MergeStatus status) {
			this.operation = operation;
			this.message = message;
			this.status = status;
		}

		public MergeOperation getOperation() {
			return operation;
		}

		public MergeStatus getStatus() {
			return status;
		}

		public String getMessage() {
			return message;
		}
	}

	private static class SampleListener implements MergeListener {

		private List<MergeEvent> expected = new ArrayList<MergeEvent>();

		private SampleListener(MergeEvent... expectedEvents) {
			this.addExpected(expectedEvents);
		}

		@Override
		public synchronized void onChange(MergeOperation operation, Merge merge) {
			final MergeEvent remove = expected.remove(0);
			assertEquals(remove.getMessage(), merge.getMessage());
			assertEquals(remove.getStatus(), merge.getStatus());
			assertEquals(remove.getOperation(), operation);
			this.notifyAll();
		}

		public void addExpected(MergeEvent... events) {
			expected.addAll(Arrays.asList(events));
		}

		public synchronized void waitForTermination() throws InterruptedException {
			while ( ! expected.isEmpty() ) {
				this.wait();
			}
		}
	}

	private static class WaitMergeExecutor implements MergeExecutor {

		private final Semaphore semaphore = new Semaphore(1);
		private final String message;

		private WaitMergeExecutor(String message) throws InterruptedException {
			this.semaphore.acquire();
			this.message = message;
		}

		@Override
		public void execute() throws Exception {
			semaphore.acquire();
		}

		@Override
		public void cancel() throws Exception {
			throw new IOException();
		}

		@Override
		public void dispose() {
		}

		@Override
		public String getMessage() {
			return message;
		}

		public void release() throws Exception {
			semaphore.release();
		}
	}

	private static class SampleExecutorFactory implements MergeExecutorFactory {

		private final List<MergeExecutor> executors = new ArrayList<MergeExecutor>();

		private SampleExecutorFactory(MergeExecutor... mergeExecutors) {
			executors.addAll(Arrays.asList(mergeExecutors));
		}

		@Override
		public MergeExecutor create(MergeDefinition definition) {
			return executors.remove(0);
		}
	}
}