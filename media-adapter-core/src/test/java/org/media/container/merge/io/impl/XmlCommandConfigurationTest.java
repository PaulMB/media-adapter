package org.media.container.merge.io.impl;

import org.junit.Test;
import org.media.container.merge.io.CommandConfiguration;
import org.media.container.merge.io.IOFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

public class XmlCommandConfigurationTest {

	@Test
	public void shouldLoadConfig() throws Exception {
		final Path source = Paths.get(XmlCommandConfigurationTest.class.getResource("/org/media/container/merge/io/impl/xml/config.xml").toURI());
		final CommandConfiguration config = IOFactory.loadConfiguration(source);
		assertEquals("/bin/sh", config.getBinary().toString());
		assertEquals("/tmp", config.getWorkDirectory().toString());
		final Map<String, String> environment = new HashMap<>();
		environment.put("LANG", "C");
		environment.put("LC_ALL", "C");
		assertEquals(environment, config.getEnvironment());
	}

	@Test
	public void shouldLoadPartialConfig() throws Exception {
		final Path source = Paths.get(XmlCommandConfigurationTest.class.getResource("/org/media/container/merge/io/impl/xml/partial.config.xml").toURI());
		final CommandConfiguration config = IOFactory.loadConfiguration(source);
		assertEquals("/bin/sh", config.getBinary().toString());
		assertEquals("/tmp", config.getWorkDirectory().toString());
	}

	@Test(expected = IOException.class)
	public void shouldFailIfFileNotFound() throws Exception {
		IOFactory.loadConfiguration(Paths.get("/not_found"));
	}

	@Test(expected = IOException.class)
	public void shouldFailIfMissingBinary() throws Exception {
		IOFactory.loadConfiguration(Paths.get(XmlCommandConfigurationTest.class.getResource("/org/media/container/merge/io/impl/xml/invalid.config.xml").toURI()));
	}
}
