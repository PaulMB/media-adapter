package org.media.web.authentication.exception;

public class AuthenticationException extends Exception {

	public AuthenticationException(Exception cause) {
		super(cause.getMessage(), cause);
	}

	public AuthenticationException(String message) {
		super(message);
	}
}
