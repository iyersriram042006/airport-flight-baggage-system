package singleton;

import model.Baggage;
import model.Flight;

import java.util.LinkedHashMap;
import java.util.Map;

// Singleton Pattern
// AirportControlCenter is the single, shared source of truth for the whole
// airport: it holds all flights, belts and baggage records. Every part of
// the system (proxy, chain, factories, main menu) reaches the SAME instance
// instead of creating separate, inconsistent copies of airport state.
public class AirportControlCenter {

    private static AirportControlCenter instance;

    private final Map<String, Flight> flights = new LinkedHashMap<>();
    private final Map<String, Baggage> baggageRecords = new LinkedHashMap<>();
    private final Map<String, String> belts = new LinkedHashMap<>(); // belt -> flightId

    // Private constructor prevents external instantiation.
    private AirportControlCenter() {
        // Pre-load a few baggage belts as sample data.
        belts.put("Belt-1", null);
        belts.put("Belt-2", null);
        belts.put("Belt-3", null);
    }

    public static synchronized AirportControlCenter getInstance() {
        if (instance == null) {
            instance = new AirportControlCenter();
        }
        return instance;
    }

    public void addFlight(Flight flight) {
        flights.put(flight.getFlightId(), flight);
    }

    public Flight getFlight(String flightId) {
        return flights.get(flightId);
    }

    public Map<String, Flight> getAllFlights() {
        return flights;
    }

    public boolean assignBelt(String belt, String flightId) {
        if (!belts.containsKey(belt)) {
            return false;
        }
        belts.put(belt, flightId);
        Flight flight = flights.get(flightId);
        if (flight != null) {
            flight.setBaggageBelt(belt);
        }
        return true;
    }

    public Map<String, String> getBelts() {
        return belts;
    }

    public void addBaggage(Baggage baggage) {
        baggageRecords.put(baggage.getBaggageId(), baggage);
    }

    public Baggage getBaggage(String baggageId) {
        return baggageRecords.get(baggageId);
    }

    public Map<String, Baggage> getAllBaggage() {
        return baggageRecords;
    }
}
