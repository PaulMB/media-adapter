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

	private CommandLine appendContainerOptions(CommandLine commandLine, MergeDefinition definition) throws IOException, MediaReadException {
		final Container container = containerFactory.create(definition.getInput().toURI());
		commandLine.addArgument("-o");
		commandLine.addArgument(definition.getOutput().getAbsolutePath(), false);
		this.appendTrackRemoval(commandLine, container, definition, TrackType.SUBTITLE, "-s", "-S");//TODO
		this.appendTrackRemoval(commandLine, container, definition, TrackType.AUDIO, "-a", "-A");
		this.appendTrackRemoval(commandLine, container, definition, TrackType.VIDEO, "-d", "-D");
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

	private void appendTrackRemoval(CommandLine commandLine, Container container, MergeDefinition definition, TrackType trackType, String removeOption, String removeAllOption) {
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
