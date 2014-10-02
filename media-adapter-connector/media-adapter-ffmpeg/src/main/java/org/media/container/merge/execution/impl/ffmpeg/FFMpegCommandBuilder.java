package org.media.container.merge.execution.impl.ffmpeg;

import org.apache.commons.exec.CommandLine;
import org.media.container.exception.MediaReadException;
import org.media.container.info.ContainerFactory;
import org.media.container.info.Track;
import org.media.container.info.TrackFilterFactory;
import org.media.container.info.TrackId;
import org.media.container.merge.MergeDefinition;
import org.media.container.merge.TrackDefinition;
import org.media.container.merge.execution.impl.command.CommandLineBuilder;
import org.media.container.merge.io.CommandConfiguration;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class FFMpegCommandBuilder implements CommandLineBuilder {

	//==================================================================================================================
	// Attributes
	//==================================================================================================================

	private final ContainerFactory containerFactory;
	private final Path binary;

	//==================================================================================================================
	// Constructors
	//==================================================================================================================

	public FFMpegCommandBuilder(CommandConfiguration configuration, ContainerFactory factory) {
		binary = configuration.getBinary();
		containerFactory = factory;
	}

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	@Override
	public CommandLine getCommandLine(MergeDefinition definition) throws IOException {
		try {
			return this.createCommandLine(definition);
		} catch (MediaReadException e) {
			throw new IOException(e.getMessage(), e);
		}
	}

	//==================================================================================================================
	// Private methods
	//==================================================================================================================

	private CommandLine createCommandLine(MergeDefinition definition) throws MediaReadException {
		final CommandLine commandLine = new CommandLine(binary.toString());
		final List<Track> inputTracks = containerFactory.create(definition.getInput().toURI()).getTracks(TrackFilterFactory.all());
		commandLine.addArgument("-y").addArgument("-i").addArgument(definition.getInput().getAbsolutePath(), false);

		final List<TrackId> removedTracks = definition.getRemovedTracks();
		final List<TrackDefinition> addedTracks = definition.getAddedTracks();

		for (int i = 0; i < addedTracks.size(); i++) {
			this.appendTrackAddition(inputTracks.size() - removedTracks.size() + i, addedTracks.get(i), commandLine);
		}

		commandLine.addArgument("-map").addArgument("0");
		for (TrackId trackId : removedTracks) {
			commandLine.addArgument("-map").addArgument("-0:" + (Integer.parseInt(trackId.toExternal()) - 1));
		}

		for (int i = 0; i < addedTracks.size(); i++) {
			commandLine.addArgument("-map").addArgument(String.valueOf(i + 1));
		}

		commandLine.addArgument("-c").addArgument("copy").addArgument(definition.getOutput().getAbsolutePath(), false);
		return commandLine;
	}

	private void appendTrackAddition(int outputTrackId, TrackDefinition definition, CommandLine commandLine) {
		commandLine.addArgument("-i");
		commandLine.addArgument(definition.getFile().getAbsolutePath(), false);
		final String language = definition.getLanguage();
		if ( language != null ) {
			commandLine.addArgument("-metadata:s:" + outputTrackId).addArgument("language=" + language, false);
		}
		final String name = definition.getName();
		if( name != null ) {
			commandLine.addArgument("-metadata:s:" + outputTrackId).addArgument("title=" + name, false);
		}
	}
}
