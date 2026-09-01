package main;

import abstractfactory.*;
import bridge.*;
import chain.*;
import factory.FlightFactory;
import model.Baggage;
import model.BaggageStatus;
import model.Flight;
import model.FlightStatus;
import observer.DisplayBoardObserver;
import observer.PassengerObserver;
import observer.StaffObserver;
import proxy.AirportSystemProxy;
import proxy.AuthService;
import proxy.User;
import singleton.AirportControlCenter;

import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    // Singleton Pattern: the ONE shared control center for the whole app.
    private static final AirportControlCenter controlCenter = AirportControlCenter.getInstance();

    // Proxy Pattern: all protected operations go through this proxy.
    private static final AirportSystemProxy systemProxy = new AirportSystemProxy();
    private static final AuthService authService = new AuthService();

    private static int flightCounter = 1;
    private static int baggageCounter = 1;

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   Welcome to the Airport Flight &");
        System.out.println("      Baggage Management System");
        System.out.println("========================================");
        System.out.println("Sample logins -> staff1/staff123 (STAFF), admin1/admin123 (ADMIN), passenger1/pass123 (PASSENGER)");

        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1": createFlight(); break;
                case "2": viewFlights(); break;
                case "3": authenticateUser(); break;
                case "4": clearFlight(); break;
                case "5": markFlightLanded(); break;
                case "6": assignBaggageBelt(); break;
                case "7": setBaggageArrivalSlot(); break;
                case "8": updateBaggageStatus(); break;
                case "9": viewDetails(); break;
                case "10": running = false; break;
                default: System.out.println("Invalid choice. Try again.");
            }
        }
        System.out.println("Thank you for using the Airport Flight & Baggage Management System.");
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("========================================");
        System.out.println("     AIRPORT FLIGHT & BAGGAGE SYSTEM");
        System.out.println("========================================");
        System.out.println("1. Create Flight");
        System.out.println("2. View Flights");
        System.out.println("3. Authenticate User");
        System.out.println("4. Clear Flight for Landing");
        System.out.println("5. Mark Flight as Landed");
        System.out.println("6. Assign Baggage Belt");
        System.out.println("7. Set Baggage Arrival Slot");
        System.out.println("8. Update Baggage Status");
        System.out.println("9. View Flight & Baggage Details");
        System.out.println("10. Exit");
        System.out.print("Enter choice: ");
    }

    // ---------- 1. Create Flight (Factory Method + Abstract Factory + Observer) ----------
    private static void createFlight() {
        System.out.print("Enter flight type (Domestic/International/Cargo): ");
        String type = scanner.nextLine().trim();
        System.out.print("Enter flight number (e.g. AI-202): ");
        String number = scanner.nextLine().trim();

        FlightFactory factory;
        try {
            factory = FlightFactory.getFactory(type);
        } catch (IllegalArgumentException e) {
            System.out.println("Unknown flight type. Use Domestic, International, or Cargo.");
            return;
        }

        String flightId = "FL" + (flightCounter++);
        // Factory Method Pattern in action: client never calls "new DomesticFlight(...)" directly.
        Flight flight = factory.createFlight(flightId, number);

        // Observer Pattern: attach observers so status changes are broadcast automatically.
        flight.registerObserver(new PassengerObserver("Guest"));
        flight.registerObserver(new DisplayBoardObserver());
        flight.registerObserver(new StaffObserver());

        controlCenter.addFlight(flight);
        System.out.println("Created: " + flight);

        // Abstract Factory Pattern: build the matching family of resources
        // (Gate + BaggageService) for this flight's category.
        AirportResourceFactory resourceFactory = flight.getCategory().equalsIgnoreCase("International")
                ? new InternationalAirportFactory()
                : new DomesticAirportFactory();
        Gate gate = resourceFactory.createGate();
        BaggageService baggageService = resourceFactory.createBaggageService();
        System.out.println("Provisioning matching airport resources:");
        gate.showGateInfo();
        baggageService.showServiceInfo();
    }

    // ---------- 2. View Flights ----------
    private static void viewFlights() {
        if (controlCenter.getAllFlights().isEmpty()) {
            System.out.println("No flights available yet.");
            return;
        }
        System.out.println("Available flights:");
        for (Flight f : controlCenter.getAllFlights().values()) {
            System.out.println("  " + f);
        }
    }

    // ---------- 3. Authenticate User (feeds the Proxy) ----------
    private static void authenticateUser() {
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        User user = authService.authenticate(username, password);
        if (user == null) {
            System.out.println("Authentication failed. Invalid username or password.");
            return;
        }
        systemProxy.setCurrentUser(user);
        System.out.println("Login successful. Welcome " + user.getUsername() + " (" + user.getRole() + ")");
    }

    // ---------- 4. Clear Flight for Landing (Proxy + Chain of Responsibility) ----------
    private static void clearFlight() {
        Flight flight = selectFlight();
        if (flight == null) return;

        // Proxy Pattern: only STAFF/ADMIN can trigger a clearance check.
        systemProxy.performProtectedOperation("Clear Flight " + flight.getFlightNumber());
        if (!systemProxy.hasAccess()) {
            return;
        }

        if (flight.getStatus() != FlightStatus.CREATED) {
            System.out.println("Flight is not in a clearable state (current status: " + flight.getStatus() + ").");
            return;
        }

        // Chain of Responsibility Pattern: build the approval chain.
        ClearanceHandler operations = new OperationsHandler();
        ClearanceHandler atc = new ATCHandler();
        ClearanceHandler security = new SecurityHandler();
        ClearanceHandler manager = new ManagerHandler();
        operations.setNext(atc).setNext(security).setNext(manager);

        System.out.println("Running clearance chain for " + flight.getFlightNumber() + ":");
        boolean approved = operations.handle(flight);

        if (approved) {
            flight.setStatus(FlightStatus.CLEARED);
            System.out.println("Flight " + flight.getFlightNumber() + " is CLEARED for landing.");
        } else {
            System.out.println("Flight " + flight.getFlightNumber() + " clearance was REJECTED.");
        }
    }

    // ---------- 5. Mark Flight as Landed (Proxy) ----------
    private static void markFlightLanded() {
        Flight flight = selectFlight();
        if (flight == null) return;

        systemProxy.performProtectedOperation("Mark Flight " + flight.getFlightNumber() + " as Landed");
        if (!systemProxy.hasAccess()) return;

        if (flight.getStatus() != FlightStatus.CLEARED) {
            System.out.println("Flight must be CLEARED before it can be marked as landed.");
            return;
        }
        flight.setStatus(FlightStatus.LANDED);
        System.out.println("Flight " + flight.getFlightNumber() + " has LANDED.");
    }

    // ---------- 6. Assign Baggage Belt (Proxy) ----------
    private static void assignBaggageBelt() {
        Flight flight = selectFlight();
        if (flight == null) return;

        systemProxy.performProtectedOperation("Assign Baggage Belt for " + flight.getFlightNumber());
        if (!systemProxy.hasAccess()) return;

        if (flight.getStatus() != FlightStatus.LANDED) {
            System.out.println("Flight must have LANDED before a belt can be assigned.");
            return;
        }

        System.out.println("Available belts: " + controlCenter.getBelts().keySet());
        System.out.print("Enter belt name to assign: ");
        String belt = scanner.nextLine().trim();
        boolean ok = controlCenter.assignBelt(belt, flight.getFlightId());
        if (ok) {
            System.out.println("Belt " + belt + " assigned to flight " + flight.getFlightNumber() + ".");
        } else {
            System.out.println("Invalid belt name.");
        }
    }

    // ---------- 7. Set Baggage Arrival Slot (Proxy) ----------
    private static void setBaggageArrivalSlot() {
        Flight flight = selectFlight();
        if (flight == null) return;

        systemProxy.performProtectedOperation("Set Baggage Arrival Slot for " + flight.getFlightNumber());
        if (!systemProxy.hasAccess()) return;

        if (flight.getBaggageBelt() == null) {
            System.out.println("Assign a baggage belt first.");
            return;
        }

        System.out.print("Enter expected baggage arrival slot (e.g. 10:15 AM - 10:30 AM): ");
        String slot = scanner.nextLine().trim();
        flight.setBaggageArrivalSlot(slot);
        flight.setStatus(FlightStatus.BAGGAGE_PROCESSING);
        System.out.println("Baggage arrival slot set. Baggage processing has started.");
    }

    // ---------- 8. Update Baggage Status (Bridge + Observer) ----------
    private static void updateBaggageStatus() {
        Flight flight = selectFlight();
        if (flight == null) return;

        systemProxy.performProtectedOperation("Update Baggage Status for " + flight.getFlightNumber());
        if (!systemProxy.hasAccess()) return;

        if (flight.getStatus() != FlightStatus.BAGGAGE_PROCESSING) {
            System.out.println("Baggage processing has not started for this flight yet.");
            return;
        }

        System.out.print("Baggage type (Regular/Priority): ");
        String baggageType = scanner.nextLine().trim();
        System.out.print("Handling method (Manual/Automated): ");
        String handlingMethod = scanner.nextLine().trim();

        // Bridge Pattern: pick the implementor (how baggage is handled)...
        BaggageHandlingImplementor implementor = handlingMethod.equalsIgnoreCase("Automated")
                ? new AutomatedBaggageHandler()
                : new ManualBaggageHandler();

        // ...and combine it with the abstraction (what kind of baggage).
        BaggageProcessor processor = baggageType.equalsIgnoreCase("Priority")
                ? new PriorityBaggageProcessor(implementor)
                : new RegularBaggageProcessor(implementor);

        String baggageId = "BG" + (baggageCounter++);
        Baggage baggage = new Baggage(baggageId, flight.getFlightId());
        controlCenter.addBaggage(baggage);

        baggage.setStatus(BaggageStatus.PROCESSING);
        processor.process(baggageId);

        baggage.setStatus(BaggageStatus.ARRIVING);
        System.out.println("Baggage " + baggageId + " is now ARRIVING.");

        baggage.setStatus(BaggageStatus.AVAILABLE);
        // Observer Pattern: flipping the flight's status notifies every
        // registered observer (passenger, display board, staff) automatically.
        flight.setStatus(FlightStatus.BAGGAGE_AVAILABLE);
        System.out.println("Baggage " + baggageId + " is now AVAILABLE.");
    }

    // ---------- 9. View Flight & Baggage Details ----------
    private static void viewDetails() {
        System.out.println("---- Flights ----");
        if (controlCenter.getAllFlights().isEmpty()) {
            System.out.println("  None yet.");
        } else {
            controlCenter.getAllFlights().values().forEach(f -> System.out.println("  " + f));
        }
        System.out.println("---- Baggage ----");
        if (controlCenter.getAllBaggage().isEmpty()) {
            System.out.println("  None yet.");
        } else {
            controlCenter.getAllBaggage().values().forEach(b -> System.out.println("  " + b));
        }
        System.out.println("---- Belts ----");
        controlCenter.getBelts().forEach((belt, flightId) ->
                System.out.println("  " + belt + " -> " + (flightId == null ? "free" : flightId)));
    }

    // ---------- Helper ----------
    private static Flight selectFlight() {
        System.out.print("Enter flight ID (e.g. FL1): ");
        String id = scanner.nextLine().trim();
        Flight flight = controlCenter.getFlight(id);
        if (flight == null) {
            System.out.println("No such flight.");
        }
        return flight;
    }
}
