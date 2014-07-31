package org.media.web.merge;

import org.media.web.info.TrackDescription;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnusedDeclaration")
@XmlRootElement
public class MergeDescription {

	//==================================================================================================================
	// Attributes
	//==================================================================================================================

	private String input;
	private List<TrackDescription> tracks;

	//==================================================================================================================
	// Constructors
	//==================================================================================================================

	public MergeDescription() {
		tracks = new ArrayList<>();
	}

	public MergeDescription(String input) {
		this();
		this.input = input;
	}

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public List<TrackDescription> getTracks() {
		return tracks;
	}

	public void setTracks(List<TrackDescription> tracks) {
		this.tracks = tracks;
	}
}
