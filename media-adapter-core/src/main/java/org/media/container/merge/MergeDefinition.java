package org.media.container.merge;

import org.media.container.info.TrackId;

import java.io.File;
import java.util.List;

public interface MergeDefinition {

	MergeDefinition addTrack(TrackDefinition track);

	MergeDefinition removeTrack(TrackId trackId);

	MergeDefinition setClustersInMetaSeek(boolean metaSeek);

	List<TrackDefinition> getAddedTracks();

	List<TrackId> getRemovedTracks();

	boolean isClustersInMetaSeek();

	File getInput();

	File getOutput();
}
