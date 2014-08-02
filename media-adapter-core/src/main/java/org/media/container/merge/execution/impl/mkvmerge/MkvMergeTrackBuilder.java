package org.media.container.merge.execution.impl.mkvmerge;

import org.apache.commons.exec.CommandLine;
import org.media.container.merge.SubtitleDefinition;
import org.media.container.merge.TrackDefinition;
import org.media.container.merge.TrackDefinitionVisitor;
import org.media.container.merge.io.IOFactory;

import java.io.IOException;
import java.nio.charset.Charset;

public class MkvMergeTrackBuilder implements TrackDefinitionVisitor {

	//==================================================================================================================
	// Attributes
	//==================================================================================================================

	private final int trackId;
	private final CommandLine commandLine;

	//==================================================================================================================
	// Constructors
	//==================================================================================================================

	public MkvMergeTrackBuilder(CommandLine commandLine) {
		this.trackId = 0;
		this.commandLine = commandLine;
	}

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	@Override
	public void visit(TrackDefinition definition) {
		this.visitDefinition(definition);
	}

	@Override
	public void visit(SubtitleDefinition definition) {
		this.visitDefinition(definition);
		final Charset charset = this.getCharset(definition);
		if ( charset != null ) {
			commandLine.addArgument("--sub-charset ");
			commandLine.addArgument(trackId + ":" + charset.toString(), false);
		}
	}

	//==================================================================================================================
	// Private methods
	//==================================================================================================================

	private void visitDefinition(TrackDefinition definition) {
		final String language = definition.getLanguage();
		if ( language != null ) {
			commandLine.addArgument("--language");
			commandLine.addArgument(trackId + ":" + language, false);
		}
		final String name = definition.getName();
		if( name != null ) {
			commandLine.addArgument("--track-name");
			commandLine.addArgument(trackId + ":" + name, false);
		}
		commandLine.addArgument(definition.getFile().getAbsolutePath(), false);
	}

	private Charset getCharset(SubtitleDefinition definition) {
		final Charset charset = definition.getCharset();
		if ( charset != null ) {
			return charset;
		}
		try {
			return IOFactory.detectCharset(definition.getFile());
		} catch (IOException e) {
			return null;
		}
	}
}
