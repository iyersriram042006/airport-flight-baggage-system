package com.airport.mediator;

import com.airport.model.Flight;
import com.airport.model.FlightStatus;
import com.airport.observer.NotificationLog;
import com.airport.strategy.BeltAssigner;
import org.springframework.stereotype.Component;

// Mediator Pattern - Concrete Mediator.
// Event flow:  Clearance -> (CLEARANCE_APPROVED) -> flight CLEARED
//              ATC lands  -> (FLIGHT_LANDED)     -> Baggage Handling prepares belt + slot
//                         -> (BAGGAGE_READY)     -> Control Center logs it (end of chain: no loops)
@Component
public class AirportControlMediator implements AirportMediator {

    private final ClearanceDepartment clearance = new ClearanceDepartment();
    private final ATCDepartment atc = new ATCDepartment();
    private final BaggageHandlingDepartment baggage;
    private final ControlCenterDepartment controlCenter;

    public AirportControlMediator(NotificationLog log, BeltAssigner beltAssigner) {
        this.baggage = new BaggageHandlingDepartment(beltAssigner);
        this.controlCenter = new ControlCenterDepartment(log);
        clearance.setMediator(this);
        atc.setMediator(this);
        baggage.setMediator(this);
        controlCenter.setMediator(this);
    }

    @Override
    public ClearanceResult requestClearance(Flight flight) {
        return clearance.process(flight);
    }

    @Override
    public void requestLanding(Flight flight) {
        atc.land(flight);
    }

    @Override
    public void notify(Department sender, String event, Flight flight) {
        String n = flight.getFlightNumber();
        switch (event) {
            case "CLEARANCE_APPROVED" -> {
                flight.setStatus(FlightStatus.CLEARED);
                controlCenter.record(sender.getName() + " approved " + n + "; flight may now land.");
            }
            case "CLEARANCE_REJECTED" -> {
                flight.setStatus(FlightStatus.REJECTED);
                controlCenter.record(sender.getName() + " rejected " + n + ".");
            }
            case "FLIGHT_LANDED" -> {
                controlCenter.record(sender.getName() + " confirmed landing of " + n + "; informing Baggage Handling.");
                baggage.onFlightLanded(flight);
            }
            case "BAGGAGE_READY" -> controlCenter.record(sender.getName() + " assigned belt "
                    + flight.getBaggageBelt() + ", expected slot " + flight.getBaggageArrivalSlot() + " for " + n + ".");
            case "BAGGAGE_BELT_UNAVAILABLE" -> controlCenter.record("No free belt for " + n + "; staff must assign one manually.");
            default -> throw new IllegalArgumentException("Unknown mediator event: " + event);
        }
    }
}
