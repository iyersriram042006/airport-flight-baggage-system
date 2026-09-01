package observer;

import model.Flight;

// Observer Pattern - Concrete Observer
public class DisplayBoardObserver implements FlightObserver {
    @Override
    public void update(Flight flight) {
        System.out.println("  [Airport Display Board] Updated: Flight " +
                flight.getFlightNumber() + " -> " + flight.getStatus());
    }
}
