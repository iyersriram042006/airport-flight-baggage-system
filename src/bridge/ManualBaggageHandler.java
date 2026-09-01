package bridge;

// Bridge Pattern - Concrete Implementor
public class ManualBaggageHandler implements BaggageHandlingImplementor {
    @Override
    public void handle(String baggageId) {
        System.out.println("  [Manual] Ground staff are manually moving baggage " + baggageId);
    }
}
