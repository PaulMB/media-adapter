package org.media.container.merge.impl;

import org.media.container.info.TrackId;
import org.media.container.merge.MergeDefinition;
import org.media.container.merge.TrackDefinition;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MergeDefinitionImpl implements MergeDefinition {

	//==================================================================================================================
	// Attributes
	//==================================================================================================================

	private final List<TrackDefinition> addedTracks;
	private final List<TrackId> removedTracks;
	private final File input;
	private final File output;
	private boolean clustersInMetaSeek;

	//==================================================================================================================
	// Constructors
	//==================================================================================================================

	public MergeDefinitionImpl(File input, File output) {
		this.input = input;
		this.output = output;
		this.addedTracks = new ArrayList<>();
		this.removedTracks = new ArrayList<>();
		this.clustersInMetaSeek = false;
	}

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	@Override
	public MergeDefinition addTrack(TrackDefinition track) {
		addedTracks.add(track);
		return this;
	}

	@Override
	public MergeDefinition removeTrack(TrackId track) {
		removedTracks.add(track);
		return this;
	}

	@Override
	public MergeDefinition setClustersInMetaSeek(boolean metaSeek) {
		clustersInMetaSeek = metaSeek;
		return this;
	}

	@Override
	public List<TrackDefinition> getAddedTracks() {
		return addedTracks;
	}

	@Override
	public List<TrackId> getRemovedTracks() {
		return removedTracks;
	}

	@Override
	public boolean isClustersInMetaSeek() {
		return clustersInMetaSeek;
	}

	@Override
	public File getInput() {
		return input;
	}

	@Override
	public File getOutput() {
		return output;
	}
}
