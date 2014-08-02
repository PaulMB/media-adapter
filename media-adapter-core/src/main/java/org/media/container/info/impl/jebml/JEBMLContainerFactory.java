package org.media.container.info.impl.jebml;

import org.ebml.io.InputStreamDataSource;
import org.ebml.matroska.MatroskaFile;
import org.media.container.exception.MediaReadException;
import org.media.container.info.Container;
import org.media.container.info.ContainerFactory;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JEBMLContainerFactory implements ContainerFactory {

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	@Override
	public Container create(URI containerURI) throws MediaReadException {
		try {
			final MatroskaFile matroskaFile = new MatroskaFile(new InputStreamDataSource(Files.newInputStream(Paths.get(containerURI))));
			matroskaFile.setScanFirstCluster(false);
			matroskaFile.readFile();
			return new JEBMLContainer(matroskaFile);
		} catch (Exception e) {
			throw new MediaReadException(e.getMessage(), e);
		}
	}
}
