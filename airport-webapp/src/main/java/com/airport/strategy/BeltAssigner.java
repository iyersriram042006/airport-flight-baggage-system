package com.airport.strategy;

import com.airport.model.Flight;
import com.airport.singleton.AirportControlCenter;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

// Strategy Pattern - the Context. It depends only on the BeltAssignmentStrategy interface.
// Adding a new strategy = add one @Component class; this workflow does not change.
@Component
public class BeltAssigner {

    private final Map<String, BeltAssignmentStrategy> strategies = new LinkedHashMap<>();

    public BeltAssigner(List<BeltAssignmentStrategy> available) {
        for (BeltAssignmentStrategy s : available) strategies.put(s.getName(), s);
    }

    public Set<String> getStrategyNames() { return strategies.keySet(); }

    public String assign(Flight flight, String strategyName) {
        BeltAssignmentStrategy strategy = strategyName == null ? null : strategies.get(strategyName.trim().toUpperCase());
        if (strategy == null) {
            throw new IllegalArgumentException("Unknown belt strategy '" + strategyName + "'. Available: " + strategies.keySet());
        }
        AirportControlCenter center = AirportControlCenter.getInstance();

        // The flight's own current belt counts as free, so it can be re-assigned.
        Map<String, String> view = new LinkedHashMap<>(center.getBelts());
        for (Map.Entry<String, String> e : view.entrySet()) {
            if (flight.getFlightId().equals(e.getValue())) e.setValue(null);
        }
        String belt = strategy.selectBelt(view, flight)
                .orElseThrow(() -> new IllegalStateException("No free baggage belt available"));
        center.assignBelt(belt, flight.getFlightId());
        return belt;
    }
}
