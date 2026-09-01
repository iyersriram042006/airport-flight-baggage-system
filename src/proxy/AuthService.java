package proxy;

import java.util.HashMap;
import java.util.Map;

// Basic hardcoded authentication used to demonstrate the Proxy pattern.
// Not meant to be secure - just enough to show role-based access control.
public class AuthService {

    private final Map<String, User> users = new HashMap<>();

    public AuthService() {
        users.put("passenger1", new User("passenger1", "pass123", Role.PASSENGER));
        users.put("staff1", new User("staff1", "staff123", Role.STAFF));
        users.put("admin1", new User("admin1", "admin123", Role.ADMIN));
    }

    public User authenticate(String username, String password) {
        User user = users.get(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }
}
