package org.media.container.info;

import org.media.container.info.impl.NoTrackFilter;
import org.media.container.info.impl.TrackTypeFilter;

public class TrackFilterFactory {

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	public static TrackFilter all() {
		return NoTrackFilter.instance;
	}

	public static TrackFilter byType(TrackType trackType) {
		return new TrackTypeFilter(trackType);
	}
}
