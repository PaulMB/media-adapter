package org.media.container.merge.execution;

import org.media.container.merge.MergeDefinition;

public interface MergeExecutorFactory {

	MergeExecutor create(MergeDefinition definition);
}
