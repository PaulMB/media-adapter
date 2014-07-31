package org.media.container.merge.execution;

import org.media.container.exception.MergeCancelException;
import org.media.container.exception.MergeNotFoundException;
import org.media.container.exception.MergeStatusException;
import org.media.container.exception.MergeSubmitException;
import org.media.container.merge.MergeDefinition;

public interface MergeCollector {

	Merge addMerge(MergeDefinition merge) throws MergeSubmitException;

	Merge[] getMerges();

	void removeMerge(MergeId merge) throws MergeNotFoundException, MergeStatusException, MergeCancelException;

	void addMergeListener(MergeListener listener);

	void removeMergeListener(MergeListener listener);

	void close();

}
