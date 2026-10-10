package com.airport.abstractfactory;

public class InternationalBaggageService implements BaggageService {
    @Override
    public String describe() { return "International Baggage Service ready."; }
}
