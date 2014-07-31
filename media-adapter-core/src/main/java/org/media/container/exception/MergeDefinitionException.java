package org.media.container.exception;

public class MergeDefinitionException extends Exception {

	//==================================================================================================================
	// Constants
	//==================================================================================================================

	private static final long serialVersionUID = 5220487789223862411L;

	//==================================================================================================================
	// Constructors
	//==================================================================================================================

	public MergeDefinitionException(Throwable cause) {
		super(cause.getMessage(), cause);
	}

	public MergeDefinitionException(String message) {
		super(message);
	}
}
