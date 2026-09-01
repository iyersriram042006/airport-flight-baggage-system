package factory;

import model.Flight;
import model.InternationalFlight;

// Factory Method Pattern - Concrete Creator
public class InternationalFlightFactory extends FlightFactory {
    @Override
    public Flight createFlight(String flightId, String flightNumber) {
        return new InternationalFlight(flightId, flightNumber);
    }
}
