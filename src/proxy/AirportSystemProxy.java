package proxy;

// Proxy Pattern
// The Proxy stands in front of the real AirportSystem and checks the
// current user's role before forwarding any request. Protected
// operations (creating flights, clearing landings, updating baggage)
// are only reachable through this proxy - never directly.
public class AirportSystemProxy implements AirportSystem {

    private final AirportSystemImpl realSystem = new AirportSystemImpl();
    private User currentUser;

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    @Override
    public void performProtectedOperation(String operationName) {
        if (!hasAccess()) {
            System.out.println("  [Proxy] ACCESS DENIED. Staff or Admin login required for: " + operationName);
            return;
        }
        realSystem.performProtectedOperation(operationName);
    }

    public boolean hasAccess() {
        return currentUser != null &&
                (currentUser.getRole() == Role.STAFF || currentUser.getRole() == Role.ADMIN);
    }
}
