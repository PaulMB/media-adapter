package org.media.web.merge.listener;

import org.media.container.merge.MergeDefinition;
import org.media.container.merge.execution.Merge;
import org.media.container.merge.execution.MergeListener;
import org.media.container.merge.execution.MergeOperation;
import org.media.container.merge.execution.MergeStatus;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class ContainerRename implements MergeListener {

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	@SuppressWarnings("ResultOfMethodCallIgnored")
	@Override
	public void onChange(MergeOperation operation, Merge merge) {
		if ( operation == MergeOperation.UPDATE && merge.getStatus() == MergeStatus.COMPLETED ) {
			final MergeDefinition definition = merge.getDefinition();
			final Path input = definition.getInput().toPath();
			final Path output = definition.getOutput().toPath();
			try {
				Files.move(output, input, StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				e.printStackTrace();
				// TODO
			}
		}
	}
}
