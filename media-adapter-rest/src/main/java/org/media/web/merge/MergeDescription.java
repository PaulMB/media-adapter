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
	private List<TrackDescription> tracksToAdd;
	private List<TrackDescription> tracksToRemove;

	//==================================================================================================================
	// Constructors
	//==================================================================================================================

	public MergeDescription() {
		tracksToAdd = new ArrayList<>();
		tracksToRemove = new ArrayList<>();
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

	public List<TrackDescription> getTracksToAdd() {
		return tracksToAdd;
	}

	public void setTracksToAdd(List<TrackDescription> tracks) {
		this.tracksToAdd = tracks;
	}

	public List<TrackDescription> getTracksToRemove() {
		return tracksToRemove;
	}

	public void setTracksToRemove(List<TrackDescription> tracksToRemove) {
		this.tracksToRemove = tracksToRemove;
	}
}
