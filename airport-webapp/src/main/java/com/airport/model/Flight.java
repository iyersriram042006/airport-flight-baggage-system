package com.airport.model;

import com.airport.memento.FlightSnapshot;
import com.airport.observer.FlightObserver;
import com.airport.prototype.FlightPrototype;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

// One class, three patterns (each on a different responsibility):
//  - Observer  : Flight is the Subject; status changes notify registered observers.
//  - Prototype : cloneWith(...) copies the reusable configuration into a NEW flight.
//  - Memento   : Flight is the Originator; createSnapshot()/restore(...) save and restore state.
public abstract class Flight implements FlightPrototype {

    protected String flightId;
    protected String flightNumber;
    protected String airline;
    protected String source;
    protected String destination;
    protected String landingTime;      // e.g. "10:00 AM"
    protected String gate;

    protected FlightStatus status = FlightStatus.CREATED;
    protected String baggageBelt;
    protected String baggageArrivalSlot;

    @JsonIgnore
    private final List<FlightObserver> observers = new ArrayList<>();

    protected Flight(String flightId, String flightNumber, String airline,
                     String source, String destination, String landingTime) {
        this.flightId = flightId;
        this.flightNumber = flightNumber;
        this.airline = airline;
        this.source = source;
        this.destination = destination;
        this.landingTime = landingTime;
    }

    // ---------------- Observer (Subject) ----------------
    public void registerObserver(FlightObserver observer) { observers.add(observer); }

    public void removeObserver(FlightObserver observer) { observers.remove(observer); }

    private void notifyObservers() {
        for (FlightObserver o : observers) {
            o.update(this);
        }
    }

    public void setStatus(FlightStatus status) {
        this.status = status;
        notifyObservers();
    }

    // ---------------- Prototype ----------------
    // Copies ONLY reusable configuration (airline, route, landing time, gate).
    // Deliberately NOT copied: id, flight number (caller supplies new ones), status,
    // baggage belt, baggage slot, observers. All copied fields are immutable Strings,
    // so a shallow copy is already a safe deep copy here.
    @Override
    public Flight cloneWith(String newId, String newFlightNumber) {
        Flight copy = newBlank(newId, newFlightNumber);   // same concrete subtype
        copy.gate = this.gate;
        return copy;                                      // status CREATED, no belt, no slot
    }

    // Each subtype creates a blank instance of its own class (keeps the clone's type).
    protected abstract Flight newBlank(String newId, String newFlightNumber);

    // ---------------- Memento (Originator) ----------------
    public FlightSnapshot createSnapshot() {
        return new State(status, landingTime, gate, baggageBelt, baggageArrivalSlot);
    }

    public void restore(FlightSnapshot snapshot) {
        if (!(snapshot instanceof State)) {
            throw new IllegalArgumentException("Snapshot was not created by a Flight");
        }
        State s = (State) snapshot;
        this.landingTime = s.landingTime;
        this.gate = s.gate;
        this.baggageBelt = s.baggageBelt;
        this.baggageArrivalSlot = s.baggageArrivalSlot;
        setStatus(s.status);   // also notifies observers about the restored status
    }

    // Private on purpose: only Flight can read what a snapshot contains.
    private static final class State implements FlightSnapshot {
        private final FlightStatus status;
        private final String landingTime, gate, baggageBelt, baggageArrivalSlot;

        private State(FlightStatus status, String landingTime, String gate, String belt, String slot) {
            this.status = status;
            this.landingTime = landingTime;
            this.gate = gate;
            this.baggageBelt = belt;
            this.baggageArrivalSlot = slot;
        }
    }

    // Used by the Abstract Factory step and by the display.
    public abstract String getCategory();

    // ---------------- plain bean getters/setters (JSON) ----------------
    public String getFlightId() { return flightId; }
    public String getFlightNumber() { return flightNumber; }
    public String getAirline() { return airline; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    public String getLandingTime() { return landingTime; }
    public void setLandingTime(String landingTime) { this.landingTime = landingTime; }
    public String getGate() { return gate; }
    public void setGate(String gate) { this.gate = gate; }
    public FlightStatus getStatus() { return status; }
    public String getBaggageBelt() { return baggageBelt; }
    public void setBaggageBelt(String baggageBelt) { this.baggageBelt = baggageBelt; }
    public String getBaggageArrivalSlot() { return baggageArrivalSlot; }
    public void setBaggageArrivalSlot(String slot) { this.baggageArrivalSlot = slot; }
}
