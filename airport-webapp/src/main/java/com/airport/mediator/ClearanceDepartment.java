package com.airport.mediator;

import com.airport.chain.ClearanceHandler;
import com.airport.model.Flight;

import java.util.List;

// Mediator - Colleague: runs the Chain of Responsibility, then reports the outcome to the mediator.
public class ClearanceDepartment extends Department {

    public ClearanceDepartment() { super("Clearance Department"); }

    public ClearanceResult process(Flight flight) {
        List<String> steps = ClearanceHandler.newReasonList();
        boolean approved = ClearanceHandler.buildStandardChain().handle(flight, steps);
        mediator.notify(this, approved ? "CLEARANCE_APPROVED" : "CLEARANCE_REJECTED", flight);
        return new ClearanceResult(approved, steps);
    }
}
