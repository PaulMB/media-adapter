package org.media.container.info;

public interface Track {

	TrackId getId();

	String getName();

	TrackType getType();

	String getCodecId();

	String getLanguage();
}
