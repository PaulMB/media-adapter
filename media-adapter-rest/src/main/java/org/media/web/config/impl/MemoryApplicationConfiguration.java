package org.media.web.config.impl;

import org.media.container.config.Configuration;
import org.media.container.merge.execution.MergeExecutorFactory;
import org.media.container.merge.execution.impl.ffmpeg.FFMpegExecutorFactory;
import org.media.container.merge.execution.impl.mkvmerge.MkvMergeExecutorFactory;
import org.media.container.merge.io.CommandConfiguration;
import org.media.container.merge.io.IOFactory;
import org.media.web.authentication.Authenticator;
import org.media.web.authentication.NoAuthentication;
import org.media.web.config.exception.ComponentNotFoundException;
import org.media.web.config.exception.ComponentStorageException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoryApplicationConfiguration extends AbstractApplicationConfiguration {

	//==================================================================================================================
	// Attributes
	//==================================================================================================================

	private String active;
	private final Map<String, MergeExecutorFactory> factories;

	//==================================================================================================================
	// Constructors
	//==================================================================================================================

	public MemoryApplicationConfiguration() {
		this("ffmpeg", createDefault());
	}

	public MemoryApplicationConfiguration(String active, Map<String, MergeExecutorFactory> factories) {
		this.active = active;
		this.factories = factories;
	}

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	@Override
	public List<String> getExecutors() {
		return new ArrayList<>(factories.keySet());
	}

	@Override
	public MergeExecutorFactory getExecutor(String name) throws ComponentNotFoundException {
		final MergeExecutorFactory factory = this.factories.get(name);
		if ( factory == null ) {
			throw new ComponentNotFoundException(name + " not found");
		}
		return factory;
	}

	@Override
	public void setExecutorConfiguration(String name, Configuration configuration) throws ComponentNotFoundException, ComponentStorageException {
		final MergeExecutorFactory executor = this.getExecutor(name);
		try {
			final MergeExecutorFactory newExecutor = executor.getClass().getConstructor(CommandConfiguration.class).newInstance((CommandConfiguration) configuration);
			this.factories.put(name, newExecutor);
			this.onChange(newExecutor);
		} catch (Exception e) {
			throw new ComponentNotFoundException(e.getMessage());
		}
	}

	@Override
	public String getActiveExecutor() {
		return active;
	}

	@Override
	public void setActiveExecutor(String name) throws ComponentNotFoundException, ComponentStorageException {
		final MergeExecutorFactory executor = this.getExecutor(name);
		active = name;
		this.onChange(executor);
	}

	@Override
	public Authenticator getAuthenticator() {
		return new NoAuthentication();
	}

	//==================================================================================================================
	// Private methods
	//==================================================================================================================

	private static Map<String, MergeExecutorFactory> createDefault() {
		final HashMap<String, MergeExecutorFactory> factories = new HashMap<>();
		final CommandConfiguration mkvmerge = IOFactory.createConfiguration("mkvmerge");
		mkvmerge.getEnvironment().put("LC_ALL", "C");
		mkvmerge.getEnvironment().put("LANG", "C");
		factories.put("mkvmerge", new MkvMergeExecutorFactory(mkvmerge));
		factories.put("ffmpeg", new FFMpegExecutorFactory(IOFactory.createConfiguration("ffmpeg")));
		return factories;
	}
}
