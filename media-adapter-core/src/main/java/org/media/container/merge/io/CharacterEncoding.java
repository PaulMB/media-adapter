package org.media.container.merge.io;

import java.nio.charset.Charset;

public class CharacterEncoding {

	//==================================================================================================================
	// Attributes
	//==================================================================================================================

	private String byteOrderMark;
	private Charset charset;

	//==================================================================================================================
	// Constructors
	//==================================================================================================================

	public CharacterEncoding(Charset charset) {
		this(charset, null);
	}

	public CharacterEncoding(Charset charset, String byteOrderMark) {
		this.byteOrderMark = byteOrderMark;
		this.charset = charset;
	}


	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	public Charset getCharset() {
		return charset;
	}

	public String getByteOrderMark() {
		return byteOrderMark;
	}
}
