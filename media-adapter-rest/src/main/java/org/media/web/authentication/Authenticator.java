package org.media.web.authentication;

import org.media.container.config.Configurable;
import org.media.web.authentication.exception.AuthenticationException;

import javax.ws.rs.container.ContainerRequestContext;

public interface Authenticator extends Configurable {

	void authenticate(ContainerRequestContext requestContext) throws AuthenticationException;

}
