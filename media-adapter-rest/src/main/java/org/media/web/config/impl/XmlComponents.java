package org.media.web.config.impl;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

@SuppressWarnings("UnusedDeclaration")
public class XmlComponents {

	//==================================================================================================================
	// Attributes
	//==================================================================================================================

	private String active;
	private List<XmlComponent> executors;

	//==================================================================================================================
	// Constructors
	//==================================================================================================================

	public XmlComponents() {
		// Nothing
	}

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	@XmlElement(name = "executor")
	public List<XmlComponent> getExecutors() {
		return executors;
	}

	public void setExecutors(List<XmlComponent> executors) {
		this.executors = executors;
	}

	@XmlAttribute(name = "active")
	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}
}
