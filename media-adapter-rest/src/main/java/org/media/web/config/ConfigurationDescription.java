package org.media.web.config;

import org.media.container.config.Configuration;
import org.media.web.config.exception.ComponentNotFoundException;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnusedDeclaration")
public class ConfigurationDescription {

	//==================================================================================================================
	// Attributes
	//==================================================================================================================

	private final String active;
	private final List<ComponentConfiguration> executors;

	//==================================================================================================================
	// Constructors
	//==================================================================================================================

	public ConfigurationDescription(ApplicationConfiguration configuration) throws ComponentNotFoundException {
		this.active = configuration.getActiveExecutor();
		this.executors = new ArrayList<>();
		final List<String> configurationExecutors = configuration.getExecutors();
		for (String executor : configurationExecutors) {
			this.executors.add(new ComponentConfiguration(executor, configuration.getExecutor(executor).getConfiguration()));
		}
	}

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	public String getActive() {
		return active;
	}

	public List<ComponentConfiguration> getExecutors() {
		return executors;
	}

	private class ComponentConfiguration {
		private final String name;
		private final Configuration configuration;

		private ComponentConfiguration(String name, Configuration configuration) {
			this.name = name;
			this.configuration = configuration;
		}

		public String getName() {
			return name;
		}

		public Configuration getConfiguration() {
			return configuration;
		}
	}
}
