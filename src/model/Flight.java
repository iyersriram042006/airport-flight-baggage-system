package model;

import observer.FlightObserver;
import java.util.ArrayList;
import java.util.List;

// Observer Pattern
// Flight is the "Subject". It keeps a list of observers (Passenger,
// Display Board, Staff) and notifies them automatically whenever its
// status changes, instead of every part of the system polling for changes.
public abstract class Flight {

    protected String flightId;
    protected String flightNumber;
    protected FlightStatus status;

    protected String baggageBelt;
    protected String baggageArrivalSlot;

    private final List<FlightObserver> observers = new ArrayList<>();

    public Flight(String flightId, String flightNumber) {
        this.flightId = flightId;
        this.flightNumber = flightNumber;
        this.status = FlightStatus.CREATED;
    }

    public void registerObserver(FlightObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(FlightObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers() {
        for (FlightObserver o : observers) {
            o.update(this);
        }
    }

    public void setStatus(FlightStatus status) {
        this.status = status;
        notifyObservers();
    }

    public FlightStatus getStatus() {
        return status;
    }

    public String getFlightId() {
        return flightId;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public String getBaggageBelt() {
        return baggageBelt;
    }

    public void setBaggageBelt(String baggageBelt) {
        this.baggageBelt = baggageBelt;
    }

    public String getBaggageArrivalSlot() {
        return baggageArrivalSlot;
    }

    public void setBaggageArrivalSlot(String baggageArrivalSlot) {
        this.baggageArrivalSlot = baggageArrivalSlot;
    }

    // Each concrete flight type describes itself; used for display and
    // by the Abstract Factory to decide which resource family to build.
    public abstract String getCategory();

    @Override
    public String toString() {
        return String.format("[%s] %s (%s) - Status: %s | Belt: %s | Baggage Slot: %s",
                flightId, flightNumber, getCategory(), status,
                baggageBelt == null ? "-" : baggageBelt,
                baggageArrivalSlot == null ? "-" : baggageArrivalSlot);
    }
}
