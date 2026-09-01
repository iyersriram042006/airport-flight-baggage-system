package chain;

import model.Flight;

// Chain of Responsibility Pattern - Concrete Handler
// Airport Manager gives the final sign-off after every earlier check
// in the chain has passed.
public class ManagerHandler extends ClearanceHandler {
    @Override
    protected boolean check(Flight flight) {
        return true;
    }
}
