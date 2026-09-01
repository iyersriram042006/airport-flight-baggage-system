package observer;

import model.Flight;

// Observer Pattern - Concrete Observer
public class StaffObserver implements FlightObserver {
    @Override
    public void update(Flight flight) {
        System.out.println("  [Airport Staff Alert] Flight " +
                flight.getFlightNumber() + " changed to " + flight.getStatus());
    }
}
