package org.media.web.http.impl;

import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.media.web.http.ServerFactory;

import java.net.URI;

public class JettyServerFactory implements ServerFactory {

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	@Override
	public String getName() {
		return "jetty";
	}

	@Override
	public void createServer(URI uri, ResourceConfig resourceConfig) {
		JettyHttpContainerFactory.createServer(uri, resourceConfig);
	}
}
