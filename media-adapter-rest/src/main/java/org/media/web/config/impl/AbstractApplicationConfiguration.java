package org.media.web.config.impl;

import org.media.container.config.Listener;
import org.media.container.merge.execution.MergeExecutorFactory;
import org.media.web.config.ApplicationConfiguration;

import java.util.ArrayList;
import java.util.List;

abstract public class AbstractApplicationConfiguration implements ApplicationConfiguration, Listener<MergeExecutorFactory> {

	//==================================================================================================================
	// Attributes
	//==================================================================================================================

	private final List<Listener<MergeExecutorFactory>> listeners;

	//==================================================================================================================
	// Constructors
	//==================================================================================================================

	protected AbstractApplicationConfiguration() {
		this.listeners = new ArrayList<>();
	}

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	@Override
	public void addListener(Listener<MergeExecutorFactory> listener) {
		this.listeners.add(listener);
	}

	@Override
	public void onChange(MergeExecutorFactory factory) {
		for (Listener<MergeExecutorFactory> listener : listeners) {
			listener.onChange(factory);
		}
	}
}
