package org.media.container.info.impl;

import org.media.container.info.Track;
import org.media.container.info.TrackFilter;
import org.media.container.info.TrackType;

public class TrackTypeFilter implements TrackFilter {

	//==================================================================================================================
	// Attributes
	//==================================================================================================================

	private final TrackType trackType;

	//==================================================================================================================
	// Constructors
	//==================================================================================================================

	public TrackTypeFilter(TrackType trackType) {
		this.trackType = trackType;
	}

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	@Override
	public boolean accept(Track track) {
		return track.getType() == trackType;
	}
}
