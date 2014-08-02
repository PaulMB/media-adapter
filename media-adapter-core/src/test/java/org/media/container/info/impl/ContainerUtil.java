package org.media.container.info.impl;

import org.media.container.info.Container;
import org.media.container.info.Track;
import org.media.container.info.TrackId;
import org.media.container.info.TrackType;
import org.media.container.merge.MergeFactory;
import org.mockito.Mockito;

import java.util.List;

import static junit.framework.Assert.assertEquals;

public class ContainerUtil {

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	public static Container container(String title, double duration, List<Track> tracks) {
		return new SampleContainer(title, duration, tracks);
	}

	public static Track track(long number, String name, String codecId, String language, TrackType trackType) {
		final Track track = Mockito.mock(Track.class);
		final TrackId trackId = MergeFactory.trackId(number);
		Mockito.when(track.getId()).thenReturn(trackId);
		Mockito.when(track.getName()).thenReturn(name);
		Mockito.when(track.getCodecId()).thenReturn(codecId);
		Mockito.when(track.getLanguage()).thenReturn(language);
		Mockito.when(track.getType()).thenReturn(trackType);
		return track;
	}

	public static void assertTrackEquals(List<Track> expectedList, List<Track> returnedList) {
		assertEquals(expectedList.size(), returnedList.size());
		for (int i = 0; i < expectedList.size(); i++) {
			final Track expected = expectedList.get(i);
			final Track returned = returnedList.get(i);
			assertEquals(expected.getId(), returned.getId());
			assertEquals(expected.getName(), returned.getName());
			assertEquals(expected.getCodecId(), returned.getCodecId());
			assertEquals(expected.getLanguage(), returned.getLanguage());
			assertEquals(expected.getType(), returned.getType());
		}
	}
}
