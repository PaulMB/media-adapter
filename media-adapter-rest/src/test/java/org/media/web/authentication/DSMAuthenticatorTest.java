package org.media.web.authentication;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.Executor;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;
import org.media.container.config.Configuration;
import org.media.container.merge.execution.Merge;
import org.media.container.merge.io.CommandConfiguration;
import org.media.web.authentication.exception.AuthenticationException;
import org.media.web.merge.MergeContext;
import org.mockito.Mockito;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DSMAuthenticatorTest extends JerseyTest implements Authenticator {

	//==================================================================================================================
	// Attributes
	//==================================================================================================================

	private Authenticator authenticator;

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	@Override
	protected Application configure() {
		final MergeContext mergeContext = Mockito.mock(MergeContext.class, Mockito.RETURNS_DEEP_STUBS);
		Mockito.when(mergeContext.getCollector().getMerges()).thenReturn(new Merge[0]);
		final ResourceConfig application = new ResourceConfig();
		application.packages("org.media.web");
		application.register(new AbstractBinder() {
			@Override
			protected void configure() {
				bind(mergeContext).to(MergeContext.class);
				bind(DSMAuthenticatorTest.this).to(Authenticator.class);
			}
		});
		return application;
	}

	@Test
	public void shouldFailIfNoToken() throws Exception {
		this.setDefaultAuthenticator();
		final Response response = target("").request().get();
		assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
	}

	@Test
	public void shouldFailIfEmptyToken() throws Exception {
		this.setDefaultAuthenticator();
		final Response response = target("").request().header("x-syno-token", new ArrayList<String>()).get();
		assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
	}

	@Test
	public void shouldFailIfNoCookie() throws Exception {
		this.setDefaultAuthenticator();
		final Response response = target("").request().header("x-syno-token", "123456").get();
		assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
	}

	@Test
	public void shouldFailIfNoRemoteAddress() throws Exception {
		this.setDefaultAuthenticator();
		final Response response = target("").request().header("x-syno-token", "123456").cookie("id", "123456").get();
		assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
	}

	@Test
	public void shouldFailIfExecutorError() throws Exception {
		this.setDefaultAuthenticator();
		final Response response = target("").request().header("x-syno-token", "123456").header("x-forwarded-for", "localhost").cookie("id", "123456").get();
		assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
	}

	@Test
	public void shouldFailIfNotAdmin() throws Exception {
		this.setAuthenticator(this.createAuthenticator(mockExecutor(), "dummyUser"));
		final Response response = target("").request().header("x-syno-token", "123456").header("x-forwarded-for", "localhost").cookie("id", "123456").get();
		assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
	}

	@Test
	public void shouldSucceedIfAdmin() throws Exception {
		this.setAuthenticator(this.createAuthenticator(mockExecutor(), "admin"));
		final Response response = target("media/merge").request().header("x-syno-token", "123456").header("x-forwarded-for", "localhost").cookie("id", "123456").get();
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
	}

	@Test
	public void shouldReturnValidExecutor() throws Exception {
		final DSMAuthenticator dsmAuthenticator = this.createAuthenticator(mockExecutor(), "");
		final Executor executor = dsmAuthenticator.createExecutor(new ByteArrayOutputStream());
		assertNotNull(executor);
		assertNotNull(executor.getStreamHandler());
		assertNotNull(executor.getWatchdog());
	}

	//==================================================================================================================
	// Private methods
	//==================================================================================================================

	public void setAuthenticator(Authenticator authenticator) {
		this.authenticator = authenticator;
	}

	public void setDefaultAuthenticator() throws IOException {
		this.setAuthenticator(this.createAuthenticator(mockErrorExecutor(), ""));
	}

	private static Executor mockErrorExecutor() throws IOException {
		final Executor executor = Mockito.mock(Executor.class);
		Mockito.when(executor.execute(Mockito.any(CommandLine.class), Mockito.any(Map.class))).thenThrow(new IOException());
		return executor;
	}

	private static Executor mockExecutor() throws IOException {
		final Executor executor = Mockito.mock(Executor.class);
		Mockito.when(executor.execute(Mockito.any(CommandLine.class), Mockito.any(Map.class))).thenReturn(0);
		return executor;
	}

	private DSMAuthenticator createAuthenticator(final Executor executor, final String resultMessage) {
		return new DSMAuthenticator(mockConfig(), new DSMExecutorFactory() {
			@Override
			public Executor createExecutor(OutputStream outputStream) {
				try {
					outputStream.write(resultMessage.getBytes());
					return executor;
				} catch (IOException e) {
					throw new Error(e);
				}
			}
		});
	}

	private static CommandConfiguration mockConfig() {
		final CommandConfiguration configuration = Mockito.mock(CommandConfiguration.class);
		Mockito.when(configuration.getBinary()).thenReturn(Paths.get("/bin/auth"));
		return configuration;
	}

	@Override
	public void authenticate(ContainerRequestContext requestContext) throws AuthenticationException {
		this.authenticator.authenticate(requestContext);
	}

	@Override
	public Configuration getConfiguration() {
		return this.authenticator.getConfiguration();
	}
}