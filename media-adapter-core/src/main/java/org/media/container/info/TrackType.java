package org.media.container.info;

import org.media.container.exception.MergeDefinitionException;

public enum TrackType {
	VIDEO, AUDIO, COMPLEX, LOGO, SUBTITLE, BUTTON, CONTROL;

	public static TrackType fromString(String trackType) throws MergeDefinitionException {
		if ( trackType == null ) {
			throw new MergeDefinitionException("no track type specified");
		}
		try {
			return TrackType.valueOf(trackType);
		} catch (IllegalArgumentException e) {
			throw new MergeDefinitionException(e);
		}
	}
}
