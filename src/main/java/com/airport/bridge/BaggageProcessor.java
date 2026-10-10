package com.airport.bridge;

import com.airport.model.Baggage;
import com.airport.model.BaggageStatus;
import com.airport.model.Flight;
import com.airport.model.FlightStatus;

import java.util.ArrayList;
import java.util.List;

// Two patterns, two different jobs, one class hierarchy:
//  - Bridge          : this abstraction (WHAT kind of baggage) holds a BaggageHandlingImplementor
//                      (HOW it is handled) and delegates the physical handling to it.
//  - Template Method : process() is final and fixes the ORDER of the workflow. Subclasses only
//                      override the steps that really differ (inspect, determineHandling).
public abstract class BaggageProcessor {

    protected final BaggageHandlingImplementor implementor;   // the Bridge

    protected BaggageProcessor(BaggageHandlingImplementor implementor) {
        this.implementor = implementor;
    }

    // ----- the template method: fixed algorithm, subclasses cannot reorder it -----
    public final List<String> process(Baggage baggage, Flight flight) {
        List<String> steps = new ArrayList<>();
        validate(baggage, flight, steps);
        receive(baggage, steps);
        inspect(baggage, steps);               // varies
        determineHandling(baggage, steps);     // varies
        handle(baggage, steps);                // delegated through the Bridge
        confirmBelt(flight, steps);
        updateStatus(baggage, steps);
        notifyOutcome(baggage, flight, steps); // Observer fires here
        return steps;
    }

    // ----- common steps (same for every baggage type) -----
    protected void validate(Baggage baggage, Flight flight, List<String> steps) {
        if (baggage == null || flight == null || !baggage.getFlightId().equals(flight.getFlightId())) {
            throw new IllegalArgumentException("Baggage does not belong to this flight");
        }
        steps.add("1. Validate: baggage " + baggage.getBaggageId() + " belongs to flight " + flight.getFlightNumber());
    }

    protected void receive(Baggage baggage, List<String> steps) {
        baggage.setStatus(BaggageStatus.PROCESSING);
        steps.add("2. Receive: baggage " + baggage.getBaggageId() + " received (PROCESSING)");
    }

    protected void handle(Baggage baggage, List<String> steps) {
        steps.add("5. Handle [" + implementor.getName() + "]: " + implementor.handle(baggage.getBaggageId()));
    }

    protected void confirmBelt(Flight flight, List<String> steps) {
        if (flight.getBaggageBelt() == null) {
            throw new IllegalStateException("No baggage belt assigned to this flight");
        }
        steps.add("6. Confirm belt: " + flight.getBaggageBelt());
    }

    protected void updateStatus(Baggage baggage, List<String> steps) {
        baggage.setStatus(BaggageStatus.ARRIVING);
        baggage.setStatus(BaggageStatus.AVAILABLE);
        steps.add("7. Update status: PROCESSING -> ARRIVING -> AVAILABLE");
    }

    protected void notifyOutcome(Baggage baggage, Flight flight, List<String> steps) {
        flight.setStatus(FlightStatus.BAGGAGE_AVAILABLE);   // Observer pattern notifies subscribers
        steps.add("8. Notify: observers told that baggage is available for " + flight.getFlightNumber());
    }

    // ----- steps that differ per baggage type -----
    protected abstract void inspect(Baggage baggage, List<String> steps);

    protected abstract void determineHandling(Baggage baggage, List<String> steps);

    // ----- helpers to pick Bridge combinations -----
    public static BaggageProcessor forType(String baggageType, BaggageHandlingImplementor implementor) {
        return "PRIORITY".equalsIgnoreCase(baggageType)
                ? new PriorityBaggageProcessor(implementor)
                : new RegularBaggageProcessor(implementor);
    }

    public static BaggageHandlingImplementor implementorFor(String handlingMethod) {
        return "AUTOMATED".equalsIgnoreCase(handlingMethod)
                ? new AutomatedBaggageHandler()
                : new ManualBaggageHandler();
    }
}
