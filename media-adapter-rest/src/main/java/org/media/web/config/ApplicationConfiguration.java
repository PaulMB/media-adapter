package org.media.web.config;

import org.media.container.config.Configuration;
import org.media.container.config.ListenerCollector;
import org.media.container.merge.execution.MergeExecutorFactory;
import org.media.web.authentication.Authenticator;
import org.media.web.config.exception.ComponentNotFoundException;
import org.media.web.config.exception.ComponentStorageException;

import java.util.List;

public interface ApplicationConfiguration extends ListenerCollector<MergeExecutorFactory> {

	List<String> getExecutors();

	MergeExecutorFactory getExecutor(String name) throws ComponentNotFoundException;

	void setExecutorConfiguration(String name, Configuration configuration) throws ComponentNotFoundException, ComponentStorageException;

	String getActiveExecutor();

	void setActiveExecutor(String name) throws ComponentNotFoundException, ComponentStorageException;

	Authenticator getAuthenticator();
}
