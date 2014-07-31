package org.media.web.authentication;

import org.media.web.authentication.exception.AuthenticationException;

import javax.ws.rs.container.ContainerRequestContext;

public class NoAuthentication implements Authenticator {

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	@Override
	public void authenticate(ContainerRequestContext requestContext) throws AuthenticationException {
		// Nothing
	}
}