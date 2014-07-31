package org.media.container.info.impl;

import org.media.container.info.TrackId;

public class TrackIdImpl implements TrackId {

	//==================================================================================================================
	// Attributes
	//==================================================================================================================

	private final String id;

	//==================================================================================================================
	// Constructors
	//==================================================================================================================

	public TrackIdImpl(long id) {
		this.id = String.valueOf(id);
	}

	public TrackIdImpl(String id) {
		this.id = id;
	}

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	@Override
	public String toExternal() {
		return id;
	}

	@Override
	public String toString() {
		return this.toExternal();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		TrackIdImpl trackId = (TrackIdImpl) o;

		//noinspection RedundantIfStatement
		if (id != null ? !id.equals(trackId.id) : trackId.id != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : 0;
	}
}
