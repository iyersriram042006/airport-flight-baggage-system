package com.airport.mediator;

import com.airport.model.Flight;
import com.airport.model.FlightStatus;

// Mediator - Colleague: ATC records the landing and tells the mediator.
public class ATCDepartment extends Department {

    public ATCDepartment() { super("ATC"); }

    public void land(Flight flight) {
        flight.setStatus(FlightStatus.LANDED);
        mediator.notify(this, "FLIGHT_LANDED", flight);
    }
}
