package org.media.container.merge.execution.impl.command;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;
import org.media.container.merge.MergeDefinition;
import org.media.container.merge.execution.MergeExecutor;
import org.media.container.merge.io.IOFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

public class CommandExecutor implements MergeExecutor {

	//==================================================================================================================
	// Attributes
	//==================================================================================================================

	private final ByteArrayOutputStream outputStream;
	private final DefaultExecutor executor;
	private final ExecuteWatchdog watchdog;
	private final MergeDefinition definition;
	private final CommandLineBuilder builder;
	private final Map<String, String> environment;

	//==================================================================================================================
	// Constructors
	//==================================================================================================================

	public CommandExecutor(MergeDefinition mergeDefinition, Map<String, String> commandEnv, CommandLineBuilder commandLineBuilder) {
		this.environment = commandEnv;
		definition = mergeDefinition;
		builder = commandLineBuilder;
		outputStream = new ByteArrayOutputStream();
		watchdog = new ExecuteWatchdog(ExecuteWatchdog.INFINITE_TIMEOUT);
		executor = new DefaultExecutor();
		executor.setExitValue(0);
		executor.setStreamHandler(new PumpStreamHandler(outputStream));
		executor.setWatchdog(watchdog);
	}

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	@Override
	public void execute() throws IOException {
		final CommandLine commandLine = builder.getCommandLine(definition);
		try {
			this.prepareWorkFiles();
			executor.execute(commandLine, environment);
		} catch (IOException e) {
			outputStream.write(e.getMessage().getBytes());
			Files.deleteIfExists(definition.getOutput().toPath());
			throw e;
		}
	}

	@Override
	public void cancel() throws IOException {
		watchdog.destroyProcess();
		Files.deleteIfExists(definition.getOutput().toPath());
	}

	@Override
	public void dispose() {
		// Nothing
	}

	@Override
	public String getMessage() {
		return outputStream.toString();
	}

	//==================================================================================================================
	// Private methods
	//==================================================================================================================

	private void prepareWorkFiles() throws IOException {
		Files.createFile(definition.getOutput().toPath());
		IOFactory.copyAttributes(definition.getInput().toPath(), definition.getOutput().toPath());
	}
}
