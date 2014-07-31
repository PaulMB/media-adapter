package org.media.container.merge.io;

import java.nio.file.Path;
import java.util.Map;

public interface CommandConfiguration {

	Path getBinary();

	Path getWorkDirectory();

	Map<String, String> getEnvironment();
}
