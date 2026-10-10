package com.airport.dto;

// Body of POST /api/flights/{id}/clone. flightNumber is required; the rest override the copy.
public class CloneRequest {
    public String flightNumber;
    public String source;
    public String destination;
    public String landingTime;
    public String gate;
}
