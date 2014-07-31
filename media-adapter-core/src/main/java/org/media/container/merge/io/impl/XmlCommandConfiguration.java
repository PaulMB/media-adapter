package org.media.container.merge.io.impl;

import org.media.container.merge.io.CommandConfiguration;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("UnusedDeclaration")
@XmlRootElement(name = "command-configuration")
public class XmlCommandConfiguration implements CommandConfiguration {

	//==================================================================================================================
	// Attributes
	//==================================================================================================================

	private String binaryPath;
	private String workDirectoryPath;
	private List<XmlEnvironmentVariable> environmentVariables;

	//==================================================================================================================
	// Constructors
	//==================================================================================================================

	public XmlCommandConfiguration() {
		// Empty
	}

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	@XmlElement(name = "binary")
	public String getBinaryPath() {
		return binaryPath;
	}

	public void setBinaryPath(String binaryPath) {
		this.binaryPath = binaryPath;
	}

	@XmlElement(name = "work-directory")
	public String getWorkDirectoryPath() {
		return workDirectoryPath;
	}

	public void setWorkDirectoryPath(String workDirectoryPath) {
		this.workDirectoryPath = workDirectoryPath;
	}

	@XmlElementWrapper(name = "environment")
	@XmlElement(name = "variable")
	public List<XmlEnvironmentVariable> getEnvironmentVariables() {
		return environmentVariables;
	}

	public void setEnvironmentVariables(List<XmlEnvironmentVariable> environmentVariables) {
		this.environmentVariables = environmentVariables;
	}

	@Override
	@XmlTransient
	public Path getBinary() {
		return binaryPath == null ? null : Paths.get(binaryPath);
	}

	@Override
	@XmlTransient
	public Path getWorkDirectory() {
		return workDirectoryPath == null ? null : Paths.get(workDirectoryPath);
	}

	@Override
	@XmlTransient
	public Map<String, String> getEnvironment() {
		final Map<String, String> environment = new HashMap<>();
		if ( environmentVariables == null ) {
			return environment;
		}
		for (XmlEnvironmentVariable variable : environmentVariables) {
			environment.put(variable.getName(), variable.getValue());
		}
		return environment;
	}
}
