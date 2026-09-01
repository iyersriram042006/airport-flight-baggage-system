package factory;

import model.Flight;

// Factory Method Pattern
// Abstract Creator. The client asks a factory for a Flight without ever
// naming the concrete class (DomesticFlight, InternationalFlight, ...).
// Adding a new flight type later only means adding a new factory subclass.
public abstract class FlightFactory {

    public abstract Flight createFlight(String flightId, String flightNumber);

    // Simple static helper so the console menu can pick a factory by name
    // without the caller needing to know the concrete factory classes.
    public static FlightFactory getFactory(String type) {
        switch (type.trim().toLowerCase()) {
            case "domestic":
                return new DomesticFlightFactory();
            case "international":
                return new InternationalFlightFactory();
            case "cargo":
                return new CargoFlightFactory();
            default:
                throw new IllegalArgumentException("Unknown flight type: " + type);
        }
    }
}
