package observer;

import model.Flight;

// Observer Pattern
// Contract for any object that wants to be notified when a Flight's
// status changes (passenger, display board, staff, etc.)
public interface FlightObserver {
    void update(Flight flight);
}
