package org.media.web.config.impl;

import org.media.web.config.ApplicationConfiguration;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@SuppressWarnings("UnusedDeclaration")
@XmlRootElement(name = "media-adapter")
public class XmlApplicationConfiguration implements ApplicationConfiguration {

	//==================================================================================================================
	// Attributes
	//==================================================================================================================

	private XmlApplicationComponent executorFactory;
	private XmlApplicationComponent authenticator;

	//==================================================================================================================
	// Constructors
	//==================================================================================================================

	public XmlApplicationConfiguration() {
		// Nothing
	}

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	@XmlElement(name = "executor-factory")
	public XmlApplicationComponent getExecutorFactory() {
		return executorFactory;
	}

	public void setExecutorFactory(XmlApplicationComponent executorFactory) {
		this.executorFactory = executorFactory;
	}

	@XmlElement(name = "authenticator")
	public XmlApplicationComponent getAuthenticator() {
		return authenticator;
	}

	public void setAuthenticator(XmlApplicationComponent authenticator) {
		this.authenticator = authenticator;
	}
}
