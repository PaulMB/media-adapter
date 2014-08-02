package org.media.web.config;

import org.junit.Test;
import org.media.container.merge.execution.impl.mkvmerge.MkvMergeExecutorFactory;
import org.media.web.authentication.Authenticator;
import org.media.web.authentication.DSMAuthenticator;
import org.mockito.Mockito;

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
		final ApplicationConfiguration configuration = ConfigurationFactory.loadConfiguration(source);
		assertEquals(MkvMergeExecutorFactory.class.getName(), configuration.getExecutorFactory().getClassName());
		assertEquals("/var/packages/MediaAdapter/target/etc/mkvmerge.xml", configuration.getExecutorFactory().getConfiguration());
		assertEquals(DSMAuthenticator.class.getName(), configuration.getAuthenticator().getClassName());
		assertEquals("/var/packages/MediaAdapter/target/etc/dsm-authenticator.xml", configuration.getAuthenticator().getConfiguration());
	}

	@Test(expected = Exception.class)
	public void shouldNotCreateComponentWithClassMismatch() throws Exception {
		ConfigurationFactory.createComponent(component(String.class.getName(), null), Integer.class);
	}

	@Test(expected = Exception.class)
	public void shouldNotCreateComponentWithNoPathConstructor() throws Exception {
		ConfigurationFactory.createComponent(component(Integer.class.getName(), "/tmp"), Integer.class);
	}

	@Test
	public void shouldCreateComponentIfEmptyConstructorIsFound() throws Exception {
		ConfigurationFactory.createComponent(component(String.class.getName(), null), String.class);
	}

	@Test(expected = Exception.class)
	public void shouldNotCreateWithNoEmptyConstructorIfNoPathIsSpecified() throws Exception {
		ConfigurationFactory.createComponent(component(DSMAuthenticator.class.getName(), null), Authenticator.class);
	}

	@Test(expected = Exception.class)
	public void shouldNotCreateWithInvalidPath() throws Exception {
		ConfigurationFactory.createComponent(component(DSMAuthenticator.class.getName(), "/media-adapter.xml"), Authenticator.class);
	}

	@Test
	public void shouldCreateComponentIfConstructorWithPathIsFound() throws Exception {
		final Path source = Paths.get(ConfigurationFactoryTest.class.getResource("/component.xml").toURI());
		ConfigurationFactory.createComponent(component(DSMAuthenticator.class.getName(), source.toString()), Authenticator.class);
	}

	//==================================================================================================================
	// Private methods
	//==================================================================================================================

	private static ApplicationComponent component(String className, String configuration) {
		final ApplicationComponent mock = Mockito.mock(ApplicationComponent.class);
		Mockito.when(mock.getClassName()).thenReturn(className);
		Mockito.when(mock.getConfiguration()).thenReturn(configuration);
		return mock;
	}
}