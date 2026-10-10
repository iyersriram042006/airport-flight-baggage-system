package com.airport.singleton;

import com.airport.model.Baggage;
import com.airport.model.Flight;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

// Singleton Pattern
// The one shared source of truth: all flights, belts (and who occupies them) and baggage.
// Classic getInstance() singleton on purpose (not a Spring bean) so the pattern is visible.
public final class AirportControlCenter {

    private static AirportControlCenter instance;

    private final Map<String, Flight> flights = new LinkedHashMap<>();
    private final Map<String, Baggage> baggageRecords = new LinkedHashMap<>();
    private final Map<String, String> belts = new LinkedHashMap<>();   // belt -> flightId (null = free)

    private AirportControlCenter() {
        resetBelts();
    }

    public static synchronized AirportControlCenter getInstance() {
        if (instance == null) {
            instance = new AirportControlCenter();
        }
        return instance;
    }

    private void resetBelts() {
        belts.clear();
        for (String b : new String[]{"B1", "B2", "B3", "B4"}) belts.put(b, null);
    }

    // Test hook: clears state but keeps the same single instance.
    public synchronized void reset() {
        flights.clear();
        baggageRecords.clear();
        resetBelts();
    }

    // ---- flights ----
    public void addFlight(Flight flight) { flights.put(flight.getFlightId(), flight); }
    public Flight getFlight(String id) { return flights.get(id); }
    public Map<String, Flight> getAllFlights() { return flights; }

    // ---- belts (occupancy is tracked, so a belt can never be double-booked) ----
    public Map<String, String> getBelts() { return belts; }

    public boolean assignBelt(String belt, String flightId) {
        if (!belts.containsKey(belt)) return false;
        String owner = belts.get(belt);
        if (owner != null && !owner.equals(flightId)) {
            throw new IllegalStateException("Belt " + belt + " is already assigned to flight " + owner);
        }
        releaseBelt(flightId);
        belts.put(belt, flightId);
        Flight f = flights.get(flightId);
        if (f != null) f.setBaggageBelt(belt);
        return true;
    }

    public void releaseBelt(String flightId) {
        for (Map.Entry<String, String> e : belts.entrySet()) {
            if (flightId.equals(e.getValue())) e.setValue(null);
        }
        Flight f = flights.get(flightId);
        if (f != null) f.setBaggageBelt(null);
    }

    // After a Memento restore: make belt occupancy match the restored flight.
    // Returns false if the restored belt has meanwhile been taken by another flight.
    public boolean syncBeltWithFlight(Flight f) {
        String desired = f.getBaggageBelt();
        for (Map.Entry<String, String> e : belts.entrySet()) {
            if (f.getFlightId().equals(e.getValue())) e.setValue(null);
        }
        if (desired == null) return true;
        String owner = belts.get(desired);
        if (owner == null) {
            belts.put(desired, f.getFlightId());
            return true;
        }
        f.setBaggageBelt(null);
        return false;
    }

    // ---- baggage ----
    public void addBaggage(Baggage b) { baggageRecords.put(b.getBaggageId(), b); }
    public Map<String, Baggage> getAllBaggage() { return baggageRecords; }

    public Set<String> getBaggageIdsForFlight(String flightId) {
        Set<String> ids = new TreeSet<>();
        for (Baggage b : baggageRecords.values()) {
            if (b.getFlightId().equals(flightId)) ids.add(b.getBaggageId());
        }
        return ids;
    }

    public void removeBaggageForFlightExcept(String flightId, Set<String> keep) {
        baggageRecords.values().removeIf(b -> b.getFlightId().equals(flightId) && !keep.contains(b.getBaggageId()));
    }
}
