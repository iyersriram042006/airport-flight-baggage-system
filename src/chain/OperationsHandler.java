package chain;

import model.Flight;

// Chain of Responsibility Pattern - Concrete Handler
// Checks that the flight has a valid schedule (in this simple demo,
// simply that it was created and has a flight number).
public class OperationsHandler extends ClearanceHandler {
    @Override
    protected boolean check(Flight flight) {
        return flight.getFlightNumber() != null && !flight.getFlightNumber().isEmpty();
    }
}
