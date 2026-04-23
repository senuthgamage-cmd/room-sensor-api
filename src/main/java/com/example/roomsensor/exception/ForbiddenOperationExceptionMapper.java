package com.example.roomsensor.exception;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import com.example.roomsensor.dto.ErrorResponse;

@Provider
public class ForbiddenOperationExceptionMapper implements ExceptionMapper<ForbiddenOperationException> {
    @Override
    public Response toResponse(ForbiddenOperationException exception) {
        return Response.status(Response.Status.FORBIDDEN)
                .type(MediaType.APPLICATION_JSON)
                .entity(new ErrorResponse(403, exception.getMessage()))
                .build();
    }
}
