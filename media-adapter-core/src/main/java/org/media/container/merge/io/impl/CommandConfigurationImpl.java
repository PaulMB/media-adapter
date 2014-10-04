package org.media.container.merge.io.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.media.container.merge.io.CommandConfiguration;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("UnusedDeclaration")
@XmlRootElement(name = "command-configuration")
public class CommandConfigurationImpl implements CommandConfiguration {

	//==================================================================================================================
	// Attributes
	//==================================================================================================================

	private String binaryPath;
	private Map<String, String> environmentVariables;

	//==================================================================================================================
	// Constructors
	//==================================================================================================================

	public CommandConfigurationImpl() {
		this.environmentVariables = new HashMap<>();
	}

	public CommandConfigurationImpl(String binary) {
		this();
		this.binaryPath = binary;
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

	@XmlElementWrapper(name = "environment")
	@XmlElement(name = "variable")
	@JsonProperty("environment")
	public List<EnvironmentVariable> getEnvironmentVariables() {
		final List<EnvironmentVariable> variables = new ArrayList<>();
		for (String key : environmentVariables.keySet()) {
			variables.add(new EnvironmentVariable(key, environmentVariables.get(key)));
		}
		return variables;
	}

	public void setEnvironmentVariables(List<EnvironmentVariable> variables) {
		this.environmentVariables.clear();
		for (EnvironmentVariable environmentVariable : variables) {
			this.environmentVariables.put(environmentVariable.getName(), environmentVariable.getValue());
		}
	}

	@Override
	@XmlTransient
	public Path getBinary() {
		return this.getBinaryPath() == null ? null : Paths.get(this.getBinaryPath());
	}

	@Override
	@XmlTransient
	public Map<String, String> getEnvironment() {
		return this.environmentVariables;
	}
}
