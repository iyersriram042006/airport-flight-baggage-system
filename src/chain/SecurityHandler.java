package chain;

import model.Flight;

// Chain of Responsibility Pattern - Concrete Handler
// Checks security clearance (demo rule: always passes, representing a
// completed security sweep, but kept as its own step in the chain).
public class SecurityHandler extends ClearanceHandler {
    @Override
    protected boolean check(Flight flight) {
        return true;
    }
}
