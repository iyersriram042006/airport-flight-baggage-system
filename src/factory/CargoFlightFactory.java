package factory;

import model.CargoFlight;
import model.Flight;

// Factory Method Pattern - Concrete Creator
public class CargoFlightFactory extends FlightFactory {
    @Override
    public Flight createFlight(String flightId, String flightNumber) {
        return new CargoFlight(flightId, flightNumber);
    }
}
