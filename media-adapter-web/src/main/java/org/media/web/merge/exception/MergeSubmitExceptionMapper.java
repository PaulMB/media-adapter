package org.media.web.merge.exception;

import org.media.container.exception.MergeDefinitionException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class MergeSubmitExceptionMapper implements ExceptionMapper<MergeDefinitionException> {

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	@Override
	public Response toResponse(MergeDefinitionException e) {
		return Response.status(Response.Status.FORBIDDEN).entity(e.getMessage()).type(MediaType.TEXT_PLAIN).build();
	}
}
