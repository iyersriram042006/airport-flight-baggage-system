package bridge;

// Bridge Pattern - Implementor
// Defines the low-level "how" of handling baggage. Kept completely
// separate from the "what kind of baggage" abstraction so both sides
// can be extended independently.
public interface BaggageHandlingImplementor {
    void handle(String baggageId);
}
