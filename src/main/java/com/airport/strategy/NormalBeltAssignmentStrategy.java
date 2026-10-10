package com.airport.strategy;

import com.airport.model.Flight;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

// Strategy - NORMAL: use the regular belts first (keeps priority belts free),
// and only fall back to a priority belt when no regular belt is free.
@Component
public class NormalBeltAssignmentStrategy implements BeltAssignmentStrategy {

    @Override
    public String getName() { return "NORMAL"; }

    @Override
    public Optional<String> selectBelt(Map<String, String> occupancy, Flight flight) {
        for (Map.Entry<String, String> e : occupancy.entrySet()) {
            if (e.getValue() == null && !PRIORITY_BELTS.contains(e.getKey())) return Optional.of(e.getKey());
        }
        for (Map.Entry<String, String> e : occupancy.entrySet()) {
            if (e.getValue() == null) return Optional.of(e.getKey());
        }
        return Optional.empty();
    }
}
