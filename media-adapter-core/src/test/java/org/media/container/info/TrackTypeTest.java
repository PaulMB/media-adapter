package org.media.container.info;

import org.junit.Test;
import org.media.container.exception.MergeDefinitionException;

import static org.junit.Assert.*;

public class TrackTypeTest {

	@Test(expected = MergeDefinitionException.class)
	public void shouldFailIfNull() throws Exception {
		TrackType.fromString(null);
	}

	@Test(expected = MergeDefinitionException.class)
	public void shouldFailIfUnknownType() throws Exception {
		TrackType.fromString("*unknown*");
	}

	@Test
	public void shouldCreateSubtitleFromString() throws Exception {
		assertEquals(TrackType.SUBTITLE, TrackType.fromString("SUBTITLE"));
	}

	@Test
	public void shouldCreateAudioFromString() throws Exception {
		assertEquals(TrackType.AUDIO, TrackType.fromString("AUDIO"));
	}

	@Test
	public void shouldCreateButtonFromString() throws Exception {
		assertEquals(TrackType.BUTTON, TrackType.fromString("BUTTON"));
	}

	@Test
	public void shouldCreateComplexFromString() throws Exception {
		assertEquals(TrackType.COMPLEX, TrackType.fromString("COMPLEX"));
	}

	@Test
	public void shouldCreateControlFromString() throws Exception {
		assertEquals(TrackType.CONTROL, TrackType.fromString("CONTROL"));
	}

	@Test
	public void shouldCreateLogoFromString() throws Exception {
		assertEquals(TrackType.LOGO, TrackType.fromString("LOGO"));
	}

	@Test
	public void shouldCreateVideoFromString() throws Exception {
		assertEquals(TrackType.VIDEO, TrackType.fromString("VIDEO"));
	}
}