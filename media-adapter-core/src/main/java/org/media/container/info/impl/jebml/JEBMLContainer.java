package org.media.container.info.impl.jebml;

import org.ebml.matroska.MatroskaFile;
import org.ebml.matroska.MatroskaFileTrack;
import org.media.container.info.Container;
import org.media.container.info.Track;
import org.media.container.info.TrackFilter;

import java.util.ArrayList;
import java.util.List;

public class JEBMLContainer implements Container {

	//==================================================================================================================
	// Attributes
	//==================================================================================================================

	private final MatroskaFile matroskaFile;
	private final List<JEBMLTrack> tracks;

	//==================================================================================================================
	// Constructors
	//==================================================================================================================

	public JEBMLContainer(MatroskaFile matroskaFile) {
		this.matroskaFile = matroskaFile;
		this.tracks = new ArrayList<>();
		final MatroskaFileTrack[] trackList = matroskaFile.getTrackList();
		for (MatroskaFileTrack fileTrack : trackList) {
			this.tracks.add(new JEBMLTrack(fileTrack));
		}
	}

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	@Override
	public String getTitle() {
		return this.matroskaFile.getSegmentTitle();
	}

	@Override
	public double getDuration() {
		return this.matroskaFile.getDuration();
	}

	@Override
	public List<Track> getTracks(TrackFilter filter) {
		final List<Track> filteredTracks = new ArrayList<>();
		for (Track track : this.tracks) {
			if ( filter.accept(track) ) {
				filteredTracks.add(track);
			}
		}
		return filteredTracks;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("Segment[title=").append(this.getTitle()).append(", duration=").append(this.getDuration()).append("]\n");
		for (Track track : tracks) {
			builder.append("Track[").append(track).append("]\n");
		}
		return builder.toString();
	}
}
