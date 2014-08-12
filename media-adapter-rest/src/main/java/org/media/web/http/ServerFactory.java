package org.media.web.http;

import org.glassfish.jersey.server.ResourceConfig;

import java.net.URI;

public interface ServerFactory {

	String getName();

	void createServer(URI uri, ResourceConfig resourceConfig);

}
