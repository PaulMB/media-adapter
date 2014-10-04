package org.media.web.config;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;
import org.media.container.config.Configuration;
import org.media.container.merge.io.CommandConfiguration;
import org.media.container.merge.io.impl.CommandConfigurationImpl;
import org.media.container.merge.io.impl.EnvironmentVariable;
import org.media.web.authentication.Authenticator;
import org.media.web.authentication.NoAuthentication;
import org.media.web.config.exception.ComponentStorageException;
import org.media.web.config.impl.MemoryApplicationConfiguration;
import org.media.web.merge.MergeAdapter;
import org.mockito.Mockito;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class MemoryConfigurationResourceTest extends JerseyTest {

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	@Override
	protected Application configure() {
		final ResourceConfig application = new ResourceConfig();
		application.register(new AbstractBinder() {
			@Override
			protected void configure() {
				bind(new NoAuthentication()).to(Authenticator.class);
				bind(new MemoryApplicationConfiguration()).to(ApplicationConfiguration.class);
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
		assertConfigEquals(configuration, "mkvmerge", entry("LC_ALL", "C"), entry("LANG", "C"));
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
}