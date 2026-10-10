package com.airport.mediator;

import com.airport.model.Flight;

// Mediator Pattern - interface.
// Departments never call each other; they only talk to the mediator, which decides who
// must react next. The Chain of Responsibility still does the clearance checks themselves;
// the mediator coordinates what happens BETWEEN departments around them.
public interface AirportMediator {
    ClearanceResult requestClearance(Flight flight);
    void requestLanding(Flight flight);
    void notify(Department sender, String event, Flight flight);
}
