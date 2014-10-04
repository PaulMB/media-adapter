package org.media.web.config.impl;

import org.media.container.config.Configuration;
import org.media.container.merge.execution.MergeExecutorFactory;
import org.media.container.merge.io.IOFactory;
import org.media.web.authentication.Authenticator;
import org.media.web.config.exception.ComponentNotFoundException;
import org.media.web.config.exception.ComponentStorageException;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XmlApplicationConfiguration extends AbstractApplicationConfiguration {

	//==================================================================================================================
	// Attributes
	//==================================================================================================================

	private final Path configurationPath;
	private XmlConfiguration xmlConfiguration;
	private Authenticator authenticator;
	private Map<String, ExecutorDescription> executors;

	//==================================================================================================================
	// Constructors
	//==================================================================================================================

	public XmlApplicationConfiguration(Path configurationPath) throws ComponentStorageException {
		this(XmlConfiguration.create(configurationPath), configurationPath);
	}

	public XmlApplicationConfiguration(XmlConfiguration configuration, Path configurationPath) throws ComponentStorageException {
		this.configurationPath = configurationPath;
		this.xmlConfiguration = configuration;
		final XmlComponent xmlAuthenticator = this.xmlConfiguration.getAuthenticator();
		this.authenticator = XmlComponent.createComponent(xmlAuthenticator.getClassName(), xmlAuthenticator.getConfiguration(), Authenticator.class);
		final List<XmlComponent> xmlComponents = this.xmlConfiguration.getExecutors().getExecutors();
		this.executors = new HashMap<>();
		for (XmlComponent xmlComponent : xmlComponents) {
			this.executors.put(xmlComponent.getName(), new ExecutorDescription(xmlComponent));
		}
	}

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	@Override
	public List<String> getExecutors() {
		return new ArrayList<>(executors.keySet());
	}

	@Override
	public MergeExecutorFactory getExecutor(String name) throws ComponentNotFoundException {
		return this.getExecutorDescription(name).getFactory();
	}

	@Override
	public void setExecutorConfiguration(String name, Configuration configuration) throws ComponentNotFoundException, ComponentStorageException {
		final ExecutorDescription description = this.getExecutorDescription(name);
		description.setConfiguration(configuration);
		this.onChange(description.getFactory());
	}

	@Override
	public String getActiveExecutor() {
		return this.xmlConfiguration.getExecutors().getActive();
	}

	@Override
	public void setActiveExecutor(String name) throws ComponentNotFoundException, ComponentStorageException {
		final MergeExecutorFactory executor = this.getExecutor(name);
		this.xmlConfiguration.getExecutors().setActive(name);
		this.onChange(executor);
		this.save();
	}

	@Override
	public Authenticator getAuthenticator() {
		return this.authenticator;
	}

	//==================================================================================================================
	// Private methods
	//==================================================================================================================

	private ExecutorDescription getExecutorDescription(String name) throws ComponentNotFoundException {
		final ExecutorDescription description = this.executors.get(name);
		if ( description == null ) {
			throw new ComponentNotFoundException(name + " not found");
		}
		return description;
	}

	private void save() throws ComponentStorageException {
		try {
			IOFactory.save(this.xmlConfiguration, this.configurationPath);
		} catch (IOException e) {
			throw new ComponentStorageException(e.getMessage());
		}
	}

	private class ExecutorDescription {

		private XmlComponent component;
		private MergeExecutorFactory factory;

		public ExecutorDescription(XmlComponent component) throws ComponentStorageException {
			this.component = component;
			this.factory = this.createFactory();
		}

		public void setConfiguration(Configuration configuration) throws ComponentStorageException {
			try {
				IOFactory.save(configuration, Paths.get(this.component.getConfiguration()));
				this.factory = this.createFactory();
			} catch (IOException e) {
				throw new ComponentStorageException(e.getMessage());
			}
		}

		public MergeExecutorFactory getFactory() {
			return factory;
		}

		private MergeExecutorFactory createFactory() throws ComponentStorageException {
			try {
				return XmlComponent.createComponent(component.getClassName(), component.getConfiguration(), MergeExecutorFactory.class);
			} catch (Exception e) {
				throw new ComponentStorageException(e.getMessage());
			}
		}
	}
}
