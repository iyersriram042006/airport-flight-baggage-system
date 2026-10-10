package com.airport.proxy;

import org.springframework.stereotype.Component;

// Proxy Pattern - Real Subject. It does not know about access control.
@Component
public class AirportSystemImpl implements AirportSystem {
    @Override
    public void performProtectedOperation(String operationName, Role callerRole) {
        // The real work happens in AirportService once the proxy has allowed the call.
    }
}
