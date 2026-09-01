package chain;

import model.Flight;

// Chain of Responsibility Pattern
// Abstract Handler. Each concrete handler performs ONE simple check and,
// if it passes, forwards the request to the next handler in the chain.
// This avoids one giant if/else block for flight clearance and makes it
// easy to add, remove or reorder checks.
public abstract class ClearanceHandler {

    protected ClearanceHandler next;

    public ClearanceHandler setNext(ClearanceHandler next) {
        this.next = next;
        return next;
    }

    public boolean handle(Flight flight) {
        if (!check(flight)) {
            System.out.println("  -> Clearance REJECTED at: " + getClass().getSimpleName());
            return false;
        }
        System.out.println("  -> " + getClass().getSimpleName() + " approved.");
        if (next != null) {
            return next.handle(flight);
        }
        return true;
    }

    protected abstract boolean check(Flight flight);
}
