package com.example.roomsensor.filter;

import java.io.IOException;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;

@Provider
public class RequestLoggingFilter implements ContainerRequestFilter {
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        System.out.printf("REQUEST %s %s%n", requestContext.getMethod(), requestContext.getUriInfo().getRequestUri());
    }
}
