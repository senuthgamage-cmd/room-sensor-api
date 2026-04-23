package com.example.roomsensor;

import java.io.IOException;
import java.net.URI;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;

public class Main {
    public static final String BASE_URI = "http://0.0.0.0:8080/";

    public static HttpServer startServer() {
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), new RoomSensorApplication());
    }

    public static void main(String[] args) throws IOException {
        HttpServer server = startServer();
        System.out.println("Server started at " + BASE_URI + "api/v1");
        System.out.println("Press Enter to stop...");
        System.in.read();
        server.shutdownNow();
    }
}
