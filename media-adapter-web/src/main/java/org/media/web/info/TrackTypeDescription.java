package org.media.web.info;

import org.media.container.info.Track;

import javax.xml.bind.annotation.XmlRootElement;

@SuppressWarnings("UnusedDeclaration")
@XmlRootElement
public class TrackTypeDescription {

	//==================================================================================================================
	// Attributes
	//==================================================================================================================

	private String trackId;
	private String path;
	private String name;
	private String codecId;
	private String language;
	private String trackType;

	//==================================================================================================================
	// Constructors
	//==================================================================================================================

	public TrackTypeDescription() {
		// Empty
	}

	public TrackTypeDescription(Track track) {
		this.trackId = track.getId().toExternal();
		this.name = track.getName();
		this.codecId = track.getCodecId();
		this.language = track.getLanguage();
		this.trackType = track.getType().name();
	}

	public TrackTypeDescription(String path) {
		this.path = path;
	}

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	public String getTrackId() {
		return trackId;
	}

	public String getPath() {
		return path;
	}

	public String getName() {
		return name;
	}

	public String getCodecId() {
		return codecId;
	}

	public String getLanguage() {
		return language;
	}

	public String getTrackType() {
		return trackType;
	}

	public void setTrackId(String trackId) {
		this.trackId = trackId;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setCodecId(String codecId) {
		this.codecId = codecId;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public void setTrackType(String trackType) {
		this.trackType = trackType;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		TrackTypeDescription that = (TrackTypeDescription) o;

		if (codecId != null ? !codecId.equals(that.codecId) : that.codecId != null) return false;
		if (language != null ? !language.equals(that.language) : that.language != null) return false;
		if (name != null ? !name.equals(that.name) : that.name != null) return false;
		if (path != null ? !path.equals(that.path) : that.path != null) return false;
		if (trackId != null ? !trackId.equals(that.trackId) : that.trackId != null) return false;
		//noinspection RedundantIfStatement
		if (trackType != null ? !trackType.equals(that.trackType) : that.trackType != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = trackId != null ? trackId.hashCode() : 0;
		result = 31 * result + (path != null ? path.hashCode() : 0);
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + (codecId != null ? codecId.hashCode() : 0);
		result = 31 * result + (language != null ? language.hashCode() : 0);
		result = 31 * result + (trackType != null ? trackType.hashCode() : 0);
		return result;
	}
}
