package org.media.web.info.exception;

import org.media.container.exception.MediaReadException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class MediaReadExceptionMapper implements ExceptionMapper<MediaReadException> {

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	@Override
	public Response toResponse(MediaReadException e) {
		return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).type(MediaType.TEXT_PLAIN).build();
	}
}
