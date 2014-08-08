package org.media.web.authentication;

import org.apache.commons.exec.Executor;

import java.io.OutputStream;

public interface DSMExecutorFactory {

	Executor createExecutor(OutputStream outputStream);
}
