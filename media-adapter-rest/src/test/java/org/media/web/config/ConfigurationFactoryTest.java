package org.media.web.config;

import org.junit.Test;
import org.media.web.authentication.Authenticator;
import org.media.web.authentication.DSMAuthenticator;
import org.media.web.config.exception.ComponentStorageException;
import org.media.web.config.impl.XmlComponent;
import org.media.web.config.impl.XmlConfiguration;

import java.nio.file.Path;
import java.nio.file.Paths;

import static junit.framework.Assert.assertEquals;

public class ConfigurationFactoryTest {

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	@Test
	public void shouldLoadConfiguration() throws Exception {
		final Path source = Paths.get(ConfigurationFactoryTest.class.getResource("/media-adapter.xml").toURI());
		final XmlConfiguration configuration = XmlConfiguration.create(source);
		final XmlComponent authenticator = configuration.getAuthenticator();
		assertEquals(2, configuration.getExecutors().getExecutors().size());
		assertEquals("ffmpeg", configuration.getExecutors().getActive());
		final XmlComponent firstComponent = configuration.getExecutors().getExecutors().get(0);
		final XmlComponent secondComponent = configuration.getExecutors().getExecutors().get(1);
		assertEquals("org.media.container.merge.execution.impl.ffmpeg.FFMpegExecutorFactory", firstComponent.getClassName());
		assertEquals("/var/packages/MediaAdapter/target/etc/ffmpeg.xml", firstComponent.getConfiguration());
		assertEquals("org.media.container.merge.execution.impl.mkvmerge.MkvMergeExecutorFactory", secondComponent.getClassName());
		assertEquals("/var/packages/MediaAdapter/target/etc/mkvmerge.xml", secondComponent.getConfiguration());
		assertEquals("org.media.web.authentication.DSMAuthenticator", authenticator.getClassName());
		assertEquals("/var/packages/MediaAdapter/target/etc/dsm-authenticator.xml", authenticator.getConfiguration());
	}

	@Test(expected = ComponentStorageException.class)
	public void shouldNotLoadInvalidConfiguration() throws Exception {
		final Path source = Paths.get(ConfigurationFactoryTest.class.getResource("/invalid.media-adapter.xml").toURI());
		ConfigurationFactory.loadConfiguration("xml:" + source.toString());
	}

	@Test(expected = Exception.class)
	public void shouldNotCreateComponentWithClassMismatch() throws Exception {
		XmlComponent.createComponent(String.class.getName(), null, Integer.class);
	}

	@Test(expected = Exception.class)
	public void shouldNotCreateComponentWithNoPathConstructor() throws Exception {
		XmlComponent.createComponent(Integer.class.getName(), "/tmp", Integer.class);
	}

	@Test
	public void shouldCreateComponentIfEmptyConstructorIsFound() throws Exception {
		XmlComponent.createComponent(String.class.getName(), null, String.class);
	}

	@Test(expected = Exception.class)
	public void shouldNotCreateWithNoEmptyConstructorIfNoPathIsSpecified() throws Exception {
		XmlComponent.createComponent(DSMAuthenticator.class.getName(), null, Authenticator.class);
	}

	@Test(expected = Exception.class)
	public void shouldNotCreateWithInvalidPath() throws Exception {
		XmlComponent.createComponent(DSMAuthenticator.class.getName(), "/media-adapter.xml", Authenticator.class);
	}

	@Test
	public void shouldCreateComponentIfConstructorWithPathIsFound() throws Exception {
		final Path source = Paths.get(ConfigurationFactoryTest.class.getResource("/component.xml").toURI());
		XmlComponent.createComponent(DSMAuthenticator.class.getName(), source.toString(), Authenticator.class);
	}
}