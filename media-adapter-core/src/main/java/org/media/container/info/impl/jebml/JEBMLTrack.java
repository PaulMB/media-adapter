package org.media.container.info.impl.jebml;

import org.ebml.matroska.MatroskaFileTrack;
import org.media.container.info.Track;
import org.media.container.info.TrackId;
import org.media.container.info.TrackType;
import org.media.container.merge.MergeFactory;

public class JEBMLTrack implements Track {

	//==================================================================================================================
	// Attributes
	//==================================================================================================================

	private final MatroskaFileTrack track;
	private final TrackType type;

	//==================================================================================================================
	// Constructors
	//==================================================================================================================

	public JEBMLTrack(MatroskaFileTrack fileTrack) {
		track = fileTrack;
		type = getTrackType(fileTrack.TrackType);
	}

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	@Override
	public TrackId getId() {
		return MergeFactory.trackId(track.TrackNo);
	}

	@Override
	public String getName() {
		return track.Name;
	}

	@Override
	public TrackType getType() {
		return type;
	}

	@Override
	public String getCodecId() {
		return track.CodecID;
	}

	@Override
	public String getLanguage() {
		return track.Language;
	}

	//==================================================================================================================
	// Private methods
	//==================================================================================================================

	private static TrackType getTrackType(byte trackType) {
		switch (trackType) {
			case 0x01:
				return TrackType.VIDEO;
			case 0x02:
				return TrackType.AUDIO;
			case 0x03:
				return TrackType.COMPLEX;
			case 0x10:
				return TrackType.LOGO;
			case 0x11:
				return TrackType.SUBTITLE;
			case 0x12:
				return TrackType.BUTTON;
			case 0x20:
				return TrackType.CONTROL;
			default:
				throw new Error("unsupported track type " + trackType);
		}
	}
}
