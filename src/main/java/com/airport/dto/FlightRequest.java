package com.airport.dto;

// Body of POST /api/flights (fed into the FlightBuilder). Last three fields are optional.
public class FlightRequest {
    public String flightType;      // DOMESTIC / INTERNATIONAL / CARGO
    public String flightNumber;
    public String airline;
    public String source;
    public String destination;
    public String landingTime;     // "10:00 AM"
    public String gate;            // optional
    public String baggageBelt;     // optional
    public String expectedBaggageSlot; // optional
}
