package org.media.web.info;

import junit.framework.Assert;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;
import org.media.container.info.ContainerFactory;
import org.media.container.info.Track;
import org.media.container.info.TrackId;
import org.media.container.info.TrackType;
import org.media.container.info.impl.jebml.JEBMLContainerFactory;
import org.media.container.merge.MergeFactory;
import org.media.container.merge.execution.MergeExecutorFactory;
import org.media.web.authentication.Authenticator;
import org.media.web.authentication.NoAuthentication;
import org.media.web.merge.MergeAdapter;
import org.media.web.merge.MergeContext;
import org.mockito.Mockito;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import java.util.Arrays;

import static junit.framework.Assert.assertEquals;

public class ContainerInfoTest extends JerseyTest {

	//==================================================================================================================
	// Constants
	//==================================================================================================================

	private static final TrackDescription track1 = new TrackDescription(track(1, "The Melancholy of Haruhi Suzumiya: Special Ending", "V_MPEG4/ISO/AVC", "jpn", TrackType.VIDEO));
	private static final TrackDescription track2 = new TrackDescription(track(2, "2ch Vorbis", "A_VORBIS", "jpn", TrackType.AUDIO));
	private static final TrackDescription track3 = new TrackDescription(track(3, "Styled ASS", "S_TEXT/ASS", null, TrackType.SUBTITLE));
	private static final TrackDescription track4 = new TrackDescription(track(4, "Styled ASS (Simple)", "S_TEXT/ASS", null, TrackType.SUBTITLE));
	private static final TrackDescription track5 = new TrackDescription(track(5, "Plain SRT", "S_TEXT/UTF8", null, TrackType.SUBTITLE));

	//==================================================================================================================
	// Public methods
	//==================================================================================================================
	@Override
	protected Application configure() {
		final MergeExecutorFactory factory = Mockito.mock(MergeExecutorFactory.class);
		final ResourceConfig application = new ResourceConfig();
		application.register(MultiPartFeature.class);
		application.register(MergeAdapter.class);
		application.packages("org.media.web");
		application.register(new AbstractBinder() {
			@Override
			protected void configure() {
				bind(new MergeContext(factory)).to(MergeContext.class);
				bind(new NoAuthentication()).to(Authenticator.class);
				bind(new JEBMLContainerFactory()).to(ContainerFactory.class);
			}
		});
		return application;
	}

	@Test
	public void shouldReturnContainerDescription() {
		final String file = ContainerInfoTest.class.getResource("/sample.mkv").getFile();
		final ContainerDescription description = target("media/info/" + file).request().get(ContainerDescription.class);
		Assert.assertEquals(5784.0, description.getDuration());
		assertEquals("The Melancholy of Haruhi Suzumiya: Special Ending", description.getTitle());
		assertEquals(Arrays.asList(track1, track2, track3, track4, track5), description.getTracks());
	}

	@Test
	public void shouldFailIfFileNotFound() {
		Assert.assertEquals(Response.Status.NOT_FOUND.getStatusCode(), target("media/info/notfound").request().get().getStatus());
	}

	@Test
	public void shouldFailIfNotMkv() {
		final String file = ContainerInfoTest.class.getResource("/invalid.file.txt").getFile();
		Assert.assertEquals(Response.Status.NOT_FOUND.getStatusCode(), target("media/info/" + file).request().get().getStatus());
	}

	@Test
	public void shouldFailIfCorruptedMkv() {
		final String file = ContainerInfoTest.class.getResource("/corrupted.mkv").getFile();
		Assert.assertEquals(Response.Status.NOT_FOUND.getStatusCode(), target("media/info/" + file).request().get().getStatus());
	}

	@Test
	public void shouldDescriptionBeEqual() {
		Assert.assertEquals(track1, track1);
		Assert.assertEquals(track1.hashCode(), track1.hashCode());
	}

	//==================================================================================================================
	// Private methods
	//==================================================================================================================

	private static Track track(long number, String name, String codecId, String language, TrackType trackType) {
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