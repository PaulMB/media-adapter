package org.media.container.merge.execution;

import org.media.container.merge.MergeDefinition;

public interface Merge {

	MergeId getId();

	MergeStatus getStatus();

	String getMessage();

	MergeDefinition getDefinition();
}
