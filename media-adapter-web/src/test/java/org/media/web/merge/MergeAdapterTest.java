package org.media.web.merge;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.sse.EventSource;
import org.glassfish.jersey.media.sse.InboundEvent;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.media.container.info.TrackType;
import org.media.container.merge.MergeDefinition;
import org.media.container.merge.execution.MergeExecutor;
import org.media.container.merge.execution.MergeExecutorFactory;
import org.media.web.authentication.Authenticator;
import org.media.web.authentication.NoAuthentication;
import org.media.web.info.TrackDescription;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static com.jayway.awaitility.Awaitility.await;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class MergeAdapterTest extends JerseyTest {

	//==================================================================================================================
	// Attributes
	//==================================================================================================================

	private Path container;
	private Path track;
	private MergeExecutor executor;

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		container = Files.createTempFile("container with space", ".mkv");
		track = Files.createTempFile("subtitle with space", ".srt");
		Files.copy(Paths.get(MergeAdapterTest.class.getResource("/sample.mkv").toURI()), container, StandardCopyOption.REPLACE_EXISTING);
		Files.copy(Paths.get(MergeAdapterTest.class.getResource("/sub.srt").toURI()), track, StandardCopyOption.REPLACE_EXISTING);
	}

	@Override
	@After
	public void tearDown() throws Exception {
		super.tearDown();
		Files.deleteIfExists(container);
		Files.deleteIfExists(track);
	}

	@Override
	protected Application configure() {
		final ResourceConfig application = new ResourceConfig();
		final MergeExecutorFactory factory = new TestFactory();
		final MergeContext context = new MergeContext(factory);
		application.register(new AbstractBinder() {
			@Override
			protected void configure() {
				bind(context).to(MergeContext.class);
				bind(new NoAuthentication()).to(Authenticator.class);
			}
		});
		application.register(MultiPartFeature.class);
		application.register(MergeAdapter.class);
		application.packages("org.media.web");
		return application;
	}

	protected void configureClient(final ClientConfig config) {
    	config.register(MultiPartFeature.class);
 	}

	@Test
	public void shouldReturnEmptyTasks() {
		final List merges = target("merge").request().get(List.class);
		assertEquals(0, merges.size());
	}

	@Test
	public void shouldAddSubtitle() throws Exception {
		final List<InboundEvent> events = this.getIncomingEvents();
		final MergeInstance merge = this.submitMerge().readEntity(MergeInstance.class);
		final String id = merge.getId();
		final String input = merge.getInput();
		assertEquals(container.getFileName().toString(), merge.getInput());
		waitForEvents(events, 4);
		assertEventEquals(Arrays.asList(
				event("merge_create", merge(id, "PENDING", input, "ok")),
				event("merge_update", merge(id, "RUNNING", input, "ok")),
				event("merge_update", merge(id, "COMPLETED", input, "ok")),
				event("merge_delete", merge(id, "COMPLETED", input, "ok"))
		), events);
		assertEquals(0, target("merge").request().get(List.class).size());
		assertTrue(Files.exists(track));
	}

	@Test
	public void shouldAddAndRemoveSubtitle() throws Exception {
		final List<InboundEvent> events = this.getIncomingEvents();
		final MergeInstance merge = this.submitMergeWithAddAndRemove().readEntity(MergeInstance.class);
		final String id = merge.getId();
		final String input = merge.getInput();
		assertEquals(container.getFileName().toString(), merge.getInput());
		waitForEvents(events, 4);
		assertEventEquals(Arrays.asList(
				event("merge_create", merge(id, "PENDING", input, "ok")),
				event("merge_update", merge(id, "RUNNING", input, "ok")),
				event("merge_update", merge(id, "COMPLETED", input, "ok")),
				event("merge_delete", merge(id, "COMPLETED", input, "ok"))
		), events);
		assertEquals(0, target("merge").request().get(List.class).size());
		assertTrue(Files.exists(track));
	}

	@Test
	public void shouldFailIfMergeCanNotBeExecuted() throws Exception {
		this.setExecutor(this.createExecutorWithFailure());
		final List<InboundEvent> events = this.getIncomingEvents();
		final MergeInstance merge = this.submitMerge().readEntity(MergeInstance.class);
		final String id = merge.getId();
		final String input = merge.getInput();
		assertEquals(container.getFileName().toString(), input);
		waitForEvents(events, 3);
		assertEventEquals(Arrays.asList(
				event("merge_create", merge(id, "PENDING", input, "failed")),
				event("merge_update", merge(id, "RUNNING", input, "failed")),
				event("merge_update", merge(id, "ERROR", input, "failed"))
		), events);
		assertEquals(1, target("merge").request().get(List.class).size());
	}

	@Test
	public void shouldFailIfNoTracksToRemove() throws Exception {
		final MergeDescription description = new MergeDescription(container.toString());
		final TrackDescription trackDescription = new TrackDescription(track.toString());
		trackDescription.setTrackType(TrackType.SUBTITLE.name());
		description.getTracksToAdd().add(trackDescription);
		description.setTracksToRemove(null);
		final Response merge = target("merge").request().post(Entity.json(description));
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), merge.getStatus());
	}

	@Test
	public void shouldFailIfNoTracksToAdd() throws Exception {
		final MergeDescription description = new MergeDescription(container.toString());
		description.setTracksToAdd(null);
		final Response merge = target("merge").request().post(Entity.json(description));
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), merge.getStatus());
	}

	@Test
	public void shouldFailIfNoInput() throws Exception {
		final MergeDescription description = new MergeDescription(null);
		final Response merge = target("merge").request().post(Entity.json(description));
		assertEquals(Response.Status.NOT_FOUND.getStatusCode(), merge.getStatus());
	}

	@Test
	public void shouldFailIfUnknownTrackType() throws Exception {
		final MergeDescription description = new MergeDescription(container.toString());
		final TrackDescription trackDescription = new TrackDescription(track.toString());
		trackDescription.setTrackType("unknown");
		description.getTracksToAdd().add(trackDescription);
		final Response merge = target("merge").request().post(Entity.json(description));
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), merge.getStatus());
	}

	@Test
	public void shouldFailIfInputNotFound() throws Exception {
		final MergeDescription description = new MergeDescription("/not_found_here");
		final TrackDescription trackDescription = new TrackDescription(track.toString());
		trackDescription.setTrackType(TrackType.SUBTITLE.name());
		description.getTracksToAdd().add(trackDescription);
		assertEquals(Response.Status.NOT_FOUND.getStatusCode(), target("merge").request().post(Entity.json(description)).getStatus());
	}

	@Test
	public void shouldRemoveExistingMerge() throws Exception {
		this.setExecutor(this.createExecutorWithFailure());
		final List<InboundEvent> events = this.getIncomingEvents();
		final MergeInstance merge = this.submitMerge().readEntity(MergeInstance.class);
		waitForEvents(events, 3);
		assertEquals(Response.Status.NO_CONTENT.getStatusCode(), target("merge/" + merge.getId()).request().delete().getStatus());
		assertEquals(0, target("merge").request().get(List.class).size());
	}

	@Test
	public void shouldFailWhenRemovingUnknownMerge() throws Exception {
		final List<InboundEvent> events = this.getIncomingEvents();
		final MergeInstance merge = this.submitMerge().readEntity(MergeInstance.class);
		waitForEvents(events, 4);
		assertEquals(Response.Status.NOT_FOUND.getStatusCode(), target("merge/" + merge.getId()).request().delete().getStatus());
	}

	//==================================================================================================================
	// Private methods
	//==================================================================================================================

	private static void assertEventEquals(List<MergeEvent> expectedDescriptions, List<InboundEvent> receivedEvents) {
		assertEquals(expectedDescriptions.size(), receivedEvents.size());
		for (int i = 0; i < expectedDescriptions.size(); i++) {
			final MergeEvent event = expectedDescriptions.get(i);
			final MergeInstance expected = event.getDescription();
			final InboundEvent received = receivedEvents.get(i);
			assertEquals(event.getEventName(), received.getName());
			final JSONObject parse = (JSONObject) JSONValue.parse(received.readData());
			assertEquals(expected.getMessage(), parse.get("message"));
			assertEquals(expected.getId(), parse.get("id"));
			assertEquals(expected.getInput(), parse.get("input"));
			assertEquals(expected.getStatus(), parse.get("status"));
		}
	}

	private static MergeEvent event(String eventName, MergeInstance description) {
		return new MergeEvent(eventName, description);
	}

	private static MergeInstance merge(String id, String status, String input, String message) {
		final MergeInstance description = new MergeInstance();
		description.setId(id);
		description.setInput(input);
		description.setMessage(message);
		description.setStatus(status);
		return description;
	}

	private List<InboundEvent> getIncomingEvents() {
		final List<InboundEvent> events = new ArrayList<>();
		new EventSource(target("merge/events")) {
			@Override
			public void onEvent(InboundEvent inboundEvent) {
				events.add(inboundEvent);
			}
		};
		return events;
	}

	private static void waitForEvents(final List<InboundEvent> events, final int nbOfEvents) {
		await().atMost(2, TimeUnit.SECONDS).until(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return events.size() == nbOfEvents;
			}
		});
	}

	private MergeExecutor createExecutorWithFailure() throws Exception {
		final MergeExecutor mergeExecutor = mock(MergeExecutor.class);
		doThrow(new Exception()).when(mergeExecutor).execute();
		when(mergeExecutor.getMessage()).thenReturn("failed");
		return mergeExecutor;
	}

	private void setExecutor(MergeExecutor executor) {
		this.executor = executor;
	}

	private Response submitMerge() throws IOException {
		final MergeDescription description = new MergeDescription(container.toString());
		final TrackDescription trackDescription = new TrackDescription(track.toString());
		trackDescription.setTrackType(TrackType.SUBTITLE.name());
		description.getTracksToAdd().add(trackDescription);
		return target("merge").request(MediaType.APPLICATION_JSON_TYPE).post(Entity.json(description));
	}

	private Response submitMergeWithAddAndRemove() throws IOException {
		final MergeDescription description = new MergeDescription(container.toString());
		final TrackDescription trackDescription = new TrackDescription(track.toString());
		trackDescription.setTrackType(TrackType.SUBTITLE.name());
		description.getTracksToAdd().add(trackDescription);
		final TrackDescription trackToRemove = new TrackDescription();
		trackToRemove.setTrackId("1");
		description.getTracksToRemove().add(trackToRemove);
		return target("merge").request(MediaType.APPLICATION_JSON_TYPE).post(Entity.json(description));
	}

	private static class MergeEvent {

		private String eventName;
		private MergeInstance description;

		private MergeEvent(String eventName, MergeInstance description) {
			this.eventName = eventName;
			this.description = description;
		}

		public String getEventName() {
			return eventName;
		}

		public MergeInstance getDescription() {
			return description;
		}
	}

	private class TestFactory implements MergeExecutorFactory {
		@Override
		public MergeExecutor create(MergeDefinition definition) {
			if ( executor == null ) {
				return new TestExecutor(definition);
			} else {
				return executor;
			}
		}
	}

	private class TestExecutor implements MergeExecutor {

		private MergeDefinition definition;

		private TestExecutor(MergeDefinition definition) {
			this.definition = definition;
		}

		@Override
		public void execute() throws Exception {
			Files.copy(definition.getInput().toPath(), definition.getOutput().toPath(), StandardCopyOption.REPLACE_EXISTING);
		}

		@Override
		public void cancel() throws Exception {
		}

		@Override
		public void dispose() {
		}

		@Override
		public String getMessage() {
			return "ok";
		}
	}
}