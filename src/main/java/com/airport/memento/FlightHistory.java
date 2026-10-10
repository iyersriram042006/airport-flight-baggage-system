package com.airport.memento;

import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Memento Pattern - Caretaker.
// Stores snapshots per flight (most recent first, max 5) and hands them back for undo.
// It never looks inside a FlightSnapshot. In-memory only: history is lost on restart.
@Component
public class FlightHistory {

    private static final int MAX_SNAPSHOTS = 5;
    private final Map<String, Deque<SavedState>> history = new HashMap<>();

    public synchronized void save(String flightId, SavedState state) {
        Deque<SavedState> stack = history.computeIfAbsent(flightId, k -> new ArrayDeque<>());
        stack.push(state);
        while (stack.size() > MAX_SNAPSHOTS) stack.removeLast();
    }

    public synchronized SavedState pop(String flightId) {
        Deque<SavedState> stack = history.get(flightId);
        return (stack == null || stack.isEmpty()) ? null : stack.pop();
    }

    public synchronized List<String> labels(String flightId) {
        List<String> out = new ArrayList<>();
        Deque<SavedState> stack = history.get(flightId);
        if (stack != null) {
            for (SavedState s : stack) out.add(s.getSavedAt() + " - " + s.getLabel());
        }
        return out;
    }
}
