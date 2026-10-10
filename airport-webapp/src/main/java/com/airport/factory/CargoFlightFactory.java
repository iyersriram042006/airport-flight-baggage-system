package com.airport.factory;

import com.airport.model.CargoFlight;
import com.airport.model.Flight;

// Factory Method Pattern - Concrete Creator
public class CargoFlightFactory extends FlightFactory {
    @Override
    public Flight createFlight(String flightId, String flightNumber, String airline,
                               String source, String destination, String landingTime) {
        return new CargoFlight(flightId, flightNumber, airline, source, destination, landingTime);
    }
}
