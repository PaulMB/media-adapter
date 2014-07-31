package org.media.container.merge.execution.impl.mkvmerge;

import org.apache.commons.exec.CommandLine;
import org.junit.Test;
import org.media.container.merge.MergeDefinition;
import org.media.container.merge.MergeFactory;

import java.io.File;

import static junit.framework.Assert.*;

public class CommandBuilderTest {

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

	//==================================================================================================================
	// Private methods
	//==================================================================================================================

	private static void assertCommandEquals(String expected, MergeDefinition definition) {
		assertEquals(CommandLine.parse(expected).toString(), new CommandBuilder(definition).getCommandLine().toString());
	}

	private static MergeDefinition definition() {
		return MergeFactory.merge(new File("/a/b/c"), new File("/d/e"));
	}
}