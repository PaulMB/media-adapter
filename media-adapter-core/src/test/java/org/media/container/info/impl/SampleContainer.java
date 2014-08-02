package org.media.container.info.impl;

import org.media.container.info.Container;
import org.media.container.info.Track;
import org.media.container.info.TrackFilter;

import java.util.ArrayList;
import java.util.List;

public class SampleContainer implements Container {

	//==================================================================================================================
	// Attributes
	//==================================================================================================================

	private String title;
	private double duration;
	private final List<Track> tracks;

	//==================================================================================================================
	// Constructors
	//==================================================================================================================

	public SampleContainer(final String title, final double duration, final List<Track> tracks) {
		this.tracks = tracks;
		this.duration = duration;
		this.title = title;
	}

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	@Override
	public String getTitle() {
		return this.title;
	}

	@Override
	public double getDuration() {
		return this.duration;
	}

	@Override
	public List<Track> getTracks(TrackFilter filter) {
		final List<Track> filtered = new ArrayList<>();
		for (Track track : tracks) {
			if ( filter.accept(track) ) {
				filtered.add(track);
			}
		}
		return filtered;
	}
}
