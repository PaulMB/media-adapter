package org.media.container.merge.impl;

import org.media.container.merge.SubtitleDefinition;
import org.media.container.merge.TrackDefinitionVisitor;

import java.io.File;
import java.nio.charset.Charset;

public class SubtitleDefinitionImpl extends TrackDefinitionImpl implements SubtitleDefinition {

	//==================================================================================================================
	// Attributes
	//==================================================================================================================

	private Charset charset;

	//==================================================================================================================
	// Constructors
	//==================================================================================================================

	public SubtitleDefinitionImpl(File file) {
		super(file);
	}

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	@Override
	public SubtitleDefinition setCharset(Charset charset) {
		this.charset = charset;
		return this;
	}

	@Override
	public Charset getCharset() {
		return charset;
	}

	@Override
	public void accept(TrackDefinitionVisitor visitor) {
		visitor.visit(this);
	}
}
