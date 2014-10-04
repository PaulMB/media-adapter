package org.media.web.config.exception;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ComponentNotFoundExceptionMapper implements ExceptionMapper<ComponentNotFoundException> {

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	@Override
	public Response toResponse(ComponentNotFoundException e) {
		return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).type(MediaType.TEXT_PLAIN).build();
	}
}
