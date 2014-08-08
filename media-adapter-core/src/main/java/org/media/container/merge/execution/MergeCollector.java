package org.media.container.merge.execution;

import org.media.container.exception.MergeCancelException;
import org.media.container.exception.MergeDefinitionException;
import org.media.container.exception.MergeNotFoundException;
import org.media.container.merge.MergeDefinition;

public interface MergeCollector {

	Merge addMerge(MergeDefinition merge) throws MergeDefinitionException;

	Merge[] getMerges();

	void removeMerge(MergeId merge) throws MergeNotFoundException, MergeCancelException;

	void addMergeListener(MergeListener listener);

	void removeMergeListener(MergeListener listener);

	void close();

}
