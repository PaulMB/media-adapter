package org.media.web.merge;

import org.glassfish.jersey.media.sse.EventOutput;
import org.glassfish.jersey.media.sse.SseFeature;
import org.media.container.exception.MediaReadException;
import org.media.container.exception.MergeCancelException;
import org.media.container.exception.MergeDefinitionException;
import org.media.container.exception.MergeNotFoundException;
import org.media.container.exception.MergeStatusException;
import org.media.container.info.TrackType;
import org.media.container.merge.MergeDefinition;
import org.media.container.merge.MergeFactory;
import org.media.container.merge.TrackDefinition;
import org.media.container.merge.execution.Merge;
import org.media.web.info.TrackDescription;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnusedDeclaration")
@Path("merge")
public class MergeAdapter {

	//==================================================================================================================
	// Attributes
	//==================================================================================================================

	@Inject
	private MergeContext context;

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public MergeInstance create(MergeDescription description) throws MergeDefinitionException, MediaReadException {
		return new MergeInstance(this.getContext().getCollector().addMerge(this.createMergeDefinition(description)));
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<MergeInstance> list() {
		final List<MergeInstance> descriptions = new ArrayList<>();
		final Merge[] merges = this.getContext().getCollector().getMerges();
		for (Merge merge : merges) {
			descriptions.add(new MergeInstance(merge));
		}
		return descriptions;
	}

	@DELETE
	@Path("{id}")
	public void delete(@PathParam("id") String id) throws MergeNotFoundException, MergeStatusException, MergeCancelException {
		this.getContext().getCollector().removeMerge(MergeFactory.id(id));
	}

	@GET
	@Path("events")
	@Produces(SseFeature.SERVER_SENT_EVENTS)
	public EventOutput getEvents() {
		return this.getContext().addEventOutput();
	}

	//==================================================================================================================
	// Private methods
	//==================================================================================================================

	private MergeContext getContext() {
		return context;
	}

	private File getInputContainer(String input) throws MediaReadException {
		if ( input == null ) {
			throw new MediaReadException("no input specified");
		}
		final File inputContainer = new File(input);
		if ( ! inputContainer.exists() ) {
			throw new MediaReadException(input);
		}
		return inputContainer;
	}

	private MergeDefinition createMergeDefinition(MergeDescription description) throws MergeDefinitionException, MediaReadException {
		final File input = this.getInputContainer(description.getInput());
		final MergeDefinition merge = MergeFactory.merge(input, new File(description.getInput() + '.' + System.currentTimeMillis())).setClustersInMetaSeek(true);
		final List<TrackDescription> tracks = description.getTracksToAdd();
		if ( tracks == null ) {
			throw new MergeDefinitionException("no tracks to add specified");
		}
		for (TrackDescription track : tracks) {
			merge.addTrack(this.createTrackAdditionDefinition(track).setLanguage(track.getLanguage()).setName(track.getName()));
		}
		final List<TrackDescription> tracksToRemove = description.getTracksToRemove();
		if ( tracksToRemove == null ) {
			throw new MergeDefinitionException("no tracks to remove specified");
		}
		for (TrackDescription track : tracksToRemove) {
			merge.removeTrack(MergeFactory.trackId(track.getTrackId()));
		}
		return merge;
	}

	private TrackDefinition createTrackAdditionDefinition(TrackDescription track) throws MergeDefinitionException {
		final TrackType trackType = TrackType.fromString(track.getTrackType());
		final File trackPath = new File(track.getPath());
		switch (trackType) {
			case SUBTITLE:
				final String codecId = track.getCodecId();
				return MergeFactory.subtitle(trackPath).setCharset(codecId == null ? null : Charset.forName(codecId));
			default:
				return MergeFactory.track(trackPath);
		}
	}
}
