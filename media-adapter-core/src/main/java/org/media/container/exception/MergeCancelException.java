package org.media.container.exception;

public class MergeCancelException extends Exception {

	//==================================================================================================================
	// Constants
	//==================================================================================================================

	private static final long serialVersionUID = 7009645850724830363L;

	//==================================================================================================================
	// Constructors
	//==================================================================================================================

	public MergeCancelException(String message, Throwable cause) {
		super(message, cause);
	}

	public MergeCancelException(String message) {
		super(message);
	}
}
