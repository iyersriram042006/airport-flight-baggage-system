package com.airport.memento;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

// One history entry: the opaque snapshot + a label + which baggage records existed at that time.
// Immutable, so the caretaker cannot change what was saved.
public final class SavedState {
    private final FlightSnapshot snapshot;
    private final String label;
    private final String savedAt;
    private final Set<String> baggageIds;

    public SavedState(FlightSnapshot snapshot, String label, Set<String> baggageIds) {
        this.snapshot = snapshot;
        this.label = label;
        this.savedAt = LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss a"));
        this.baggageIds = Collections.unmodifiableSet(new TreeSet<>(baggageIds));
    }

    public FlightSnapshot getSnapshot() { return snapshot; }
    public String getLabel() { return label; }
    public String getSavedAt() { return savedAt; }
    public Set<String> getBaggageIds() { return baggageIds; }
}
