package org.media.web;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.media.container.info.ContainerFactory;
import org.media.container.info.impl.jebml.JEBMLContainerFactory;
import org.media.container.merge.execution.MergeExecutorFactory;
import org.media.web.authentication.Authenticator;
import org.media.web.config.ApplicationConfiguration;
import org.media.web.config.ConfigurationFactory;
import org.media.web.http.ServerFactories;
import org.media.web.merge.MergeContext;

import javax.ws.rs.ApplicationPath;
import java.net.URI;
import java.nio.file.Paths;

@ApplicationPath("")
public class MediaApplication extends ResourceConfig {

	//==================================================================================================================
	// Constructors
	//==================================================================================================================

	@SuppressWarnings("UnusedDeclaration")
	public MediaApplication() throws Exception {
		this(getConfigurationPath());
	}

	public MediaApplication(String configurationPath) throws Exception {
		this.register(MultiPartFeature.class);
		this.packages(true, "org.media.web");
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

	//==================================================================================================================
	// Main method
	//==================================================================================================================

	public static void main(String[] args) throws Exception {
		final CommandLine commandLine = getCommandLine(args);
		final String port = commandLine.getOptionValue("port", "5010");
		final String config = commandLine.getOptionValue("config", "media-adapter.xml");
		final String server = commandLine.getOptionValue("http", "simple");
		new ServerFactories().getFactory(server).createServer(URI.create("http://localhost:" + port + "/"), new MediaApplication(config));
		//noinspection InfiniteLoopStatement
		while ( true ) Thread.sleep(Long.MAX_VALUE);
	}

	//==================================================================================================================
	// Private methods
	//==================================================================================================================

	@SuppressWarnings("AccessStaticViaInstance")
	private static CommandLine getCommandLine(final String[] args) throws ParseException {
		final Options options = new Options();
		options.addOption(OptionBuilder.withArgName("port").hasArg().withDescription("Port number").create("port"));
		options.addOption(OptionBuilder.withArgName("file").hasArg().withDescription("Configuration file").create("config"));
		options.addOption(OptionBuilder.withArgName("(jdk|simple|jetty)").hasArg().withDescription("HTTP server type").create("http"));
		final CommandLineParser parser = new PosixParser();
		try {
			return parser.parse(options, args);
		} catch (ParseException e) {
			new HelpFormatter().printHelp("java -jar media-adapter-rest.jar", options);
			throw e;
		}
	}

	private static String getConfigurationPath() {
		final String configurationPath = System.getProperty("media.adapter.config");
		if ( configurationPath == null ) {
			throw new IllegalArgumentException("Missing system property 'media.adapter.config'");
		}
		return configurationPath;
	}
}
