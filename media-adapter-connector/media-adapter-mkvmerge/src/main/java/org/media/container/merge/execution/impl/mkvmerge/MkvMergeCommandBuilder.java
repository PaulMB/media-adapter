package org.media.container.merge.execution.impl.mkvmerge;

import org.apache.commons.exec.CommandLine;
import org.media.container.exception.MediaReadException;
import org.media.container.info.Container;
import org.media.container.info.ContainerFactory;
import org.media.container.info.Track;
import org.media.container.info.TrackFilterFactory;
import org.media.container.info.TrackId;
import org.media.container.info.TrackType;
import org.media.container.merge.MergeDefinition;
import org.media.container.merge.TrackDefinition;
import org.media.container.merge.execution.impl.command.CommandLineBuilder;
import org.media.container.merge.io.CommandConfiguration;
import org.media.container.merge.util.MergeUtil;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class MkvMergeCommandBuilder implements CommandLineBuilder {

	//==================================================================================================================
	// Attributes
	//==================================================================================================================

	private final ContainerFactory containerFactory;
	private final Path binary;

	//==================================================================================================================
	// Constructors
	//==================================================================================================================

	public MkvMergeCommandBuilder(CommandConfiguration configuration, ContainerFactory factory) {
		binary = configuration.getBinary();
		containerFactory = factory;
	}

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	@Override
	public CommandLine getCommandLine(MergeDefinition definition) throws IOException {
		try {
			return this.appendContainerOptions(new CommandLine(binary.toString()), definition);
		} catch (MediaReadException e) {
			throw new IOException(e.getMessage(), e);
		}
	}

	//==================================================================================================================
	// Private methods
	//==================================================================================================================

	private CommandLine appendContainerOptions(CommandLine commandLine, MergeDefinition definition) throws MediaReadException {
		final Container container = containerFactory.create(definition.getInput().toURI());
		commandLine.addArgument("-o");
		commandLine.addArgument(definition.getOutput().getAbsolutePath(), false);
		final TrackRemovalAppender appender = new TrackRemovalAppender(commandLine, container, definition);
		appender.appendTrackRemoval(TrackType.SUBTITLE, "-s", "-S");
		appender.appendTrackRemoval(TrackType.AUDIO, "-a", "-A");
		appender.appendTrackRemoval(TrackType.VIDEO, "-d", "-D");
		commandLine.addArgument(definition.getInput().getAbsolutePath(), false);

		if ( definition.isClustersInMetaSeek() ) {
			commandLine.addArgument("--clusters-in-meta-seek");
		}
		final List<TrackDefinition> tracks = definition.getAddedTracks();
		for (TrackDefinition track : tracks) {
			track.accept(new MkvMergeTrackBuilder(commandLine));
		}
		return commandLine;
	}

	//==================================================================================================================
	// Private classes
	//==================================================================================================================

	private class TrackRemovalAppender {
		private final CommandLine commandLine;
		private final Container container;
		private final MergeDefinition definition;

		private TrackRemovalAppender(CommandLine commandLine, Container container, MergeDefinition definition) {
			this.commandLine = commandLine;
			this.container = container;
			this.definition = definition;
		}

		public void appendTrackRemoval(TrackType trackType, String removeOption, String removeAllOption) {
			final List<Track> tracks = container.getTracks(TrackFilterFactory.byType(trackType));
			final List<TrackId> toKeep = MergeUtil.getTracksExcept(tracks, definition.getRemovedTracks());
			if ( toKeep.isEmpty() ) {
				commandLine.addArgument(removeAllOption);
			} else if ( toKeep.size() < tracks.size() ) {
				commandLine.addArgument(removeOption);
				commandLine.addArgument(MergeUtil.toString(toKeep));
			}
		}
	}
}
