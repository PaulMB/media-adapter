package org.media.web.authentication;

import org.media.web.authentication.exception.AuthenticationException;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@SuppressWarnings("UnusedDeclaration")
@Provider
@PreMatching
public class AuthenticationFilter implements ContainerRequestFilter {

	//==================================================================================================================
	// Attributes
	//==================================================================================================================

	@Inject
	private Authenticator authenticator;

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		try {
			authenticator.authenticate(requestContext);
		} catch (AuthenticationException e) {
			requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build());
		}
	}
}
