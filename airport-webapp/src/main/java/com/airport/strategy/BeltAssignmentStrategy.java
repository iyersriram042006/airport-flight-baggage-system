package com.airport.strategy;

import com.airport.model.Flight;

import java.util.List;
import java.util.Map;
import java.util.Optional;

// Strategy Pattern - the algorithm interface for choosing a baggage belt.
// Belts B1 and B2 are the "priority belts" (closest to the arrivals hall) - an assumption
// documented in the README.
public interface BeltAssignmentStrategy {

    List<String> PRIORITY_BELTS = List.of("B1", "B2");

    String getName();

    // occupancy: belt -> flightId (null = free). Returns a FREE belt, or empty if none.
    Optional<String> selectBelt(Map<String, String> occupancy, Flight flight);
}
