package abstractfactory;

// Abstract Factory Pattern
// Declares creation methods for a FAMILY of related resources (Gate +
// BaggageService) that belong together for a given flight category.
// This guarantees a Domestic flight never accidentally gets an
// International gate paired with a Domestic baggage service, etc.
public interface AirportResourceFactory {
    Gate createGate();
    BaggageService createBaggageService();
}
