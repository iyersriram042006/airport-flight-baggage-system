package model;

// Simple data holder representing a batch of baggage tied to a flight.
public class Baggage {

    private final String baggageId;
    private final String flightId;
    private BaggageStatus status;

    public Baggage(String baggageId, String flightId) {
        this.baggageId = baggageId;
        this.flightId = flightId;
        this.status = BaggageStatus.PENDING;
    }

    public String getBaggageId() {
        return baggageId;
    }

    public String getFlightId() {
        return flightId;
    }

    public BaggageStatus getStatus() {
        return status;
    }

    public void setStatus(BaggageStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format("Baggage[%s] for Flight[%s] - Status: %s", baggageId, flightId, status);
    }
}
