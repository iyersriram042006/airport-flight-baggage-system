package com.airport.abstractfactory;

// Abstract Factory - Concrete Factory
public class InternationalAirportFactory implements AirportResourceFactory {
    @Override
    public Gate createGate() { return new InternationalGate(); }

    @Override
    public BaggageService createBaggageService() { return new InternationalBaggageService(); }
}
