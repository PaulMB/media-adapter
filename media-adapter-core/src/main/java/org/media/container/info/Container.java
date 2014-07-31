package org.media.container.info;

import java.util.List;

public interface Container {

	String getTitle();

	double getDuration();

	List<Track> getTracks();
}
