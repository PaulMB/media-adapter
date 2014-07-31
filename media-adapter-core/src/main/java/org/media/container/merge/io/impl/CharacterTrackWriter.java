package org.media.container.merge.io.impl;

import org.media.container.merge.io.CharacterConverter;
import org.media.container.merge.io.CharacterEncoding;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class CharacterTrackWriter {

	//==================================================================================================================
	// Private methods
	//==================================================================================================================

	private final CharacterEncoding encoding;
	private final Charset sourceCharset;
	private final List<CharacterConverter> converters;

	//==================================================================================================================
	// Constructors
	//==================================================================================================================

	public CharacterTrackWriter(Charset sourceCharset, CharacterEncoding encoding) {
		this.sourceCharset = sourceCharset;
		this.encoding = encoding;
		this.converters = new ArrayList<>();
	}

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	public CharacterTrackWriter addConverter(CharacterConverter characterConverter) {
		this.converters.add(characterConverter);
		return this;
	}

	public void write(InputStream inputStream, OutputStream outputStream) throws IOException {
		final Writer writer = new OutputStreamWriter(outputStream, encoding.getCharset().newEncoder());
		final String byteOrderMark = encoding.getByteOrderMark();
		if ( byteOrderMark != null ) {
			writer.write(byteOrderMark);
		}
		char[] buffer = new char[8192];
		int read;

		final InputStreamReader inputStreamReader = new InputStreamReader(inputStream, sourceCharset);
		while( (read = inputStreamReader.read(buffer)) >= 0 ) {
			String readSequence = new String(buffer, 0, read);
			for (CharacterConverter converter : converters) {
				readSequence = converter.convert(readSequence);
			}
		  	writer.write(readSequence);
			writer.flush();
		}
	}

	public CharacterEncoding getEncoding() {
		return encoding;
	}
}
