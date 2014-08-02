package org.media.container.merge.execution.impl.command;

import org.apache.commons.exec.CommandLine;
import org.media.container.merge.MergeDefinition;

import java.io.IOException;

public interface CommandLineBuilder {

	CommandLine getCommandLine(MergeDefinition definition) throws IOException;
}
