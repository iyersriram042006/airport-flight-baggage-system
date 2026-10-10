package com.airport.bridge;

// Bridge Pattern - Concrete Implementor
public class ManualBaggageHandler implements BaggageHandlingImplementor {
    @Override
    public String getName() { return "Manual"; }

    @Override
    public String handle(String baggageId) { return "Ground staff manually move baggage " + baggageId; }
}
