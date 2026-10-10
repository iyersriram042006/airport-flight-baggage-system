package com.airport.prototype;

import com.airport.model.Flight;

// Prototype Pattern
// Contract for objects that can produce a configured copy of themselves.
public interface FlightPrototype {
    Flight cloneWith(String newId, String newFlightNumber);
}
