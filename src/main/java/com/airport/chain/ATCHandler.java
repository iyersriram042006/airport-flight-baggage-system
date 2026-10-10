package com.airport.chain;

import com.airport.model.Flight;
import com.airport.model.FlightStatus;

// Chain of Responsibility - ATC: runway free? (demo rule: flight must still be CREATED)
public class ATCHandler extends ClearanceHandler {
    @Override
    protected boolean check(Flight flight) {
        return flight.getStatus() == FlightStatus.CREATED;
    }
}
