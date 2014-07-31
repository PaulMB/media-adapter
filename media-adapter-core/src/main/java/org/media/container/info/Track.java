package org.media.container.info;

public interface Track {

	long getNumber();

	String getName();

	TrackType getType();

	String getCodecId();

	String getLanguage();
}
