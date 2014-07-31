package org.media.container.merge;

import org.media.container.merge.execution.MergeId;
import org.media.container.merge.execution.impl.MergeUniqueId;
import org.media.container.merge.impl.MergeDefinitionImpl;
import org.media.container.merge.impl.SubtitleDefinitionImpl;
import org.media.container.merge.impl.TrackDefinitionImpl;

import java.io.File;

public class MergeFactory {

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	public static MergeDefinition merge(File input, File output) {
		return new MergeDefinitionImpl(input, output);
	}

	public static TrackDefinition track(File track) {
		return new TrackDefinitionImpl(track);
	}

	public static SubtitleDefinition subtitle(File track) {
		return new SubtitleDefinitionImpl(track);
	}

	public static MergeId id() {
		return new MergeUniqueId();
	}

	public static MergeId id(String name) {
		return new MergeUniqueId(name);
	}

}
