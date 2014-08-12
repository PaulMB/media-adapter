package org.media.web.http;

import org.media.web.http.impl.JdkServerFactory;
import org.media.web.http.impl.JettyServerFactory;
import org.media.web.http.impl.SimpleServerFactory;

import java.util.HashMap;
import java.util.Map;

public class ServerFactories {

	//==================================================================================================================
	// Attributes
	//==================================================================================================================

	private Map<String, ServerFactory> factories;

	//==================================================================================================================
	// Constructors
	//==================================================================================================================

	public ServerFactories() {
		this.factories = new HashMap<>();
		this.addFactory(new SimpleServerFactory());
		this.addFactory(new JdkServerFactory());
		this.addFactory(new JettyServerFactory());
	}

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	public void addFactory(ServerFactory factory) {
		if ( factory == null ) {
			throw new IllegalArgumentException("null factory");
		}
		this.factories.put(factory.getName(), factory);
	}

	public ServerFactory getFactory(String name) {
		final ServerFactory serverFactory = this.factories.get(name);
		if ( serverFactory == null ) {
			throw new IllegalArgumentException("HTTP server factory not found for " + name);
		}
		return serverFactory;
	}
}
