package org.media.web.http.impl;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.simple.SimpleContainerFactory;
import org.media.web.http.ServerFactory;

import java.net.URI;

public class SimpleServerFactory implements ServerFactory {

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	@Override
	public String getName() {
		return "simple";
	}

	@Override
	public void createServer(URI uri, ResourceConfig resourceConfig) {
		SimpleContainerFactory.create(uri, resourceConfig);
	}
}
