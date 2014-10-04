package org.media.container.merge.io;

import org.junit.Test;

import java.io.OutputStream;

import static org.junit.Assert.*;

public class ByteBufferOutputStreamTest {

	@Test
	public void shouldAddStringIfSubCapacity() throws Exception {
		final OutputStream stream = stream(4);
		stream.write("abc".getBytes());
		assertEquals("abc", stream.toString());
	}

	@Test
	public void shouldCompactStringIfOverCapacity() throws Exception {
		final OutputStream stream = stream(4);
		stream.write("abcdefgh".getBytes());
		assertEquals("efgh", stream.toString());
	}

	@Test
	public void shouldCompactStringSeveralTimesIfOverCapacity() throws Exception {
		final OutputStream stream = stream(4);
		stream.write("abcdefgh".getBytes());
		stream.write("ijk".getBytes());
		assertEquals("hijk", stream.toString());
	}

	@Test
	public void shouldAddByteIfSubCapacity() throws Exception {
		final OutputStream stream = stream(4);
		stream.write('a');
		assertEquals("a", stream.toString());
	}

	@Test
	public void shouldCompactByteIfOverCapacity() throws Exception {
		final OutputStream stream = stream(4);
		stream.write("abcde".getBytes());
		stream.write('f');
		assertEquals("cdef", stream.toString());
	}

	private static OutputStream stream(int capacity) {
		return new ByteBufferOutputStream(capacity);
	}
}