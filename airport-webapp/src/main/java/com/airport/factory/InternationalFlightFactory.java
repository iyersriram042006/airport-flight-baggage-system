package com.airport.factory;

import com.airport.model.InternationalFlight;
import com.airport.model.Flight;

// Factory Method Pattern - Concrete Creator
public class InternationalFlightFactory extends FlightFactory {
    @Override
    public Flight createFlight(String flightId, String flightNumber, String airline,
                               String source, String destination, String landingTime) {
        return new InternationalFlight(flightId, flightNumber, airline, source, destination, landingTime);
    }
}
