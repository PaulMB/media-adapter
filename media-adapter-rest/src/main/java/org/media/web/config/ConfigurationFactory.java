package org.media.web.config;

import org.media.web.config.exception.ComponentStorageException;
import org.media.web.config.impl.MemoryApplicationConfiguration;
import org.media.web.config.impl.XmlApplicationConfiguration;

import java.nio.file.Paths;

public class ConfigurationFactory {

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	public static ApplicationConfiguration loadConfiguration(final String config) throws ComponentStorageException {
		if ( config.startsWith("xml:") ) {
			return new XmlApplicationConfiguration(Paths.get(config.substring(config.indexOf(':') + 1)));
		} else if ( config.startsWith("memory")) {
			return new MemoryApplicationConfiguration();
		} else {
			throw new IllegalArgumentException("invalid configuration; must be 'memory' or 'xml:/path/to/config.xml'");
		}
	}
}
