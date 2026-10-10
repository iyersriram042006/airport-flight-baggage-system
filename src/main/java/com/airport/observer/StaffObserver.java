package com.airport.observer;

import com.airport.model.Flight;

// Observer Pattern - Concrete Observer
public class StaffObserver implements FlightObserver {

    private final NotificationLog log;

    public StaffObserver(NotificationLog log) {
        this.log = log;
    }

    @Override
    public void update(Flight flight) {
        log.add("Staff alert: Flight " + flight.getFlightNumber() + " changed to " + flight.getStatus());
    }
}
