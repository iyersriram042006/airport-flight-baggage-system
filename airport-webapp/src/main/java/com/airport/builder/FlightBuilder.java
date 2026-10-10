package com.airport.builder;

import com.airport.factory.FlightFactory;
import com.airport.model.Flight;
import com.airport.model.FlightStatus;
import com.airport.model.SlotCalculator;

// Builder Pattern
// A flight has several required and several optional properties. The builder collects them
// step by step, validates the combination in build(), and then delegates the choice of the
// concrete subtype to the Factory Method (FlightFactory). Builder = "how to assemble",
// Factory Method = "which class to instantiate".
public class FlightBuilder {

    private String flightId;
    private String flightType;
    private String flightNumber;
    private String airline;
    private String origin;
    private String destination;
    private String landingTime;
    private String gate;
    private String baggageBelt;
    private String expectedBaggageSlot;
    private FlightStatus initialStatus;

    public FlightBuilder setFlightId(String v) { this.flightId = v; return this; }
    public FlightBuilder setFlightType(String v) { this.flightType = v; return this; }
    public FlightBuilder setFlightNumber(String v) { this.flightNumber = v; return this; }
    public FlightBuilder setAirline(String v) { this.airline = v; return this; }
    public FlightBuilder setOrigin(String v) { this.origin = v; return this; }
    public FlightBuilder setDestination(String v) { this.destination = v; return this; }
    public FlightBuilder setLandingTime(String v) { this.landingTime = v; return this; }
    public FlightBuilder setGate(String v) { this.gate = v; return this; }
    public FlightBuilder setBaggageBelt(String v) { this.baggageBelt = v; return this; }
    public FlightBuilder setExpectedBaggageSlot(String v) { this.expectedBaggageSlot = v; return this; }
    public FlightBuilder setInitialStatus(FlightStatus v) { this.initialStatus = v; return this; }

    public Flight build() {
        require(flightId, "flight id");
        require(flightType, "flight type");
        require(flightNumber, "flight number");
        require(origin, "origin");
        require(destination, "destination");
        require(landingTime, "landing time");
        if (!SlotCalculator.isValidTime(landingTime)) {
            throw new IllegalArgumentException("Landing time must look like '10:00 AM'");
        }

        Flight flight = FlightFactory.getFactory(flightType).createFlight(
                flightId, flightNumber.trim(), blank(airline) ? "N/A" : airline.trim(),
                origin.trim(), destination.trim(), landingTime.trim());

        if (!blank(gate)) flight.setGate(gate.trim());
        if (!blank(baggageBelt)) flight.setBaggageBelt(baggageBelt.trim());
        if (!blank(expectedBaggageSlot)) flight.setBaggageArrivalSlot(expectedBaggageSlot.trim());
        if (initialStatus != null) flight.setStatus(initialStatus);
        return flight;
    }

    private static boolean blank(String s) { return s == null || s.isBlank(); }

    private static void require(String value, String label) {
        if (blank(value)) throw new IllegalArgumentException("Missing required flight property: " + label);
    }
}
