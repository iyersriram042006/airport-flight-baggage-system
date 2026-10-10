package com.airport.observer;

import com.airport.model.Flight;

// Observer Pattern - contract for anything that wants flight status updates.
public interface FlightObserver {
    void update(Flight flight);
}
