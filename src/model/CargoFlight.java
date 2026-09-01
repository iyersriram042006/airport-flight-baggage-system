package model;

// Factory Method Pattern
// Concrete Product: one of the flight types the FlightFactory can create.
public class CargoFlight extends Flight {

    public CargoFlight(String flightId, String flightNumber) {
        super(flightId, flightNumber);
    }

    @Override
    public String getCategory() {
        return "Cargo";
    }
}
