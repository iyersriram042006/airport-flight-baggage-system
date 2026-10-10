package com.airport.chain;

import com.airport.model.Flight;
import com.airport.model.SlotCalculator;

// Chain of Responsibility - Airport Operations: is the schedule valid?
public class OperationsHandler extends ClearanceHandler {
    @Override
    protected boolean check(Flight flight) {
        return flight.getFlightNumber() != null && !flight.getFlightNumber().isBlank()
                && SlotCalculator.isValidTime(flight.getLandingTime());
    }
}
