package org.media.container.info.impl.jebml;

import org.ebml.matroska.MatroskaFileTrack;
import org.junit.Test;
import org.media.container.exception.MediaReadException;
import org.media.container.info.Container;
import org.media.container.info.ContainerFactory;
import org.media.container.info.Track;
import org.media.container.info.TrackFilterFactory;
import org.media.container.info.TrackType;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.media.container.info.impl.ContainerUtil.assertTrackEquals;
import static org.media.container.info.impl.ContainerUtil.track;

public class JEBMLContainerFactoryTest {

	//==================================================================================================================
	// Constants
	//==================================================================================================================

	private static final Track track1 = track(1, "The Melancholy of Haruhi Suzumiya: Special Ending", "V_MPEG4/ISO/AVC", "jpn", TrackType.VIDEO);
	private static final Track track2 = track(2, "2ch Vorbis", "A_VORBIS", "jpn", TrackType.AUDIO);
	private static final Track track3 = track(3, "Styled ASS", "S_TEXT/ASS", null, TrackType.SUBTITLE);
	private static final Track track4 = track(4, "Styled ASS (Simple)", "S_TEXT/ASS", null, TrackType.SUBTITLE);
	private static final Track track5 = track(5, "Plain SRT", "S_TEXT/UTF8", null, TrackType.SUBTITLE);

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	@Test
	public void shouldCreateContainer() throws Exception {
		final Container container = factory().create(this.getResource("/org/media/container/info/impl/jebml/sample.mkv"));
		assertNotNull(container.toString());
		assertEquals(5784.0, container.getDuration());
		assertEquals("The Melancholy of Haruhi Suzumiya: Special Ending", container.getTitle());
		assertTrackEquals(Arrays.asList(track1, track2, track3, track4, track5), container.getTracks(TrackFilterFactory.all()));
	}

	@Test
	public void shouldReturnSubtitles() throws Exception {
		final Container container = factory().create(this.getResource("/org/media/container/info/impl/jebml/sample.mkv"));
		assertTrackEquals(Arrays.asList(track3, track4, track5), container.getTracks(TrackFilterFactory.byType(TrackType.SUBTITLE)));
	}

	@Test
	public void shouldReturnAudio() throws Exception {
		final Container container = factory().create(this.getResource("/org/media/container/info/impl/jebml/sample.mkv"));
		assertTrackEquals(Arrays.asList(track2), container.getTracks(TrackFilterFactory.byType(TrackType.AUDIO)));
	}

	@Test
	public void shouldReturnVideo() throws Exception {
		final Container container = factory().create(this.getResource("/org/media/container/info/impl/jebml/sample.mkv"));
		assertTrackEquals(Arrays.asList(track1), container.getTracks(TrackFilterFactory.byType(TrackType.VIDEO)));
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

	@Test
	public void shouldConvertLogoTrack() throws Exception {
		assertEquals(TrackType.LOGO, new JEBMLTrack(createMkvTrack((byte) 0x10)).getType());
	}

	@Test
	public void shouldConvertComplexTrack() throws Exception {
		assertEquals(TrackType.COMPLEX, new JEBMLTrack(createMkvTrack((byte) 0x03)).getType());
	}

	@Test
	public void shouldConvertControlTrack() throws Exception {
		assertEquals(TrackType.CONTROL, new JEBMLTrack(createMkvTrack((byte) 0x20)).getType());
	}

	@Test
	public void shouldConvertButtonTrack() throws Exception {
		assertEquals(TrackType.BUTTON, new JEBMLTrack(createMkvTrack((byte) 0x12)).getType());
	}

	@Test(expected = Error.class)
	public void shouldNotConvertUnknownTrackType() throws Exception {
		assertEquals(TrackType.BUTTON, new JEBMLTrack(createMkvTrack((byte) 0x00)).getType());
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

	private static MatroskaFileTrack createMkvTrack(byte type) {
		final MatroskaFileTrack fileTrack = new MatroskaFileTrack();
		fileTrack.CodecID = "dummyCodec";
		fileTrack.Name = "dummyName";
		fileTrack.Language = "en";
		fileTrack.TrackNo = 1;
		fileTrack.TrackType = type;
		return fileTrack;
	}
}