package bridge;

// Bridge Pattern - Concrete Implementor
public class AutomatedBaggageHandler implements BaggageHandlingImplementor {
    @Override
    public void handle(String baggageId) {
        System.out.println("  [Automated] Conveyor system is auto-routing baggage " + baggageId);
    }
}
