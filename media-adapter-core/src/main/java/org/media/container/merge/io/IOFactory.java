package org.media.container.merge.io;

import org.media.container.merge.io.impl.AccentRemover;
import org.media.container.merge.io.impl.CharacterTrackWriter;
import org.media.container.merge.io.impl.CharsetDetector;
import org.media.container.merge.io.impl.XmlCommandConfiguration;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFileAttributes;

public class IOFactory {

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	public static CharacterTrackWriter charWriter(Charset sourceCharset, CharacterEncoding encoding) {
		return new CharacterTrackWriter(sourceCharset, encoding);
	}

	public static CharacterConverter accentRemover() {
		return new AccentRemover();
	}

	public static CharacterEncoding utf8Encoding() {
		return encoding(Charset.forName("UTF-8"), "\uFEFF");
	}

	public static CharacterEncoding encoding(String charset) {
		return encoding(Charset.forName(charset));
	}

	public static CharacterEncoding encoding(Charset charset) {
		return new CharacterEncoding(charset);
	}

	public static CharacterEncoding encoding(Charset charset, String byteOrderMark) {
		return new CharacterEncoding(charset, byteOrderMark);
	}

	public static Charset detectCharset(File file) throws IOException {
		try (InputStream inputStream = Files.newInputStream(file.toPath())) {
			return new CharsetDetector().detectCharset(inputStream);
		}
	}

	public static CommandConfiguration loadConfiguration(Path configurationPath) throws IOException {
		try {
			final JAXBContext context = JAXBContext.newInstance(XmlCommandConfiguration.class);
			final Unmarshaller marshaller = context.createUnmarshaller();
			try (InputStream inputStream = Files.newInputStream(configurationPath)) {
				final CommandConfiguration configuration = (CommandConfiguration) marshaller.unmarshal(inputStream);
				if (configuration.getBinary() == null) {
					throw new IOException("missing binary");
				}
				return configuration;
			}
		} catch (JAXBException e) {
			throw new IOException(e.getMessage(), e);
		}
	}

	public static void copyAttributes(Path source, Path destination) throws IOException {
		final PosixFileAttributes inputAttributes = Files.readAttributes(source, PosixFileAttributes.class);
		final PosixFileAttributeView outputView = Files.getFileAttributeView(destination, PosixFileAttributeView.class);
		outputView.setGroup(inputAttributes.group());
		outputView.setOwner(inputAttributes.owner());
		outputView.setPermissions(inputAttributes.permissions());
	}
}
