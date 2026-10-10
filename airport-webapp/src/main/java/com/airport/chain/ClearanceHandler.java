package com.airport.chain;

import com.airport.model.Flight;

import java.util.ArrayList;
import java.util.List;

// Chain of Responsibility Pattern - abstract Handler.
// Each handler does ONE check and passes the request on; the first failure stops the chain.
public abstract class ClearanceHandler {

    protected ClearanceHandler next;

    public ClearanceHandler setNext(ClearanceHandler next) {
        this.next = next;
        return next;
    }

    public boolean handle(Flight flight, List<String> steps) {
        if (!check(flight)) {
            steps.add(getClass().getSimpleName() + ": REJECTED");
            return false;
        }
        steps.add(getClass().getSimpleName() + ": approved");
        return next == null || next.handle(flight, steps);
    }

    protected abstract boolean check(Flight flight);

    public static ClearanceHandler buildStandardChain() {
        ClearanceHandler operations = new OperationsHandler();
        operations.setNext(new ATCHandler()).setNext(new SecurityHandler()).setNext(new ManagerHandler());
        return operations;
    }

    public static List<String> newReasonList() {
        return new ArrayList<>();
    }
}
