package org.media.container.merge.execution.impl.command;

import org.apache.commons.exec.CommandLine;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.media.container.merge.MergeDefinition;
import org.media.container.merge.MergeFactory;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class CommandExecutorTest {

	//==================================================================================================================
	// Attributes
	//==================================================================================================================

	private Path input;
	private Path output;

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	@Before
	public void setUp() throws Exception {
		input = Files.createTempFile("command_executor_source_file", ".mkv");
		output = Paths.get(System.getProperty("java.io.tmpdir"), "command_executor_destination_file" + System.currentTimeMillis() + ".mkv");
	}

	@After
	public void tearDown() throws Exception {
		Files.deleteIfExists(input);
		Files.deleteIfExists(output);
	}

	@Test(expected = IOException.class)
	public void shouldFailIfInvalidCommand() throws Exception {
		final MergeDefinition definition = MergeFactory.merge(input.toFile(), output.toFile());
		final CommandLineBuilder builder = Mockito.mock(CommandLineBuilder.class);
		Mockito.when(builder.getCommandLine(Mockito.any(MergeDefinition.class))).thenReturn(new CommandLine("/unknown_command"));
		new CommandExecutor(definition, new HashMap<String, String>(), builder).execute();
	}

	@Test
	public void shouldDoNothingOnDispose() throws Exception {
		final MergeDefinition definition = MergeFactory.merge(input.toFile(), output.toFile());
		final CommandLineBuilder builder = Mockito.mock(CommandLineBuilder.class);
		new CommandExecutor(definition, new HashMap<String, String>(), builder).dispose();
	}
}