package org.media.container.merge.io.impl;

import org.mozilla.universalchardet.UniversalDetector;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class CharsetDetector {

	//==================================================================================================================
	// Attributes
	//==================================================================================================================

	private UniversalDetector detector;

	//==================================================================================================================
	// Constructors
	//==================================================================================================================

	public CharsetDetector() {
		detector = new UniversalDetector(null);
	}

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	public Charset detectCharset(InputStream inputStream) throws IOException {
		byte[] buffer = new byte[4096];
		int readBytes;
		try {
			while ((readBytes = inputStream.read(buffer)) > 0 && ! detector.isDone()) {
				detector.handleData(buffer, 0, readBytes);
			}
			detector.dataEnd();
			final String detectedCharset = detector.getDetectedCharset();
			if ( detectedCharset == null ) {
				return null;
			} else {
				return Charset.forName(detectedCharset);
			}
		} finally {
			detector.reset();
		}
	}
}
