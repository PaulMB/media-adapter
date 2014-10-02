package org.media.container.merge.io;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class IOFactoryTest {

	private Path source;
	private Path destination;

	@Before
	public void setUp() throws Exception {
		source = Files.createTempFile("copy_attributes_source_" + System.currentTimeMillis(), ".tmp");
		destination = Files.createTempFile("copy_attributes_source_" + System.currentTimeMillis(), ".tmp");
	}

	@After
	public void tearDown() throws Exception {
		Files.deleteIfExists(source);
		Files.deleteIfExists(destination);
	}

	@Test(expected = IOException.class)
	public void shouldFailIfUnknownSource() throws Exception {
		IOFactory.copyAttributes(Paths.get("/dummyFile"), destination);
	}

	@Test(expected = IOException.class)
	public void shouldFailIfUnknownDestination() throws Exception {
		IOFactory.copyAttributes(source, Paths.get("/dummyFile"));
	}

	@Test
	public void shouldCopyAttributes() throws Exception {
		final List<PosixFilePermission> permissions = Arrays.asList(PosixFilePermission.OWNER_EXECUTE, PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE);
		Files.setPosixFilePermissions(destination, new HashSet<>(permissions));
		IOFactory.copyAttributes(source, destination);
		assertEquals(Files.getPosixFilePermissions(source), Files.getPosixFilePermissions(destination));
	}

	@Test
	public void shouldGetTempFileWithExtension() throws Exception {
		assertEquals(new File("/tmp/test.flappy.mkv"), IOFactory.appendNameSuffix(new File("/tmp/test.mkv"), "flappy"));
	}

	@Test
	public void shouldGetTempFileWithoutExtension() throws Exception {
		assertEquals(new File("/tmp/test.flappy"), IOFactory.appendNameSuffix(new File("/tmp/test"), "flappy"));
	}

	@Test
	public void shouldGetTempFileWithTrailingDot() throws Exception {
		assertEquals(new File("/tmp/test.flappy."), IOFactory.appendNameSuffix(new File("/tmp/test."), "flappy"));
	}

	@Test
	public void shouldGetTempFileWithBeginningDot() throws Exception {
		assertEquals(new File("/tmp/.flappy.mkv"), IOFactory.appendNameSuffix(new File("/tmp/.mkv"), "flappy"));
	}

	@Test
	public void shouldGetTempFileWithMultipleDots() throws Exception {
		assertEquals(new File("/tmp/some.test.flappy.mkv"), IOFactory.appendNameSuffix(new File("/tmp/some.test.mkv"), "flappy"));
	}

	@Test
	public void shouldGetTempFileWithBeginningAndMultipleDots() throws Exception {
		assertEquals(new File("/tmp/.test.flappy.mkv"), IOFactory.appendNameSuffix(new File("/tmp/.test.mkv"), "flappy"));
	}

	@Test
	public void shouldGetTempFileWhenDirContainsDots() throws Exception {
		assertEquals(new File("/tmp.dir/test.flappy.mkv"), IOFactory.appendNameSuffix(new File("/tmp.dir/test.mkv"), "flappy"));
	}

	@Test
	public void shouldGetTempFileWhenOnlyDirContainsDots() throws Exception {
		assertEquals(new File("/tmp.dir/mkv.flappy"), IOFactory.appendNameSuffix(new File("/tmp.dir/mkv"), "flappy"));
	}
}