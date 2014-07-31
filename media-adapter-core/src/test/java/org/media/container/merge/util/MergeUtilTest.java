package org.media.container.merge.util;

import org.junit.Test;
import org.media.container.info.Track;
import org.media.container.info.TrackId;
import org.media.container.merge.MergeFactory;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class MergeUtilTest {

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	@Test
	public void testGetTracksExceptOne() throws Exception {
		assertEquals(Arrays.asList(MergeFactory.trackId(2)), MergeUtil.getTracksExcept(Arrays.asList(track(1), track(2)), Arrays.asList(MergeFactory.trackId(1))));
	}

	@Test
	public void testGetTracksExceptAll() throws Exception {
		assertEquals(new ArrayList<TrackId>(), MergeUtil.getTracksExcept(Arrays.asList(track(1), track(2)), Arrays.asList(MergeFactory.trackId(1), MergeFactory.trackId(2))));
	}

	@Test
	public void testGetTracksExceptNone() throws Exception {
		assertEquals(Arrays.asList(MergeFactory.trackId(1), MergeFactory.trackId(2)), MergeUtil.getTracksExcept(Arrays.asList(track(1), track(2)), new ArrayList<TrackId>()));
	}

	@Test
	public void testGetTracksExceptUnknown() throws Exception {
		assertEquals(Arrays.asList(MergeFactory.trackId(1), MergeFactory.trackId(2)), MergeUtil.getTracksExcept(Arrays.asList(track(1), track(2)), Arrays.asList(MergeFactory.trackId(4))));
	}

	@Test
	public void shouldReturnMultipleTracksToString() throws Exception {
		assertEquals("1,3,4", MergeUtil.toString(Arrays.asList(MergeFactory.trackId(1), MergeFactory.trackId(3), MergeFactory.trackId(4))));
	}

	@Test
	public void shouldReturnNoTracksToString() throws Exception {
		assertEquals("", MergeUtil.toString(new ArrayList<TrackId>()));
	}

	@Test
	public void shouldReturnOneTrackToString() throws Exception {
		assertEquals("1", MergeUtil.toString(Arrays.asList(MergeFactory.trackId(1))));
	}

	//==================================================================================================================
	// Private methods
	//==================================================================================================================

	private static Track track(long id) {
		final Track track = Mockito.mock(Track.class);
		final TrackId trackId = MergeFactory.trackId(id);
		Mockito.when(track.getId()).thenReturn(trackId);
		return track;
	}
}