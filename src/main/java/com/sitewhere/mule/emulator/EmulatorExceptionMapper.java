package com.sitewhere.mule.emulator;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * Converts exceptions to an XML payload.
 * 
 * @author dadams
 */
public class EmulatorExceptionMapper implements ExceptionMapper<Exception> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.ws.rs.ext.ExceptionMapper#toResponse(java.lang.Throwable)
	 */
	public Response toResponse(Exception e) {
		CommandResponse response = new CommandResponse(CommandResult.Failure);
		response.setMessage(e.getMessage());
		return Response.ok(response, MediaType.APPLICATION_XML).build();
	}
}