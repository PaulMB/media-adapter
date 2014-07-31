package org.media.container.exception;

public class MergeSubmitException extends Exception {

	//==================================================================================================================
	// Constants
	//==================================================================================================================

	private static final long serialVersionUID = 5220487789223862411L;

	//==================================================================================================================
	// Constructors
	//==================================================================================================================

	public MergeSubmitException(Throwable cause) {
		super(cause.getMessage(), cause);
	}

	public MergeSubmitException(String message) {
		super(message);
	}
}
