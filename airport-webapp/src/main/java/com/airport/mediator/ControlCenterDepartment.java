package com.airport.mediator;

import com.airport.observer.NotificationLog;

// Mediator - Colleague: keeps the operations log of coordination decisions.
public class ControlCenterDepartment extends Department {

    private final NotificationLog log;

    public ControlCenterDepartment(NotificationLog log) {
        super("Control Center");
        this.log = log;
    }

    public void record(String message) {
        log.add("Control Center: " + message);
    }
}
