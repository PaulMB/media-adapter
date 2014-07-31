package org.media.container.info;

import org.media.container.exception.MediaReadException;

import java.net.URI;

public interface ContainerFactory {

	Container create(URI containerURI) throws MediaReadException;

}
