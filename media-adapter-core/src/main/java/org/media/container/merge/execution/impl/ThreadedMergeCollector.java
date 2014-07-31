package org.media.container.merge.execution.impl;

import org.media.container.exception.MergeCancelException;
import org.media.container.exception.MergeDefinitionException;
import org.media.container.exception.MergeNotFoundException;
import org.media.container.exception.MergeStatusException;
import org.media.container.merge.MergeDefinition;
import org.media.container.merge.MergeFactory;
import org.media.container.merge.execution.Merge;
import org.media.container.merge.execution.MergeCollector;
import org.media.container.merge.execution.MergeExecutorFactory;
import org.media.container.merge.execution.MergeId;
import org.media.container.merge.execution.MergeListener;
import org.media.container.merge.execution.MergeOperation;
import org.media.container.merge.execution.MergeStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

public class ThreadedMergeCollector implements MergeCollector, MergeListener {

	//==================================================================================================================
	// Attributes
	//==================================================================================================================

	private final List<MergeListener> listeners;
	private final ExecutorService executorService;
	private final Map<MergeId, MergeTask> merges;
	private final MergeExecutorFactory executorFactory;

	//==================================================================================================================
	// Constructors
	//==================================================================================================================

	public ThreadedMergeCollector(MergeExecutorFactory executorFactory) {
		this.executorFactory = executorFactory;
		this.listeners = new ArrayList<MergeListener>();
		this.executorService = Executors.newSingleThreadExecutor();
		this.merges = new LinkedHashMap<MergeId, MergeTask>();
	}

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	@Override
	public Merge addMerge(MergeDefinition definition) throws MergeDefinitionException {
		final MergeTask merge = new MergeTask(MergeFactory.id(), this, definition, this.executorFactory);
		this.merges.put(merge.getId(), merge);
		this.onChange(MergeOperation.CREATE, merge);
		try {
			this.executorService.submit(merge);
		} catch (RejectedExecutionException e) {
			throw new MergeDefinitionException(e);
		}
		return merge;
	}

	@Override
	public Merge[] getMerges() {
		final Collection<MergeTask> values = merges.values();
		return values.toArray(new MergeTask[values.size()]);
	}

	@Override
	public void removeMerge(MergeId merge) throws MergeNotFoundException, MergeCancelException, MergeStatusException {
		final MergeTask mergeTask = this.getMergeTask(merge);
		switch ( mergeTask.getStatus() ) {
			case PENDING:
				throw new MergeStatusException();
			case RUNNING:
				mergeTask.cancel();
			default:
				this.removeMergeTask(mergeTask);
		}
	}

	@Override
	public void addMergeListener(MergeListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeMergeListener(MergeListener listener) {
		listeners.remove(listener);
	}

	@Override
	public void close() {
		this.executorService.shutdownNow();
	}

	@Override
	public void onChange(MergeOperation operation, Merge merge) {
		for (MergeListener listener : listeners) {
			listener.onChange(operation, merge);
		}
		if ( merge.getStatus() == MergeStatus.COMPLETED ) {
			this.removeMergeTask(merge);
		}
	}

	//==================================================================================================================
	// Private methods
	//==================================================================================================================

	private MergeTask getMergeTask(MergeId merge) throws MergeNotFoundException {
		final MergeTask mergeTask = merges.get(merge);
		if ( mergeTask == null ) {
			throw new MergeNotFoundException();
		}
		return mergeTask;
	}

	private void removeMergeTask(Merge merge) {
		this.merges.remove(merge.getId());
		for (MergeListener listener : listeners) {
			listener.onChange(MergeOperation.DELETE, merge);
		}
	}
}
