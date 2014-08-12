package org.media.web.merge.listener;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.media.container.merge.MergeDefinition;
import org.media.container.merge.MergeFactory;
import org.media.container.merge.execution.Merge;
import org.media.container.merge.execution.MergeOperation;
import org.media.container.merge.execution.MergeStatus;

import java.nio.file.Files;
import java.nio.file.Path;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ContainerRenameTest {

	private Path input;
	private Path output;

	@Before
	public void setUp() throws Exception {
		input = Files.createTempFile(this.getClass().getSimpleName() + "Input", null);
		Files.write(input, "InputContent".getBytes());
		output = Files.createTempFile(this.getClass().getSimpleName() + "Output", null);
		Files.write(output, "OutputContent".getBytes());
	}

	@SuppressWarnings("ResultOfMethodCallIgnored")
	@After
	public void tearDown() throws Exception {
		Files.deleteIfExists(input);
		Files.deleteIfExists(output);
	}

	@Test
	public void shouldNotDoAnythingIfError() throws Exception {
		new ContainerRename().onChange(MergeOperation.UPDATE, merge(MergeStatus.ERROR));
		assertEquals("InputContent", new String(Files.readAllBytes(input)));
		assertEquals("OutputContent", new String(Files.readAllBytes(output)));
	}

	@Test
	public void shouldNotDoAnythingIfRunning() throws Exception {
		new ContainerRename().onChange(MergeOperation.UPDATE, merge(MergeStatus.RUNNING));
		assertEquals("InputContent", new String(Files.readAllBytes(input)));
		assertEquals("OutputContent", new String(Files.readAllBytes(output)));
	}

	@Test
	public void shouldNotDoAnythingIfPending() throws Exception {
		new ContainerRename().onChange(MergeOperation.CREATE, merge(MergeStatus.PENDING));
		assertEquals("InputContent", new String(Files.readAllBytes(input)));
		assertEquals("OutputContent", new String(Files.readAllBytes(output)));
	}

	@Test
	public void shouldMoveFile() throws Exception {
		new ContainerRename().onChange(MergeOperation.UPDATE, merge(MergeStatus.COMPLETED));
		assertFalse(Files.exists(output));
		assertEquals("OutputContent", new String(Files.readAllBytes(input)));
	}

	private Merge merge(MergeStatus status) {
		final MergeDefinition definition = MergeFactory.merge(input.toFile(), output.toFile());
		final Merge merge = mock(Merge.class);
		when(merge.getStatus()).thenReturn(status);
		when(merge.getDefinition()).thenReturn(definition);
		return merge;
	}
}