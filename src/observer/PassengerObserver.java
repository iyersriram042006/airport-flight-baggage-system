package observer;

import model.Flight;

// Observer Pattern - Concrete Observer
public class PassengerObserver implements FlightObserver {

    private final String passengerName;

    public PassengerObserver(String passengerName) {
        this.passengerName = passengerName;
    }

    @Override
    public void update(Flight flight) {
        System.out.println("  [Notify Passenger " + passengerName + "] Flight " +
                flight.getFlightNumber() + " status is now: " + flight.getStatus());
    }
}
