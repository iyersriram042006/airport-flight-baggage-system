package com.airport.observer;

import com.airport.model.Flight;

// Observer Pattern - Concrete Observer
public class DisplayBoardObserver implements FlightObserver {

    private final NotificationLog log;

    public DisplayBoardObserver(NotificationLog log) {
        this.log = log;
    }

    @Override
    public void update(Flight flight) {
        log.add("Display Board updated: Flight " + flight.getFlightNumber() + " -> " + flight.getStatus());
    }
}
