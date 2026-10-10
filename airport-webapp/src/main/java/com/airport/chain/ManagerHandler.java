package com.airport.chain;

import com.airport.model.Flight;

// Chain of Responsibility - Manager: final approval by the airport manager
public class ManagerHandler extends ClearanceHandler {
    @Override
    protected boolean check(Flight flight) {
        return true;
    }
}
