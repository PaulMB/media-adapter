package org.media.container.merge.execution;

import org.media.container.config.Configurable;
import org.media.container.merge.MergeDefinition;

public interface MergeExecutorFactory extends Configurable {

	MergeExecutor create(MergeDefinition definition);
}
