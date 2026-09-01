package bridge;

// Bridge Pattern - Refined Abstraction
public class PriorityBaggageProcessor extends BaggageProcessor {

    public PriorityBaggageProcessor(BaggageHandlingImplementor implementor) {
        super(implementor);
    }

    @Override
    public void process(String baggageId) {
        System.out.println("  Processing PRIORITY baggage " + baggageId + " (expedited):");
        implementor.handle(baggageId);
    }
}
