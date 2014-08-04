package org.media.container.merge.execution.impl;

import org.media.container.exception.MergeCancelException;
import org.media.container.merge.MergeDefinition;
import org.media.container.merge.execution.MergeExecutor;
import org.media.container.merge.execution.MergeExecutorFactory;
import org.media.container.merge.execution.Merge;
import org.media.container.merge.execution.MergeId;
import org.media.container.merge.execution.MergeListener;
import org.media.container.merge.execution.MergeOperation;
import org.media.container.merge.execution.MergeStatus;

public class MergeTask implements Merge, Runnable {

	//==================================================================================================================
	// Attributes
	//==================================================================================================================

	private final MergeListener listener;
	private final MergeExecutor executor;
	private final MergeId identifier;
	private final MergeDefinition definition;
	private MergeStatus status;

	//==================================================================================================================
	// Constructors
	//==================================================================================================================

	public MergeTask(MergeId id, MergeListener mergeListener, MergeDefinition merge, MergeExecutorFactory factory) {
		identifier = id;
		definition = merge;
		listener = mergeListener;
		executor = factory.create(merge);
		status = MergeStatus.PENDING;
	}

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	@Override
	public MergeId getId() {
		return identifier;
	}

	@Override
	public MergeStatus getStatus() {
		return status;
	}

	@Override
	public String getMessage() {
		return executor.getMessage();
	}

	@Override
	public MergeDefinition getDefinition() {
		return definition;
	}

	@Override
	public void run() {
		try {
			this.setStatus(MergeStatus.RUNNING);
			executor.execute();
			this.setStatus(MergeStatus.COMPLETED);
		} catch (Exception e) {
			this.setStatus(MergeStatus.ERROR);
		} finally {
			executor.dispose();
		}
	}

	public void cancel() throws MergeCancelException {
		try {
			executor.cancel();
		} catch (Exception e) {
			throw new MergeCancelException(e.getMessage(), e);
		}
	}

	//==================================================================================================================
	// Private methods
	//==================================================================================================================

	private void setStatus(MergeStatus newStatus) {
		status = newStatus;
		listener.onChange(MergeOperation.UPDATE, this);
	}

	//==================================================================================================================
	// Object overriding
	//==================================================================================================================

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		MergeTask mergeTask = (MergeTask) o;

		//noinspection RedundantIfStatement
		if (!identifier.equals(mergeTask.identifier)) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return identifier.hashCode();
	}
}
