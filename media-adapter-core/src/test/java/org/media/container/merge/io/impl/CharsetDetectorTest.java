package org.media.container.merge.io.impl;

import org.junit.Test;
import org.media.container.merge.io.IOFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;

import static org.junit.Assert.*;

public class CharsetDetectorTest {

	@Test
	public void shouldNotDetectCharsetOnBinaryFile() throws Exception {
		assertNull(IOFactory.detectCharset(getFile("/info/impl/jebml/sample.mkv")));
	}

	@Test(expected = IOException.class)
	public void shouldFailIfUnknownFile() throws Exception {
		assertNull(IOFactory.detectCharset(new File("/dummy")));
	}

	@Test
	public void shouldDetectUtf8Charset() throws Exception {
		assertEquals(Charset.forName("UTF-8"), IOFactory.detectCharset(getFile("/merge/io/impl/srt/complete.sub.utf8.srt")));
	}

	@Test
	public void shouldDetectIsoCharset() throws Exception {
		assertEquals(Charset.forName("WINDOWS-1252"), IOFactory.detectCharset(getFile("/merge/io/impl/srt/complete.sub.srt")));
	}

	private static File getFile(String path) throws URISyntaxException {
		return new File(CharsetDetectorTest.class.getResource("/org/media/container" + path).toURI());
	}
}