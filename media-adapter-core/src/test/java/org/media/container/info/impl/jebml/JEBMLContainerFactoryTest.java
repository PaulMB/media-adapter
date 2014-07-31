package org.media.container.info.impl.jebml;

import org.junit.Test;
import org.media.container.exception.MediaReadException;
import org.media.container.info.Container;
import org.media.container.info.ContainerFactory;
import org.media.container.info.Track;
import org.media.container.info.TrackType;
import org.media.container.info.impl.TrackImpl;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import static junit.framework.Assert.assertEquals;

public class JEBMLContainerFactoryTest {

	//==================================================================================================================
	// Constants
	//==================================================================================================================

	private static final Track track1 = new TrackImpl(1, "The Melancholy of Haruhi Suzumiya: Special Ending", "V_MPEG4/ISO/AVC", "jpn", TrackType.VIDEO);
	private static final Track track2 = new TrackImpl(2, "2ch Vorbis", "A_VORBIS", "jpn", TrackType.AUDIO);
	private static final Track track3 = new TrackImpl(3, "Styled ASS", "S_TEXT/ASS", null, TrackType.SUBTITLE);
	private static final Track track4 = new TrackImpl(4, "Styled ASS (Simple)", "S_TEXT/ASS", null, TrackType.SUBTITLE);
	private static final Track track5 = new TrackImpl(5, "Plain SRT", "S_TEXT/UTF8", null, TrackType.SUBTITLE);

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	@Test
	public void testCreate() throws Exception {
		final Container container = factory().create(this.getResource("/org/media/container/info/impl/jebml/sample.mkv"));
		assertEquals(5784.0, container.getDuration());
		assertEquals("The Melancholy of Haruhi Suzumiya: Special Ending", container.getTitle());
		assertEquals(Arrays.asList(track1, track2, track3, track4, track5), container.getTracks());
	}

	@Test(expected = MediaReadException.class)
	public void shouldFailWhenFileNotFound() throws Exception {
		factory().create(new File("/not/found").toURI());
	}

	@Test(expected = MediaReadException.class)
	public void shouldFailWhenNotAnMkvFile() throws Exception {
		factory().create(this.getResource("/org/media/container/info/impl/jebml/invalid.file.txt"));
	}

	@Test(expected = MediaReadException.class)
	public void shouldFailWhenCorruptedMkvFile() throws Exception {
		factory().create(this.getResource("/org/media/container/info/impl/jebml/corrupted.mkv"));
	}

	//==================================================================================================================
	// Private methods
	//==================================================================================================================

	private static ContainerFactory factory() {
		return new JEBMLContainerFactory();
	}

	private URI getResource(String name) throws URISyntaxException {
		return JEBMLContainerFactoryTest.class.getResource(name).toURI();
	}
}