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
import org.media.container.merge.util.MergeUtil;

import java.io.IOException;
import java.util.List;

public class CommandBuilder {

	//==================================================================================================================
	// Attributes
	//==================================================================================================================

	private final CommandLine commandLine;
	private final MergeDefinition definition;
	private final Container container;

	//==================================================================================================================
	// Constructors
	//==================================================================================================================

	public CommandBuilder(MergeDefinition mergeDefinition, ContainerFactory containerFactory) throws IOException {
		this(mergeDefinition, containerFactory, "mkvmerge");
	}

	public CommandBuilder(MergeDefinition mergeDefinition, ContainerFactory factory, String executable) throws IOException {
		definition = mergeDefinition;
		commandLine = new CommandLine(executable);
		try {
			container = factory.create(mergeDefinition.getInput().toURI());
			this.createCommandLine();
		} catch (MediaReadException e) {
			throw new IOException(e.getMessage(), e);
		}
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

	private void createCommandLine() throws IOException, MediaReadException {
		commandLine.addArgument("-o");
		commandLine.addArgument(definition.getOutput().getAbsolutePath(), false);
		this.appendTrackRemoval(TrackType.SUBTITLE, "-s", "-S");
		this.appendTrackRemoval(TrackType.AUDIO, "-a", "-A");
		this.appendTrackRemoval(TrackType.VIDEO, "-d", "-D");
		commandLine.addArgument(definition.getInput().getAbsolutePath(), false);

		if ( definition.isClustersInMetaSeek() ) {
			commandLine.addArgument("--clusters-in-meta-seek");
		}
		final List<TrackDefinition> tracks = definition.getAddedTracks();
		for (TrackDefinition track : tracks) {
			track.accept(new CommandTrackBuilder(commandLine));
		}
	}

	private void appendTrackRemoval(TrackType trackType, String removeOption, String removeAllOption) {
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
