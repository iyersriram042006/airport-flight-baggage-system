package proxy;

// Proxy Pattern - Real Subject
// The actual protected airport system. It has no idea that access to it
// is being controlled; it just does the work when asked.
public class AirportSystemImpl implements AirportSystem {
    @Override
    public void performProtectedOperation(String operationName) {
        System.out.println("  [AirportSystem] Executing protected operation: " + operationName);
    }
}
