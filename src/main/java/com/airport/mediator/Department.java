package com.airport.mediator;

// Mediator Pattern - abstract Colleague: knows only the mediator, never other departments.
public abstract class Department {
    protected AirportMediator mediator;
    private final String name;

    protected Department(String name) { this.name = name; }

    public void setMediator(AirportMediator mediator) { this.mediator = mediator; }

    public String getName() { return name; }
}
