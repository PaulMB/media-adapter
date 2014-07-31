package org.media.web.info;

import org.media.container.exception.MediaReadException;
import org.media.container.info.ContainerFactory;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.File;

@SuppressWarnings("UnusedDeclaration")
@Path("info")
public class ContainerInfo {

	//==================================================================================================================
	// Attributes
	//==================================================================================================================

	@Inject
	private ContainerFactory factory;

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{path:.+}")
	public ContainerDescription info(@PathParam("path") String path) throws MediaReadException {
		return new ContainerDescription(factory.create(new File('/' + path).toURI()));
	}
}
