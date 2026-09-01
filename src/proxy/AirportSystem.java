package proxy;

// Proxy Pattern
// Common interface implemented by both the RealSubject (AirportSystemImpl)
// and the Proxy (AirportSystemProxy), so the client can use either one
// interchangeably.
public interface AirportSystem {
    void performProtectedOperation(String operationName);
}
