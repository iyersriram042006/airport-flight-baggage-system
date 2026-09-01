package chain;

import model.Flight;
import model.FlightStatus;

// Chain of Responsibility Pattern - Concrete Handler
// Checks runway/landing availability (demo rule: flight must still be
// in CREATED state, i.e. not already cleared or landed).
public class ATCHandler extends ClearanceHandler {
    @Override
    protected boolean check(Flight flight) {
        return flight.getStatus() == FlightStatus.CREATED;
    }
}
