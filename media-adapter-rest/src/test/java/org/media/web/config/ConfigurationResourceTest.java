package org.media.web.config;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.media.container.config.Configuration;
import org.media.container.merge.execution.impl.ffmpeg.FFMpegExecutorFactory;
import org.media.container.merge.execution.impl.mkvmerge.MkvMergeExecutorFactory;
import org.media.container.merge.io.CommandConfiguration;
import org.media.container.merge.io.IOFactory;
import org.media.container.merge.io.impl.CommandConfigurationImpl;
import org.media.container.merge.io.impl.EnvironmentVariable;
import org.media.web.authentication.Authenticator;
import org.media.web.authentication.NoAuthentication;
import org.media.web.config.exception.ComponentStorageException;
import org.media.web.config.impl.XmlApplicationConfiguration;
import org.media.web.config.impl.XmlComponent;
import org.media.web.config.impl.XmlComponents;
import org.media.web.config.impl.XmlConfiguration;
import org.media.web.merge.MergeAdapter;
import org.mockito.Mockito;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class ConfigurationResourceTest extends JerseyTest {

	//==================================================================================================================
	// Attributes
	//==================================================================================================================

	private Path config;
	private Path ffmpegConfig;
	private Path mkvmergeConfig;

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
	}

	@Override
	@After
	public void tearDown() throws Exception {
		super.tearDown();
		Files.deleteIfExists(ffmpegConfig);
		Files.deleteIfExists(mkvmergeConfig);
		Files.deleteIfExists(config);
	}

	@Override
	protected Application configure() {
		final ApplicationConfiguration applicationConfiguration = this.getApplicationConfiguration();
		final ResourceConfig application = new ResourceConfig();
		application.register(new AbstractBinder() {
			@Override
			protected void configure() {
				bind(new NoAuthentication()).to(Authenticator.class);
				bind(applicationConfiguration).to(ApplicationConfiguration.class);
			}
		});
		application.register(MultiPartFeature.class);
		application.register(MergeAdapter.class);
		application.register(JacksonFeature.class);
		application.packages("org.media.web");
		return application;
	}

	protected void configureClient(final ClientConfig config) {
    	config.register(JacksonFeature.class);
 	}

	@Test
	public void shouldReturnExecutors() {
		final List merges = target("media/config/executor/component").request().get(List.class);
		assertEquals(2, merges.size());
		assertEquals(Arrays.asList("ffmpeg", "mkvmerge"), merges);
	}

	@Test
	public void shouldGetActiveExecutor() {
		assertEquals("ffmpeg", target("media/config/executor/active").request().get(String.class));
	}

	@Test
	public void shouldSetActiveExecutor() throws ComponentStorageException {
		assertEquals(Response.Status.NO_CONTENT.getStatusCode(), target("media/config/executor/active").request().put(Entity.text("mkvmerge")).getStatus());
		assertEquals("mkvmerge", target("media/config/executor/active").request().get(String.class));
		assertEquals("mkvmerge", ConfigurationFactory.loadConfiguration("xml:" + config).getActiveExecutor());
	}

	@Test
	public void shouldNotSetUnknownActiveExecutor() {
		assertEquals(Response.Status.NOT_FOUND.getStatusCode(), target("media/config/executor/active").request().put(Entity.text("dummy")).getStatus());
	}

	@Test
	public void shouldGetConfigWithoutEnv() {
		final Configuration configuration = target("media/config/executor/component/ffmpeg").request().get(Configuration.class);
		assertTrue(configuration instanceof CommandConfiguration);
		final CommandConfiguration commandConfiguration = (CommandConfiguration) configuration;
		assertEquals(0, commandConfiguration.getEnvironment().size());
	}

	@Test
	public void shouldGetConfigWithEnv() {
		final Configuration configuration = target("media/config/executor/component/mkvmerge").request().get(Configuration.class);
		assertConfigEquals(configuration, "/bin/mkvmerge", entry("LC_ALL", "C"), entry("LANG", "C"));
	}

	@Test
	public void shouldNotGetUnknownConfig() {
		assertEquals(Response.Status.NOT_FOUND.getStatusCode(), target("media/config/executor/component/dummy").request().get().getStatus());
	}

	@Test
	public void shouldSetConfigWithEnv() throws IOException {
		final CommandConfigurationImpl configuration = new CommandConfigurationImpl();
		configuration.setBinaryPath("/opt/flu");
		EnvironmentVariable variable = new EnvironmentVariable();
		variable.setName("VAR");
		variable.setValue("OK");
		configuration.setEnvironmentVariables(Arrays.asList(variable));
		assertEquals(Response.Status.NO_CONTENT.getStatusCode(), target("media/config/executor/component/ffmpeg").request().put(Entity.json(configuration)).getStatus());
		assertConfigEquals(target("media/config/executor/component/ffmpeg").request().get(Configuration.class), "/opt/flu", entry("VAR", "OK"));
		assertConfigEquals(IOFactory.loadConfiguration(ffmpegConfig), "/opt/flu", entry("VAR", "OK"));
	}

	@Test
	public void shouldNotSetUnknownConfig() {
		final CommandConfigurationImpl configuration = new CommandConfigurationImpl();
		configuration.setBinaryPath("/opt/flu");
		assertEquals(Response.Status.NOT_FOUND.getStatusCode(), target("media/config/executor/component/dummy").request().put(Entity.json(configuration)).getStatus());
	}

	//==================================================================================================================
	// Private methods
	//==================================================================================================================

	private static void assertConfigEquals(Configuration configuration, String expectedBin, Map.Entry... entries) {
		final CommandConfiguration commandConfiguration = (CommandConfiguration) configuration;
		assertEquals(expectedBin, commandConfiguration.getBinary().toString());
		HashMap<String, String> env = new HashMap<>();
		for (Map.Entry entry : entries) {
			env.put(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
		}
		assertEquals(env, commandConfiguration.getEnvironment());

	}

	private static Map.Entry entry(String key, String value) {
		Map.Entry entry = Mockito.mock(Map.Entry.class);
		Mockito.when(entry.getKey()).thenReturn(key);
		Mockito.when(entry.getValue()).thenReturn(value);
		return entry;
	}

	private ApplicationConfiguration getApplicationConfiguration() {
		final String tmpdir = System.getProperty("java.io.tmpdir");
		final long current = System.currentTimeMillis();
		config = Paths.get(tmpdir, current + "config.xml");
		ffmpegConfig = Paths.get(tmpdir, current + "ffmpeg.xml");
		mkvmergeConfig = Paths.get(tmpdir, current + "mkvmerge.xml");
		final XmlConfiguration applicationConfiguration = new XmlConfiguration();
		final XmlComponents executors = new XmlComponents();
		final XmlComponent ffmpeg = new XmlComponent();
		ffmpeg.setClassName(FFMpegExecutorFactory.class.getName());
		ffmpeg.setName("ffmpeg");
		ffmpeg.setConfiguration(ffmpegConfig.toString());
		final XmlComponent mkvmerge = new XmlComponent();
		mkvmerge.setClassName(MkvMergeExecutorFactory.class.getName());
		mkvmerge.setName("mkvmerge");
		mkvmerge.setConfiguration(mkvmergeConfig.toString());
		executors.setActive("ffmpeg");
		executors.setExecutors(Arrays.asList(ffmpeg, mkvmerge));
		applicationConfiguration.setExecutors(executors);
		final XmlComponent noAuth = new XmlComponent();
		noAuth.setClassName(NoAuthentication.class.getName());
		noAuth.setName("noAuth");
		applicationConfiguration.setAuthenticator(noAuth);
		try {
			Files.copy(Paths.get(ConfigurationResourceTest.class.getResource("/ffmpeg.xml").toURI()), ffmpegConfig, StandardCopyOption.REPLACE_EXISTING);
			Files.copy(Paths.get(ConfigurationResourceTest.class.getResource("/mkvmerge.xml").toURI()), mkvmergeConfig, StandardCopyOption.REPLACE_EXISTING);
			return new XmlApplicationConfiguration(applicationConfiguration, config);
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}
	}
}