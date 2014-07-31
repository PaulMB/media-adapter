package org.media.container.merge.io.impl;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

@SuppressWarnings("UnusedDeclaration")
public class XmlEnvironmentVariable {

	//==================================================================================================================
	// Attributes
	//==================================================================================================================

	private String name;
	private String value;

	//==================================================================================================================
	// Constructors
	//==================================================================================================================

	public XmlEnvironmentVariable() {
		//Empty
	}

	public XmlEnvironmentVariable(String name, String value) {
		this.name = name;
		this.value = value;
	}

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	@XmlAttribute(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlValue
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
