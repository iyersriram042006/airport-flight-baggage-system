package com.airport.model;

// Data holder for one baggage batch created by the baggage-processing workflow.
public class Baggage {
    private final String baggageId;
    private final String flightId;
    private final String baggageType;      // Regular / Priority
    private final String handlingMethod;   // Manual / Automated
    private BaggageStatus status = BaggageStatus.PENDING;

    public Baggage(String baggageId, String flightId, String baggageType, String handlingMethod) {
        this.baggageId = baggageId;
        this.flightId = flightId;
        this.baggageType = baggageType;
        this.handlingMethod = handlingMethod;
    }

    public String getBaggageId() { return baggageId; }
    public String getFlightId() { return flightId; }
    public String getBaggageType() { return baggageType; }
    public String getHandlingMethod() { return handlingMethod; }
    public BaggageStatus getStatus() { return status; }
    public void setStatus(BaggageStatus status) { this.status = status; }
}
