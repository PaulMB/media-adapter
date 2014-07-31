package org.media.container.merge.execution.impl.mkvmerge;

import org.media.container.merge.MergeDefinition;
import org.media.container.merge.execution.MergeExecutor;
import org.media.container.merge.execution.MergeExecutorFactory;
import org.media.container.merge.io.CommandConfiguration;
import org.media.container.merge.io.IOFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CommandExecutorFactory implements MergeExecutorFactory {

	//==================================================================================================================
	// Attributes
	//==================================================================================================================

	private final CommandConfiguration configuration;

	//==================================================================================================================
	// Constructors
	//==================================================================================================================

	@SuppressWarnings("UnusedDeclaration")
	public CommandExecutorFactory(Path configuration) throws IOException {
		this.configuration = IOFactory.loadConfiguration(configuration);
	}

	public CommandExecutorFactory(CommandConfiguration configuration) {
		this.configuration = configuration;
	}

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	@Override
	public MergeExecutor create(MergeDefinition definition) {
		return new CommandExecutor(definition, configuration);
	}
}
