package org.media.container.merge.io;

import org.media.container.config.Configuration;

import java.nio.file.Path;
import java.util.Map;

public interface CommandConfiguration extends Configuration {

	Path getBinary();

	Map<String, String> getEnvironment();
}
