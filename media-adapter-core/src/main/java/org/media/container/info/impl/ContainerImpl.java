package org.media.container.info.impl;

import org.media.container.info.Container;
import org.media.container.info.Track;

import java.util.List;

public class ContainerImpl implements Container {

	//==================================================================================================================
	// Attributes
	//==================================================================================================================

	private String title;
	private double duration;
	private final List<Track> tracks;

	//==================================================================================================================
	// Constructors
	//==================================================================================================================

	public ContainerImpl(final String title, final double duration, final List<Track> tracks) {
		this.tracks = tracks;
		this.duration = duration;
		this.title = title;
	}

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	@Override
	public String getTitle() {
		return this.title;
	}

	@Override
	public double getDuration() {
		return this.duration;
	}

	@Override
	public List<Track> getTracks() {
		return tracks;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("Segment[title=").append(title).append(", duration=").append(duration).append("]\n");
		for (Track track : tracks) {
			builder.append("Track[").append(track).append("]\n");
		}
		return builder.toString();
	}
}
