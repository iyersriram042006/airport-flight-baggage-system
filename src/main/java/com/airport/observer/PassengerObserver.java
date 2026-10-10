package com.airport.observer;

import com.airport.model.Flight;

// Observer Pattern - Concrete Observer
public class PassengerObserver implements FlightObserver {

    private final NotificationLog log;

    public PassengerObserver(NotificationLog log) {
        this.log = log;
    }

    @Override
    public void update(Flight flight) {
        log.add("Passenger notified: Flight " + flight.getFlightNumber() + " status is now " + flight.getStatus());
    }
}
