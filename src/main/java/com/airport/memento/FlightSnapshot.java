package com.airport.memento;

// Memento Pattern - the "narrow" memento interface.
// The caretaker (FlightHistory) can only hold and hand back a FlightSnapshot;
// the saved fields are private inside Flight, so nobody else can read or edit them.
public interface FlightSnapshot {
}
