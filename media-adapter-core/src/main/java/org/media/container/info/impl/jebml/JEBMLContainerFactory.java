package org.media.container.info.impl.jebml;

import org.ebml.io.InputStreamDataSource;
import org.ebml.matroska.MatroskaFile;
import org.ebml.matroska.MatroskaFileTrack;
import org.media.container.exception.MediaReadException;
import org.media.container.info.Container;
import org.media.container.info.ContainerFactory;
import org.media.container.info.Track;
import org.media.container.info.TrackType;
import org.media.container.info.impl.ContainerImpl;
import org.media.container.info.impl.TrackImpl;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class JEBMLContainerFactory implements ContainerFactory {

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	@Override
	public Container create(URI containerURI) throws MediaReadException {
		try {
			final MatroskaFile matroskaFile = new MatroskaFile(new InputStreamDataSource(Files.newInputStream(Paths.get(containerURI))));
			matroskaFile.setScanFirstCluster(false);
			matroskaFile.readFile();
			final List<Track> tracks = this.getTracks(matroskaFile.getTrackList());
			return new ContainerImpl(matroskaFile.getSegmentTitle(), matroskaFile.getDuration(), tracks);
		} catch (Exception e) {
			throw new MediaReadException(e.getMessage(), e);
		}
	}

	//==================================================================================================================
	// Private methods
	//==================================================================================================================

	private List<Track> getTracks(MatroskaFileTrack[] fileTracks) {
		final List<Track> tracks = new ArrayList<Track>();
		for (MatroskaFileTrack fileTrack : fileTracks) {
			final TrackType type = getTrackType(fileTrack.TrackType);
			tracks.add(new TrackImpl(fileTrack.TrackNo, fileTrack.Name, fileTrack.CodecID, fileTrack.Language, type));
		}
		return tracks;
	}

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
