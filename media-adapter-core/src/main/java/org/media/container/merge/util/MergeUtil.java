package org.media.container.merge.util;

import org.media.container.info.Track;
import org.media.container.info.TrackId;

import java.util.ArrayList;
import java.util.List;

public class MergeUtil {

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	public static List<TrackId> getTracksExcept(List<Track> tracks, List<TrackId> except) {
		final List<TrackId> toKeep = new ArrayList<>();
		for (Track track : tracks) {
			final TrackId trackId = track.getId();
			if ( ! except.contains(trackId) ) {
				toKeep.add(trackId);
			}
		}
		return toKeep;
	}

	public static String toString(List<TrackId> tracks) {
		final StringBuilder builder = new StringBuilder();
		for (int i = 0; i < tracks.size(); i++) {
			if ( i > 0 ) {
				builder.append(",");
			}
			builder.append(tracks.get(i).toExternal());
		}
		return builder.toString();
	}
}
