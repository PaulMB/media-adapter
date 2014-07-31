package org.media.container.merge.impl;

import org.media.container.merge.TrackDefinition;
import org.media.container.merge.TrackDefinitionVisitor;

import java.io.File;

public class TrackDefinitionImpl implements TrackDefinition {

	//==================================================================================================================
	// Attributes
	//==================================================================================================================

	private File file;
	private String language;
	private String name;

	//==================================================================================================================
	// Constructors
	//==================================================================================================================

	public TrackDefinitionImpl(File file) {
		this.file = file;
	}

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	@Override
	public TrackDefinition setLanguage(String language) {
		this.language = language;
		return this;
	}

	@Override
	public TrackDefinition setName(String name) {
		this.name = name;
		return this;
	}

	@Override
	public File getFile() {
		return file;
	}

	@Override
	public String getLanguage() {
		return language;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void accept(TrackDefinitionVisitor visitor) {
		visitor.visit(this);
	}
}
