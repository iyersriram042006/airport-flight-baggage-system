package model;

// Factory Method Pattern
// Concrete Product: one of the flight types the FlightFactory can create.
public class DomesticFlight extends Flight {

    public DomesticFlight(String flightId, String flightNumber) {
        super(flightId, flightNumber);
    }

    @Override
    public String getCategory() {
        return "Domestic";
    }
}
