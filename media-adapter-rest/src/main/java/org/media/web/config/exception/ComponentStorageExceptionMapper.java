package org.media.web.config.exception;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ComponentStorageExceptionMapper implements ExceptionMapper<ComponentStorageException> {

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	@Override
	public Response toResponse(ComponentStorageException e) {
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).type(MediaType.TEXT_PLAIN).build();
	}
}
