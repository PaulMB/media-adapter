package org.media.web.config.impl;

import org.media.web.config.exception.ComponentStorageException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@SuppressWarnings("UnusedDeclaration")
@XmlRootElement(name = "media-adapter")
public class XmlConfiguration {

	//==================================================================================================================
	// Attributes
	//==================================================================================================================

	private XmlComponents executors;
	private XmlComponent authenticator;

	//==================================================================================================================
	// Constructors
	//==================================================================================================================

	public XmlConfiguration() {
		// Nothing
	}

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	@XmlElement(name = "executors")
	public XmlComponents getExecutors() {
		return executors;
	}

	public void setExecutors(XmlComponents executors) {
		this.executors = executors;
	}

	@XmlElement(name = "authenticator")
	public XmlComponent getAuthenticator() {
		return authenticator;
	}

	public void setAuthenticator(XmlComponent authenticator) {
		this.authenticator = authenticator;
	}

	public static XmlConfiguration create(Path path) throws ComponentStorageException {
		try {
			final JAXBContext context = JAXBContext.newInstance(XmlConfiguration.class);
			final Unmarshaller marshaller = context.createUnmarshaller();
			try (InputStream inputStream = Files.newInputStream(path)) {
				return (XmlConfiguration) marshaller.unmarshal(inputStream);
			}
		} catch (Exception e) {
			throw new ComponentStorageException(e.getMessage());
		}
	}
}
