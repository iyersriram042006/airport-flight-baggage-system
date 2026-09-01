package factory;

import model.DomesticFlight;
import model.Flight;

// Factory Method Pattern - Concrete Creator
public class DomesticFlightFactory extends FlightFactory {
    @Override
    public Flight createFlight(String flightId, String flightNumber) {
        return new DomesticFlight(flightId, flightNumber);
    }
}
