package com.example.roomsensor.exception;

import com.example.roomsensor.dto.ErrorResponse;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {
    @Override
    public Response toResponse(WebApplicationException exception) {
        Response source = exception.getResponse();
        int status = source != null ? source.getStatus() : Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();

        String message = exception.getMessage();
        if (message == null || message.isBlank()) {
            Response.StatusType statusInfo = source != null ? source.getStatusInfo() : Response.Status.INTERNAL_SERVER_ERROR;
            message = statusInfo.getReasonPhrase();
        }

        return Response.status(status)
                .type(MediaType.APPLICATION_JSON)
                .entity(new ErrorResponse(status, message))
                .build();
    }
}
