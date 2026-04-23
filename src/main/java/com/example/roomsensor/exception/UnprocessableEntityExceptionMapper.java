package com.example.roomsensor.exception;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import com.example.roomsensor.dto.ErrorResponse;

@Provider
public class UnprocessableEntityExceptionMapper implements ExceptionMapper<UnprocessableEntityException> {
    @Override
    public Response toResponse(UnprocessableEntityException exception) {
        return Response.status(422)
                .type(MediaType.APPLICATION_JSON)
                .entity(new ErrorResponse(422, exception.getMessage()))
                .build();
    }
}
