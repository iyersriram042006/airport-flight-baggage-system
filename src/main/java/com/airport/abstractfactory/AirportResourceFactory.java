package com.airport.abstractfactory;

// Abstract Factory Pattern
// Creates a FAMILY of related resources (Gate + BaggageService) so a Domestic flight can
// never get an International gate with a Domestic baggage service.
public interface AirportResourceFactory {
    Gate createGate();
    BaggageService createBaggageService();
}
