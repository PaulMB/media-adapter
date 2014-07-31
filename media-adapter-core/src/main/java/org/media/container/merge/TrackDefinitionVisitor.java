package org.media.container.merge;

public interface TrackDefinitionVisitor {

	void visit(TrackDefinition definition);

	void visit(SubtitleDefinition definition);
}
