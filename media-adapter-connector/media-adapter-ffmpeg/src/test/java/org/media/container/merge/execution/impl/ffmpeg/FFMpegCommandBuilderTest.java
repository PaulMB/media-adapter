package org.media.container.merge.execution.impl.ffmpeg;

import org.apache.commons.exec.CommandLine;
import org.junit.Test;
import org.media.container.exception.MediaReadException;
import org.media.container.info.Container;
import org.media.container.info.ContainerFactory;
import org.media.container.info.Track;
import org.media.container.info.TrackId;
import org.media.container.info.TrackType;
import org.media.container.info.impl.SampleContainer;
import org.media.container.merge.MergeDefinition;
import org.media.container.merge.MergeFactory;
import org.media.container.merge.io.CommandConfiguration;
import org.mockito.Mockito;

import java.io.File;
import java.net.URI;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class FFMpegCommandBuilderTest {

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	@Test
	public void shouldReturnBasicCommandLine() throws Exception {
		assertCommandEquals("ffmpeg -y -i /a/b/c -map 0 -c copy /d/e", definition());
	}

	@Test
	public void shouldReturnCommandLineWithOneTrack() throws Exception {
		final MergeDefinition definition = definition().addTrack(MergeFactory.track(new File("/f/g")));
		assertCommandEquals("ffmpeg -y -i /a/b/c -i /f/g -map 0 -map 1 -c copy /d/e", definition);
	}

	@Test
	public void shouldReturnCommandLineWithOneTrackWithLanguage() throws Exception {
		final MergeDefinition definition = definition().addTrack(MergeFactory.track(new File("/f/g")).setLanguage("fre"));
		assertCommandEquals("ffmpeg -y -i /a/b/c -i /f/g -metadata:s:5 language=fre -map 0 -map 1 -c copy /d/e", definition);
	}

	@Test
	public void shouldReturnCommandLineWithOneTrackWithLanguageAndName() throws Exception {
		final MergeDefinition definition = definition().addTrack(MergeFactory.track(new File("/f/g")).setLanguage("fre").setName("French"));
		assertCommandEquals("ffmpeg -y -i /a/b/c -i /f/g -metadata:s:5 language=fre -metadata:s:5 title=French -map 0 -map 1 -c copy /d/e", definition);
	}

	@Test
	public void shouldReturnCommandLineWithTwoTracks() throws Exception {
		final MergeDefinition definition = definition().addTrack(MergeFactory.subtitle(new File("/f/g"))).addTrack(MergeFactory.track(new File("/h/i")));
		assertCommandEquals("ffmpeg -y -i /a/b/c -i /f/g -i /h/i -map 0 -map 1 -map 2 -c copy /d/e", definition);
	}

	@Test
	public void shouldReturnCommandLineWithTwoTracksWithOptions() throws Exception {
		final MergeDefinition definition = definition().
				addTrack(MergeFactory.track(new File("/f/g")).setLanguage("en")).
				addTrack(MergeFactory.track(new File("/h/i")).setLanguage("ger").setName("German"));
		assertCommandEquals("ffmpeg -y -i /a/b/c -i /f/g -metadata:s:5 language=en -i /h/i -metadata:s:6 language=ger -metadata:s:6 title=German -map 0 -map 1 -map 2 -c copy /d/e", definition);
	}

	@Test
	public void shouldRemoveAllSubs() throws Exception {
		final MergeDefinition definition = definition().removeTrack(MergeFactory.trackId(4)).removeTrack(MergeFactory.trackId(5)).removeTrack(MergeFactory.trackId(3));
		assertCommandEquals("ffmpeg -y -i /a/b/c -map 0 -map -0:3 -map -0:4 -map -0:2 -c copy /d/e", definition);
	}

	@Test
	public void shouldRemoveTwoSubs() throws Exception {
		final MergeDefinition definition = definition().removeTrack(MergeFactory.trackId(4)).removeTrack(MergeFactory.trackId(3));
		assertCommandEquals("ffmpeg -y -i /a/b/c -map 0 -map -0:3 -map -0:2 -c copy /d/e", definition);
	}

	@Test
	public void shouldRemoveOneSub() throws Exception {
		final MergeDefinition definition = definition().removeTrack(MergeFactory.trackId(3));
		assertCommandEquals("ffmpeg -y -i /a/b/c -map 0 -map -0:2 -c copy /d/e", definition);
	}

	@Test
	public void shouldRemoveAudio() throws Exception {
		final MergeDefinition definition = definition().removeTrack(MergeFactory.trackId(2));
		assertCommandEquals("ffmpeg -y -i /a/b/c -map 0 -map -0:1 -c copy /d/e", definition);
	}

	@Test
	public void shouldRemoveVideo() throws Exception {
		final MergeDefinition definition = definition().removeTrack(MergeFactory.trackId(1));
		assertCommandEquals("ffmpeg -y -i /a/b/c -map 0 -map -0:0 -c copy /d/e", definition);
	}

	@Test
	public void shouldAddAndRemoveTracks() throws Exception {
		final MergeDefinition definition = definition().addTrack(MergeFactory.subtitle(new File("/f/g"))).removeTrack(MergeFactory.trackId(3));
		assertCommandEquals("ffmpeg -y -i /a/b/c -i /f/g -map 0 -map -0:2 -map 1 -c copy /d/e", definition);
	}

	@Test
	public void shouldAddAndRemoveTracksWithMetadata() throws Exception {
		final MergeDefinition definition = definition().addTrack(MergeFactory.subtitle(new File("/f/g")).setLanguage("en")).removeTrack(MergeFactory.trackId(3));
		assertCommandEquals("ffmpeg -y -i /a/b/c -i /f/g -metadata:s:4 language=en -map 0 -map -0:2 -map 1 -c copy /d/e", definition);
	}

	//==================================================================================================================
	// Private methods
	//==================================================================================================================

	private static void assertCommandEquals(String expected, MergeDefinition definition) throws Exception {
		final CommandConfiguration configuration = Mockito.mock(CommandConfiguration.class);
		Mockito.when(configuration.getBinary()).thenReturn(Paths.get("ffmpeg"));
		assertEquals(CommandLine.parse(expected).toString(), new FFMpegCommandBuilder(configuration, factory()).getCommandLine(definition).toString());
	}

	private static MergeDefinition definition() {
		return MergeFactory.merge(new File("/a/b/c"), new File("/d/e"));
	}

	private static ContainerFactory factory() throws MediaReadException {
		final List<Track> tracks = new ArrayList<>();
		tracks.add(track(1, null, "V_MPEG4/ISO/AVC", "jpn", TrackType.VIDEO));
		tracks.add(track(2, "2ch Vorbis", "A_VORBIS", "jpn", TrackType.AUDIO));
		tracks.add(track(3, "Styled ASS", "S_TEXT/ASS", null, TrackType.SUBTITLE));
		tracks.add(track(4, "Srt", "S_TEXT/UTF8", null, TrackType.SUBTITLE));
		tracks.add(track(5, "2nd Srt", "S_TEXT/UTF8", null, TrackType.SUBTITLE));
		final Container container = container("", 0.0, tracks);
		return new ContainerFactory() {
			@Override
			public Container create(URI containerURI) throws MediaReadException {
				return container;
			}
		};
	}

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
}