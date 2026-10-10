package com.airport.proxy;

import org.springframework.stereotype.Component;

// Proxy Pattern - Proxy. Every STAFF/ADMIN-only operation (clearance, landing, belts, slots,
// baggage processing, clone, restore) goes through here first.
@Component
public class AirportSystemProxy implements AirportSystem {

    private final AirportSystemImpl realSystem;

    public AirportSystemProxy(AirportSystemImpl realSystem) {
        this.realSystem = realSystem;
    }

    @Override
    public void performProtectedOperation(String operationName, Role callerRole) {
        if (callerRole != Role.STAFF && callerRole != Role.ADMIN) {
            throw new AccessDeniedException("Access denied for '" + operationName + "'. STAFF or ADMIN role required.");
        }
        realSystem.performProtectedOperation(operationName, callerRole);
    }
}
