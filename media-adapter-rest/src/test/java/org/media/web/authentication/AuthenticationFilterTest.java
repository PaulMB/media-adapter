package org.media.web.authentication;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;
import org.media.container.config.Configuration;
import org.media.web.authentication.exception.AuthenticationException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

public class AuthenticationFilterTest extends JerseyTest {

	@Override
	protected Application configure() {
		final ResourceConfig application = new ResourceConfig();
		application.packages("org.media.web");
		application.register(new AbstractBinder() {
			@Override
			protected void configure() {
				bind(new DenyAll()).to(Authenticator.class);
			}
		});
		return application;
	}

	@Test
	public void shouldDenyAccess() throws Exception {
		final String file = AuthenticationFilterTest.class.getResource("/sample.mkv").getFile();
		assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), target("info/" + file).request().get().getStatus());
	}

	private class DenyAll implements Authenticator {

		@Override
		public void authenticate(ContainerRequestContext requestContext) throws AuthenticationException {
			throw new AuthenticationException("Not authorized");
		}

		@Override
		public Configuration getConfiguration() {
			return null;
		}
	}
}