package com.airport.bridge;

// Bridge Pattern - Implementor: HOW baggage is physically handled.
public interface BaggageHandlingImplementor {
    String getName();
    String handle(String baggageId);
}
