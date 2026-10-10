package com.airport.chain;

import com.airport.model.Flight;

// Chain of Responsibility - Security: security clearance (demo rule: always passes)
public class SecurityHandler extends ClearanceHandler {
    @Override
    protected boolean check(Flight flight) {
        return true;
    }
}
