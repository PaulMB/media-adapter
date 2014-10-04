package org.media.web.config.impl;

import org.media.web.config.exception.ComponentStorageException;

import javax.xml.bind.annotation.XmlAttribute;
import java.lang.reflect.Constructor;
import java.nio.file.Path;
import java.nio.file.Paths;

@SuppressWarnings("UnusedDeclaration")
public class XmlComponent {

	//==================================================================================================================
	// Attributes
	//==================================================================================================================

	private String name;
	private String className;
	private String configuration;

	//==================================================================================================================
	// Constructors
	//==================================================================================================================

	public XmlComponent() {
		// Nothing
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

	//==================================================================================================================
	// Helper methods
	//==================================================================================================================

	public static <T> T createComponent(String className, String configuration, Class<T> expectedType) throws ComponentStorageException {
		try {
			final Class<?> forName = Class.forName(className);
			if (!expectedType.isAssignableFrom(forName)) {
				throw new ClassCastException(className + " is not an instance of " + expectedType);
			}
			if (configuration == null) {
				return expectedType.cast(forName.newInstance());
			} else {
				final Path path = Paths.get(configuration);
				final Constructor<?> constructor = forName.getConstructor(Path.class);
				return expectedType.cast(constructor.newInstance(path));
			}
		} catch (Exception e) {
			throw new ComponentStorageException(e.getMessage());
		}
	}
}
