package org.media.container.merge.io.impl;

import org.junit.Test;
import org.media.container.merge.io.IOFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import static junit.framework.Assert.assertEquals;

public class TrackWriterTest {

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	@Test
	public void shouldRemoveAccentsFromString() throws Exception {
		assertEquals("deoapuo", IOFactory.accentRemover().convert("déôàpùö"));
	}

	@Test(expected = IOException.class)
	public void shouldNotConvertFileWithBinaryData() throws Exception {
		final CharacterTrackWriter writer = IOFactory.charWriter(Charset.defaultCharset(), IOFactory.utf8Encoding()).addConverter(IOFactory.accentRemover());
		try (InputStream inputStream = TrackWriterTest.class.getResourceAsStream("/org/media/container/merge/io/impl/srt/sample.pdf")) {
			writer.write(inputStream, new ByteArrayOutputStream());
		}
		System.out.println(Charset.availableCharsets().keySet());
	}

	@Test
	public void shouldConvertSub() throws Exception {
		assertConvert("/sub.expected.srt", "/sub.srt", IOFactory.charWriter(Charset.forName("UTF-8"), IOFactory.encoding("US-ASCII")).addConverter(IOFactory.accentRemover()));
	}

	@Test
	public void shouldConvertToUtf8() throws Exception {
		assertConvert("/complete.sub.utf8.srt", "/complete.sub.srt", IOFactory.charWriter(Charset.forName("ISO-8859-1"), IOFactory.utf8Encoding()));
	}

	@Test
	public void shouldConvertToUtf8AndRemoveAccents() throws Exception {
		assertConvert("/complete.sub.utf8.no.accent.srt", "/complete.sub.srt", IOFactory.charWriter(Charset.forName("ISO-8859-1"), IOFactory.utf8Encoding()).addConverter(IOFactory.accentRemover()));
	}

	@Test
	public void shouldRemoveAccents() throws Exception {
		assertConvert("/complete.sub.no.accent.srt", "/complete.sub.srt", IOFactory.charWriter(Charset.forName("ISO-8859-1"), IOFactory.encoding("ISO-8859-1")).addConverter(IOFactory.accentRemover()));
	}

	//==================================================================================================================
	// Private methods
	//==================================================================================================================

	public static void assertConvert(String expectedResource, String originalResource, CharacterTrackWriter writer) throws Exception {
		final ByteArrayOutputStream expected = new ByteArrayOutputStream();
		if ( writer.getEncoding().getByteOrderMark() != null ) {
			expected.write(writer.getEncoding().getByteOrderMark().getBytes());
		}
		expected.write(Files.readAllBytes(Paths.get(TrackWriterTest.class.getResource("/org/media/container/merge/io/impl/srt" + expectedResource).toURI())));
		final ByteArrayOutputStream output = new ByteArrayOutputStream();
		try(InputStream input = TrackWriterTest.class.getResourceAsStream("/org/media/container/merge/io/impl/srt" + originalResource)){
			writer.write(input, output);
		}
		assertEquals(expected.size(), output.size());
		assertEquals(expected.toString(), output.toString());
	}
}