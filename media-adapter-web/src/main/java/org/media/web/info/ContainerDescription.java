package org.media.web.info;

import org.media.container.info.Container;
import org.media.container.info.Track;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnusedDeclaration")
@XmlRootElement
public class ContainerDescription {

	//==================================================================================================================
	// Attributes
	//==================================================================================================================

	private String title;
	private double duration;
	private List<TrackDescription> tracks;

	//==================================================================================================================
	// Constructors
	//==================================================================================================================

	public ContainerDescription() {
	}

	public ContainerDescription(Container container) {
		this.title = container.getTitle();
		this.duration = container.getDuration();
		this.tracks = new ArrayList<>();
		for (Track track : container.getTracks()) {
			this.tracks.add(new TrackDescription(track));
		}
	}

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	public String getTitle() {
		return title;
	}

	public double getDuration() {
		return duration;
	}

	public List<TrackDescription> getTracks() {
		return tracks;
	}
}
