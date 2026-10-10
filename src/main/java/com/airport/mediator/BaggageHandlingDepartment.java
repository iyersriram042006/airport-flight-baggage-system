package com.airport.mediator;

import com.airport.model.Flight;
import com.airport.model.FlightStatus;
import com.airport.model.SlotCalculator;
import com.airport.strategy.BeltAssigner;

// Mediator - Colleague: when told a flight landed, assigns a belt (Strategy) and sets the
// expected baggage slot, then reports back. It has no reference to ATC or Clearance.
public class BaggageHandlingDepartment extends Department {

    private final BeltAssigner beltAssigner;

    public BaggageHandlingDepartment(BeltAssigner beltAssigner) {
        super("Baggage Handling");
        this.beltAssigner = beltAssigner;
    }

    public void onFlightLanded(Flight flight) {
        try {
            beltAssigner.assign(flight, "NORMAL");
        } catch (IllegalStateException noBelt) {
            mediator.notify(this, "BAGGAGE_BELT_UNAVAILABLE", flight);
            return;
        }
        flight.setBaggageArrivalSlot(SlotCalculator.computeSlot(flight.getLandingTime()));
        flight.setStatus(FlightStatus.BAGGAGE_PROCESSING);
        mediator.notify(this, "BAGGAGE_READY", flight);
    }
}
