package org.media.container.merge.execution;

public interface MergeExecutor {

	void execute() throws Exception;

	void cancel() throws Exception;

	void dispose();

	String getMessage();
}
