package com.airport.factory;

import com.airport.model.DomesticFlight;
import com.airport.model.Flight;

// Factory Method Pattern - Concrete Creator
public class DomesticFlightFactory extends FlightFactory {
    @Override
    public Flight createFlight(String flightId, String flightNumber, String airline,
                               String source, String destination, String landingTime) {
        return new DomesticFlight(flightId, flightNumber, airline, source, destination, landingTime);
    }
}
