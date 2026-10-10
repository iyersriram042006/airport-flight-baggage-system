package com.airport.abstractfactory;

public class DomesticBaggageService implements BaggageService {
    @Override
    public String describe() { return "Domestic Baggage Service ready."; }
}
