package org.media.web.config.impl;

import org.media.web.config.ApplicationComponent;

import javax.xml.bind.annotation.XmlAttribute;

@SuppressWarnings("UnusedDeclaration")
public class XmlApplicationComponent implements ApplicationComponent {

	//==================================================================================================================
	// Attributes
	//==================================================================================================================

	private String className;
	private String configuration;

	//==================================================================================================================
	// Constructors
	//==================================================================================================================

	public XmlApplicationComponent() {
		// Nothing
	}

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	@XmlAttribute(name = "class")
	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	@XmlAttribute(name = "configuration")
	public String getConfiguration() {
		return configuration;
	}

	public void setConfiguration(String configuration) {
		this.configuration = configuration;
	}
}
