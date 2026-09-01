package bridge;

// Bridge Pattern - Refined Abstraction
public class RegularBaggageProcessor extends BaggageProcessor {

    public RegularBaggageProcessor(BaggageHandlingImplementor implementor) {
        super(implementor);
    }

    @Override
    public void process(String baggageId) {
        System.out.println("  Processing REGULAR baggage " + baggageId + ":");
        implementor.handle(baggageId);
    }
}
