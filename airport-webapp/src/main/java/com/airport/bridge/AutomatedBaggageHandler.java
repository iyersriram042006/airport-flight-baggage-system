package com.airport.bridge;

// Bridge Pattern - Concrete Implementor
public class AutomatedBaggageHandler implements BaggageHandlingImplementor {
    @Override
    public String getName() { return "Automated"; }

    @Override
    public String handle(String baggageId) { return "Conveyor system auto-routes baggage " + baggageId; }
}
