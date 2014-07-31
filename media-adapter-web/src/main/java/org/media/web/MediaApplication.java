package org.media.web;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.media.container.info.ContainerFactory;
import org.media.container.info.impl.jebml.JEBMLContainerFactory;
import org.media.container.merge.execution.MergeExecutorFactory;
import org.media.web.authentication.Authenticator;
import org.media.web.config.ApplicationConfiguration;
import org.media.web.config.ConfigurationFactory;
import org.media.web.merge.MergeContext;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import java.nio.file.Paths;

public class MediaApplication extends ResourceConfig {

	//==================================================================================================================
	// Constructors
	//==================================================================================================================

	public MediaApplication(@Context ServletContext context) throws Exception {
		this.register(MultiPartFeature.class);
		final String configurationPath = context.getInitParameter("media-adapter-configuration");
		if ( configurationPath == null ) {
			throw new IllegalArgumentException("missing init parameter 'media-adapter-configuration'");
		}
		final ApplicationConfiguration configuration = ConfigurationFactory.loadConfiguration(Paths.get(configurationPath));
		final MergeExecutorFactory executorFactory = ConfigurationFactory.createComponent(configuration.getExecutorFactory(), MergeExecutorFactory.class);
		final Authenticator authenticator = ConfigurationFactory.createComponent(configuration.getAuthenticator(), Authenticator.class);
		this.register(new AbstractBinder() {
			@Override
			protected void configure() {
				bind(new MergeContext(executorFactory)).to(MergeContext.class);
				bind(authenticator).to(Authenticator.class);
				bind(new JEBMLContainerFactory()).to(ContainerFactory.class);
			}
		});
	}
}
