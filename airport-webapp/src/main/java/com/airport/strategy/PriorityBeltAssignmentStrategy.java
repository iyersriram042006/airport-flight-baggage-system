package com.airport.strategy;

import com.airport.model.Flight;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

// Strategy - PRIORITY: use the reserved priority belts first, then any free belt.
@Component
public class PriorityBeltAssignmentStrategy implements BeltAssignmentStrategy {

    @Override
    public String getName() { return "PRIORITY"; }

    @Override
    public Optional<String> selectBelt(Map<String, String> occupancy, Flight flight) {
        for (String belt : PRIORITY_BELTS) {
            if (occupancy.containsKey(belt) && occupancy.get(belt) == null) return Optional.of(belt);
        }
        for (Map.Entry<String, String> e : occupancy.entrySet()) {
            if (e.getValue() == null) return Optional.of(e.getKey());
        }
        return Optional.empty();
    }
}
