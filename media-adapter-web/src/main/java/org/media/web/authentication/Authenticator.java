package org.media.web.authentication;

import org.media.web.authentication.exception.AuthenticationException;

import javax.ws.rs.container.ContainerRequestContext;

public interface Authenticator {

	void authenticate(ContainerRequestContext requestContext) throws AuthenticationException;

}
