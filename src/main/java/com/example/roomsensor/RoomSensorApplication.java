package com.example.roomsensor;

import org.glassfish.jersey.server.ResourceConfig;

import com.example.roomsensor.api.ApiRootResource;
import com.example.roomsensor.api.RoomResource;
import com.example.roomsensor.api.SensorResource;
import com.example.roomsensor.exception.ConflictExceptionMapper;
import com.example.roomsensor.exception.ForbiddenOperationExceptionMapper;
import com.example.roomsensor.exception.GlobalExceptionMapper;
import com.example.roomsensor.exception.JsonProcessingExceptionMapper;
import com.example.roomsensor.exception.UnprocessableEntityExceptionMapper;
import com.example.roomsensor.exception.WebApplicationExceptionMapper;
import com.example.roomsensor.filter.RequestLoggingFilter;
import com.example.roomsensor.filter.ResponseLoggingFilter;

public class RoomSensorApplication extends ResourceConfig {
    public RoomSensorApplication() {
        register(ApiRootResource.class);
        register(RoomResource.class);
        register(SensorResource.class);

        register(ConflictExceptionMapper.class);
        register(UnprocessableEntityExceptionMapper.class);
        register(ForbiddenOperationExceptionMapper.class);
        register(JsonProcessingExceptionMapper.class);
        register(WebApplicationExceptionMapper.class);
        register(GlobalExceptionMapper.class);

        register(RequestLoggingFilter.class);
        register(ResponseLoggingFilter.class);

        packages("org.glassfish.jersey.jackson");
    }
}
