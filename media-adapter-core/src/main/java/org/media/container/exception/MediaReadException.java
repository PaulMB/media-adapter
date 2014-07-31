package org.media.container.exception;

public class MediaReadException extends Exception {

	//==================================================================================================================
	// Constants
	//==================================================================================================================

	private static final long serialVersionUID = -729620409385509180L;

	//==================================================================================================================
	// Constructors
	//==================================================================================================================

	public MediaReadException(String message) {
		super(message);
	}

	public MediaReadException(String message, Throwable cause) {
		super(message, cause);
	}
}
