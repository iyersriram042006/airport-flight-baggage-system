package com.airport.proxy;

// Proxy Pattern - Subject interface shared by the real system and the proxy.
public interface AirportSystem {
    void performProtectedOperation(String operationName, Role callerRole);
}
