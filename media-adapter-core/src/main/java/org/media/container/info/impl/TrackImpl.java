package org.media.container.info.impl;

import org.media.container.info.Track;
import org.media.container.info.TrackId;
import org.media.container.info.TrackType;
import org.media.container.merge.MergeFactory;

public class TrackImpl implements Track {

	//==================================================================================================================
	// Attributes
	//==================================================================================================================

	private TrackId id;
	private String name;
	private String codecId;
	private String language;
	private TrackType trackType;

	//==================================================================================================================
	// Constructors
	//==================================================================================================================

	@SuppressWarnings("UnusedDeclaration")
	public TrackImpl() {
		// Empty
	}

	public TrackImpl(long number, String name, String codecId, String language, TrackType trackType) {
		this.id = MergeFactory.trackId(number);
		this.name = name;
		this.codecId = codecId;
		this.language = language;
		this.trackType = trackType;
	}

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	@Override
	public TrackId getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public TrackType getType() {
		return trackType;
	}

	@Override
	public String getCodecId() {
		return codecId;
	}

	@Override
	public String getLanguage() {
		return language;
	}

	@Override
	public String toString() {
		return "id=" + id + ", name=" + name + ", codecId=" + codecId + ", language=" + language + ", type=" + trackType;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		TrackImpl track = (TrackImpl) o;

		if (codecId != null ? !codecId.equals(track.codecId) : track.codecId != null) return false;
		if (id != null ? !id.equals(track.id) : track.id != null) return false;
		if (language != null ? !language.equals(track.language) : track.language != null) return false;
		if (name != null ? !name.equals(track.name) : track.name != null) return false;
		//noinspection RedundantIfStatement
		if (trackType != track.trackType) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + (codecId != null ? codecId.hashCode() : 0);
		result = 31 * result + (language != null ? language.hashCode() : 0);
		result = 31 * result + (trackType != null ? trackType.hashCode() : 0);
		return result;
	}
}
