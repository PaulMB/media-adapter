package org.media.container.merge;

import java.io.File;

public interface MergeDefinition {

	MergeDefinition addTrack(TrackDefinition track);

	MergeDefinition setClustersInMetaSeek(boolean metaSeek);

	TrackDefinition[] getTracks();

	boolean isClustersInMetaSeek();

	File getInput();

	File getOutput();
}
