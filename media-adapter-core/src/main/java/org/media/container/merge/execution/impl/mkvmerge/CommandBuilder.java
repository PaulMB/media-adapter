package org.media.container.merge.execution.impl.mkvmerge;

import org.apache.commons.exec.CommandLine;
import org.media.container.merge.MergeDefinition;
import org.media.container.merge.TrackDefinition;

public class CommandBuilder {

	//==================================================================================================================
	// Attributes
	//==================================================================================================================

	private final CommandLine commandLine;
	private final MergeDefinition definition;

	//==================================================================================================================
	// Constructors
	//==================================================================================================================

	public CommandBuilder(MergeDefinition mergeDefinition) {
		this(mergeDefinition, "mkvmerge");
	}

	public CommandBuilder(MergeDefinition mergeDefinition, String executable) {
		definition = mergeDefinition;
		commandLine = new CommandLine(executable);
		createCommandLine();
	}

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	public CommandLine getCommandLine() {
		return commandLine;
	}

	//==================================================================================================================
	// Private methods
	//==================================================================================================================

	private void createCommandLine() {
		commandLine.addArgument("-o");
		commandLine.addArgument(definition.getOutput().getAbsolutePath(), false);
		commandLine.addArgument(definition.getInput().getAbsolutePath(), false);

		if ( definition.isClustersInMetaSeek() ) {
			commandLine.addArgument("--clusters-in-meta-seek");
		}
		final TrackDefinition[] tracks = definition.getTracks();
		for (TrackDefinition track : tracks) {
			track.accept(new CommandTrackBuilder(commandLine));
		}
	}
}
