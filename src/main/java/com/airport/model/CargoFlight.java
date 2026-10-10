package com.airport.model;

// Factory Method Pattern - Concrete Product
public class CargoFlight extends Flight {

    public CargoFlight(String flightId, String flightNumber, String airline,
                      String source, String destination, String landingTime) {
        super(flightId, flightNumber, airline, source, destination, landingTime);
    }

    @Override
    protected Flight newBlank(String newId, String newFlightNumber) {
        return new CargoFlight(newId, newFlightNumber, airline, source, destination, landingTime);
    }

    @Override
    public String getCategory() {
        return "Cargo";
    }
}
