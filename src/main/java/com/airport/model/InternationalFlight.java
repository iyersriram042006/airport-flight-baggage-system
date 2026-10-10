package com.airport.model;

// Factory Method Pattern - Concrete Product
public class InternationalFlight extends Flight {

    public InternationalFlight(String flightId, String flightNumber, String airline,
                      String source, String destination, String landingTime) {
        super(flightId, flightNumber, airline, source, destination, landingTime);
    }

    @Override
    protected Flight newBlank(String newId, String newFlightNumber) {
        return new InternationalFlight(newId, newFlightNumber, airline, source, destination, landingTime);
    }

    @Override
    public String getCategory() {
        return "International";
    }
}
