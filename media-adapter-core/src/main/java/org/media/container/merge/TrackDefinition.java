package org.media.container.merge;

import java.io.File;

public interface TrackDefinition {

	TrackDefinition setLanguage(String language);

	TrackDefinition setName(String name);

	File getFile();

	String getLanguage();

	String getName();

	void accept(TrackDefinitionVisitor visitor);
}
