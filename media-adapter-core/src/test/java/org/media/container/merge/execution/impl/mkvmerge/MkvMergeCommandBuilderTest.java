package org.media.container.merge.execution.impl.mkvmerge;

import org.apache.commons.exec.CommandLine;
import org.junit.Test;
import org.media.container.exception.MediaReadException;
import org.media.container.info.Container;
import org.media.container.info.ContainerFactory;
import org.media.container.info.Track;
import org.media.container.info.TrackType;
import org.media.container.info.impl.ContainerUtil;
import org.media.container.merge.MergeDefinition;
import org.media.container.merge.MergeFactory;
import org.media.container.merge.io.CommandConfiguration;
import org.mockito.Mockito;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.media.container.info.impl.ContainerUtil.track;

public class MkvMergeCommandBuilderTest {

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	@Test
	public void shouldReturnBasicCommandLine() throws Exception {
		assertCommandEquals("mkvmerge -o /d/e /a/b/c", definition());
	}

	@Test
	public void shouldReturnCommandLineWithMetaSeek() throws Exception {
		final MergeDefinition definition = definition().setClustersInMetaSeek(true);
		assertCommandEquals("mkvmerge -o /d/e /a/b/c --clusters-in-meta-seek", definition);
	}

	@Test
	public void shouldReturnCommandLineWithOneTrack() throws Exception {
		final MergeDefinition definition = definition().addTrack(MergeFactory.track(new File("/f/g")));
		assertCommandEquals("mkvmerge -o /d/e /a/b/c /f/g", definition);
	}

	@Test
	public void shouldReturnCommandLineWithOneTrackWithLanguage() throws Exception {
		final MergeDefinition definition = definition().addTrack(MergeFactory.track(new File("/f/g")).setLanguage("fre"));
		assertCommandEquals("mkvmerge -o /d/e /a/b/c --language 0:fre /f/g", definition);
	}

	@Test
	public void shouldReturnCommandLineWithOneTrackWithLanguageAndName() throws Exception {
		final MergeDefinition definition = definition().addTrack(MergeFactory.track(new File("/f/g")).setLanguage("fre").setName("French"));
		assertCommandEquals("mkvmerge -o /d/e /a/b/c --language 0:fre --track-name 0:French /f/g", definition);
	}

	@Test
	public void shouldReturnCommandLineWithTwoTracks() throws Exception {
		final MergeDefinition definition = definition().addTrack(MergeFactory.track(new File("/f/g"))).addTrack(MergeFactory.track(new File("/h/i")));
		assertCommandEquals("mkvmerge -o /d/e /a/b/c /f/g /h/i", definition);
	}

	@Test
	public void shouldReturnCommandLineWithTwoTracksWithOptions() throws Exception {
		final MergeDefinition definition = definition().
				addTrack(MergeFactory.track(new File("/f/g")).setLanguage("en")).
				addTrack(MergeFactory.track(new File("/h/i")).setLanguage("ger").setName("German"));
		assertCommandEquals("mkvmerge -o /d/e /a/b/c --language 0:en /f/g --language 0:ger --track-name 0:German /h/i", definition);
	}

	@Test
	public void shouldRemoveAllSubs() throws Exception {
		final MergeDefinition definition = definition().removeTrack(MergeFactory.trackId(4)).removeTrack(MergeFactory.trackId(5)).removeTrack(MergeFactory.trackId(3));
		assertCommandEquals("mkvmerge -o /d/e -S /a/b/c", definition);
	}

	@Test
	public void shouldRemoveTwoSubs() throws Exception {
		final MergeDefinition definition = definition().removeTrack(MergeFactory.trackId(4)).removeTrack(MergeFactory.trackId(3));
		assertCommandEquals("mkvmerge -o /d/e -s 5 /a/b/c", definition);
	}

	@Test
	public void shouldRemoveOneSub() throws Exception {
		final MergeDefinition definition = definition().removeTrack(MergeFactory.trackId(3));
		assertCommandEquals("mkvmerge -o /d/e -s 4,5 /a/b/c", definition);
	}

	@Test
	public void shouldRemoveAudio() throws Exception {
		final MergeDefinition definition = definition().removeTrack(MergeFactory.trackId(2));
		assertCommandEquals("mkvmerge -o /d/e -A /a/b/c", definition);
	}

	@Test
	public void shouldRemoveVidio() throws Exception {
		final MergeDefinition definition = definition().removeTrack(MergeFactory.trackId(1));
		assertCommandEquals("mkvmerge -o /d/e -D /a/b/c", definition);
	}

	//==================================================================================================================
	// Private methods
	//==================================================================================================================

	private static void assertCommandEquals(String expected, MergeDefinition definition) throws Exception {
		final CommandConfiguration configuration = Mockito.mock(CommandConfiguration.class);
		final Path path = Paths.get("mkvmerge");
		Mockito.when(configuration.getBinary()).thenReturn(path);
		assertEquals(CommandLine.parse(expected).toString(), new MkvMergeCommandBuilder(configuration, factory()).getCommandLine(definition).toString());
	}

	private static ContainerFactory factory() throws MediaReadException {
		final List<Track> tracks = new ArrayList<>();
		tracks.add(track(1, null, "V_MPEG4/ISO/AVC", "jpn", TrackType.VIDEO));
		tracks.add(track(2, "2ch Vorbis", "A_VORBIS", "jpn", TrackType.AUDIO));
		tracks.add(track(3, "Styled ASS", "S_TEXT/ASS", null, TrackType.SUBTITLE));
		tracks.add(track(4, "Srt", "S_TEXT/UTF8", null, TrackType.SUBTITLE));
		tracks.add(track(5, "2nd Srt", "S_TEXT/UTF8", null, TrackType.SUBTITLE));
		final Container container = ContainerUtil.container("", 0.0, tracks);
		return new ContainerFactory() {
			@Override
			public Container create(URI containerURI) throws MediaReadException {
				return container;
			}
		};
	}

	private static MergeDefinition definition() {
		return MergeFactory.merge(new File("/a/b/c"), new File("/d/e"));
	}
}