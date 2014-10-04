package org.media.container.merge.io;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * Byte output stream with a constant size. If the bytes to write exceed capacity, only the trailing part is kept.
 */
public class ByteBufferOutputStream extends OutputStream {

	//==================================================================================================================
	// Attributes
	//==================================================================================================================

	private final ByteBuffer buffer;

	//==================================================================================================================
	// Constructors
	//==================================================================================================================

	public ByteBufferOutputStream(int capacity) {
		this.buffer = ByteBuffer.allocate(capacity);
	}

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	@Override
	public void write(int b) throws IOException {
		if ( buffer.position() == buffer.capacity() ) {
			buffer.position(1);
			buffer.compact();
		}
		buffer.put((byte) b);
	}

	@SuppressWarnings("NullableProblems")
	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		if (b == null) {
			throw new NullPointerException();
		} else if ((off < 0) || (off > b.length) || (len < 0) || ((off + len) > b.length) || ((off + len) < 0)) {
			throw new IndexOutOfBoundsException();
		} else if (len == 0) {
			return;
		}
		final int actualLen = Math.min(len, buffer.capacity());
		if ( buffer.position() + actualLen > buffer.capacity() ) {
			buffer.position(actualLen);
			buffer.compact();
		}
		buffer.put(b, off + (len - actualLen), actualLen);
	}

	@Override
	public String toString() {
		return new String(buffer.array(), 0, buffer.position());
	}

	@Override
	public void close() throws IOException {
	}
}
