package org.media.web.http.impl;

import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.media.web.http.ServerFactory;

import java.net.URI;

public class JdkServerFactory implements ServerFactory {

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	@Override
	public String getName() {
		return "jdk";
	}

	@Override
	public void createServer(URI uri, ResourceConfig resourceConfig) {
		JdkHttpServerFactory.createHttpServer(uri, resourceConfig);
	}
}
