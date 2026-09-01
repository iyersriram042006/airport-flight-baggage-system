package bridge;

// Bridge Pattern - Abstraction
// Represents "what kind of baggage" (Regular, Priority). It holds a
// reference to a BaggageHandlingImplementor (Manual/Automated) and
// delegates the actual work to it. New baggage types or new handling
// methods can be added on either side without touching the other.
public abstract class BaggageProcessor {

    protected BaggageHandlingImplementor implementor;

    protected BaggageProcessor(BaggageHandlingImplementor implementor) {
        this.implementor = implementor;
    }

    public abstract void process(String baggageId);
}
