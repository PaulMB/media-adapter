package org.media.container.merge.execution.impl.mkvmerge;

import org.media.container.config.Configuration;
import org.media.container.info.impl.jebml.JEBMLContainerFactory;
import org.media.container.merge.MergeDefinition;
import org.media.container.merge.execution.MergeExecutor;
import org.media.container.merge.execution.MergeExecutorFactory;
import org.media.container.merge.execution.impl.command.CommandExecutor;
import org.media.container.merge.io.CommandConfiguration;
import org.media.container.merge.io.IOFactory;

import java.io.IOException;
import java.nio.file.Path;

@SuppressWarnings("UnusedDeclaration")
public class MkvMergeExecutorFactory implements MergeExecutorFactory {

	//==================================================================================================================
	// Attributes
	//==================================================================================================================

	private final CommandConfiguration configuration;

	//==================================================================================================================
	// Constructors
	//==================================================================================================================

	public MkvMergeExecutorFactory(Path configuration) throws IOException {
		this.configuration = IOFactory.loadConfiguration(configuration);
	}

	public MkvMergeExecutorFactory(CommandConfiguration configuration) {
		this.configuration = configuration;
	}

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	@Override
	public MergeExecutor create(MergeDefinition definition) {
		return new CommandExecutor(definition, configuration.getEnvironment(), new MkvMergeCommandBuilder(configuration, new JEBMLContainerFactory()));
	}

	@Override
	public Configuration getConfiguration() {
		return configuration;
	}
}
