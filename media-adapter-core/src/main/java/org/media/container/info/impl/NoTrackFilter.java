package org.media.container.info.impl;

import org.media.container.info.Track;
import org.media.container.info.TrackFilter;

public class NoTrackFilter implements TrackFilter {

	//==================================================================================================================
	// Constants
	//==================================================================================================================

	public static TrackFilter instance = new NoTrackFilter();

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	@Override
	public boolean accept(Track track) {
		return true;
	}
}
