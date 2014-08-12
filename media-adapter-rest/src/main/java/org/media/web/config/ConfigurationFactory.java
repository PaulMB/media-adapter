package org.media.web.config;

import org.media.web.config.impl.XmlApplicationConfiguration;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConfigurationFactory {

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	public static ApplicationConfiguration loadConfiguration(Path path) throws IOException {
		try {
			final JAXBContext context = JAXBContext.newInstance(XmlApplicationConfiguration.class);
			final Unmarshaller marshaller = context.createUnmarshaller();
			try (InputStream inputStream = Files.newInputStream(path)) {
				return (ApplicationConfiguration) marshaller.unmarshal(inputStream);
			}
		} catch (JAXBException e) {
			throw new IOException(e.getMessage(), e);
		}
	}

	public static <T> T createComponent(ApplicationComponent applicationComponent, Class<T> expectedType) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
		final String className = applicationComponent.getClassName();
		final Class<?> forName = Class.forName(className);
		if ( ! expectedType.isAssignableFrom(forName) ) {
			throw new ClassCastException(className + " is not an instance of " + expectedType);
		}
		final String configuration = applicationComponent.getConfiguration();
		if ( configuration == null ) {
			return expectedType.cast(forName.newInstance());
		} else {
			final Path path = Paths.get(configuration);
			final Constructor<?> constructor = forName.getConstructor(Path.class);
			return expectedType.cast(constructor.newInstance(path));
		}
	}
}
