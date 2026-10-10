package com.airport.factory;

import com.airport.model.Flight;

// Factory Method Pattern - abstract Creator.
// The FlightBuilder asks a factory for the right Flight subtype; nobody calls
// "new DomesticFlight(...)" directly.
public abstract class FlightFactory {

    public abstract Flight createFlight(String flightId, String flightNumber, String airline,
                                        String source, String destination, String landingTime);

    public static FlightFactory getFactory(String flightType) {
        switch (flightType.trim().toUpperCase()) {
            case "DOMESTIC":      return new DomesticFlightFactory();
            case "INTERNATIONAL": return new InternationalFlightFactory();
            case "CARGO":         return new CargoFlightFactory();
            default: throw new IllegalArgumentException("Unknown flight type: " + flightType);
        }
    }
}
