package com.airport.service;

import com.airport.abstractfactory.AirportResourceFactory;
import com.airport.abstractfactory.BaggageService;
import com.airport.abstractfactory.DomesticAirportFactory;
import com.airport.abstractfactory.Gate;
import com.airport.abstractfactory.InternationalAirportFactory;
import com.airport.bridge.BaggageProcessor;
import com.airport.builder.FlightBuilder;
import com.airport.dto.CloneRequest;
import com.airport.dto.FlightRequest;
import com.airport.mediator.AirportMediator;
import com.airport.mediator.ClearanceResult;
import com.airport.memento.FlightHistory;
import com.airport.memento.SavedState;
import com.airport.model.Baggage;
import com.airport.model.Flight;
import com.airport.model.FlightStatus;
import com.airport.model.SlotCalculator;
import com.airport.observer.DisplayBoardObserver;
import com.airport.observer.NotificationLog;
import com.airport.observer.PassengerObserver;
import com.airport.observer.StaffObserver;
import com.airport.proxy.AirportSystemProxy;
import com.airport.proxy.Role;
import com.airport.singleton.AirportControlCenter;
import com.airport.strategy.BeltAssigner;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

// Application service: this is where all 13 patterns meet in real request flows.
// Business rules live here; controllers stay thin.
@Service
public class AirportService {

    // Singleton
    private final AirportControlCenter center = AirportControlCenter.getInstance();

    private final AirportSystemProxy proxy;                 // Proxy
    private final NotificationLog log;                      // Observer sink
    private final BeltAssigner beltAssigner;                // Strategy context
    private final AirportMediator mediator;                 // Mediator
    private final FlightHistory history;                    // Memento caretaker

    private final AtomicInteger flightCounter = new AtomicInteger(1);
    private final AtomicInteger baggageCounter = new AtomicInteger(1);

    public AirportService(AirportSystemProxy proxy, NotificationLog log, BeltAssigner beltAssigner,
                          AirportMediator mediator, FlightHistory history) {
        this.proxy = proxy;
        this.log = log;
        this.beltAssigner = beltAssigner;
        this.mediator = mediator;
        this.history = history;
        seedSampleFlights();
    }

    public static class NoSuchElementException extends RuntimeException {
        public NoSuchElementException(String message) { super(message); }
    }

    // ================= reads =================

    public Collection<Flight> listFlights() { return center.getAllFlights().values(); }

    public Flight getFlight(String id) {
        Flight f = center.getFlight(id);
        if (f == null) throw new NoSuchElementException("Flight not found: " + id);
        return f;
    }

    public Map<String, String> getBelts() { return center.getBelts(); }

    public Collection<Baggage> getAllBaggage() { return center.getAllBaggage().values(); }

    public List<String> getNotifications() { return log.getRecent(); }

    public List<String> getHistory(String id) {
        getFlight(id);
        return history.labels(id);
    }

    // ================= Builder + Factory Method + Abstract Factory + Observer =================

    public synchronized Map<String, Object> createFlight(FlightRequest req) {
        ensureUniqueNumber(req.flightNumber);
        if (req.baggageBelt != null && !req.baggageBelt.isBlank()) {
            String belt = req.baggageBelt.trim();
            if (!center.getBelts().containsKey(belt)) throw new IllegalArgumentException("Unknown belt: " + belt);
            if (center.getBelts().get(belt) != null) throw new IllegalStateException("Belt " + belt + " is already occupied");
        }

        // Builder assembles + validates; it asks the Factory Method for the right subtype.
        Flight flight = new FlightBuilder()
                .setFlightId("FL" + flightCounter.getAndIncrement())
                .setFlightType(req.flightType)
                .setFlightNumber(req.flightNumber)
                .setAirline(req.airline)
                .setOrigin(req.source)
                .setDestination(req.destination)
                .setLandingTime(req.landingTime)
                .setGate(req.gate)
                .setBaggageBelt(req.baggageBelt)
                .setExpectedBaggageSlot(req.expectedBaggageSlot)
                .build();

        register(flight);
        if (flight.getBaggageBelt() != null) center.assignBelt(flight.getBaggageBelt(), flight.getFlightId());
        log.add("Flight " + flight.getFlightNumber() + " created (" + flight.getCategory() + ").");

        // Abstract Factory: matching Gate + BaggageService family for this flight category.
        AirportResourceFactory resources = "International".equals(flight.getCategory())
                ? new InternationalAirportFactory() : new DomesticAirportFactory();
        Gate gate = resources.createGate();
        BaggageService baggageService = resources.createBaggageService();

        return Map.of("flight", flight, "gate", gate.describe(), "baggageService", baggageService.describe());
    }

    // ================= Prototype =================

    public synchronized Flight cloneFlight(String id, CloneRequest req, Role role) {
        Flight original = getFlight(id);
        proxy.performProtectedOperation("Clone flight " + original.getFlightNumber(), role);
        ensureUniqueNumber(req.flightNumber);

        Flight copy = original.cloneWith("FL" + flightCounter.getAndIncrement(), req.flightNumber.trim());
        if (notBlank(req.source)) copy.setSource(req.source.trim());
        if (notBlank(req.destination)) copy.setDestination(req.destination.trim());
        if (notBlank(req.gate)) copy.setGate(req.gate.trim());
        if (notBlank(req.landingTime)) {
            if (!SlotCalculator.isValidTime(req.landingTime)) throw new IllegalArgumentException("Landing time must look like '10:00 AM'");
            copy.setLandingTime(req.landingTime.trim());
        }
        register(copy);
        log.add("Flight " + copy.getFlightNumber() + " cloned from " + original.getFlightNumber() + ".");
        return copy;
    }

    // ================= Proxy + Mediator + Chain of Responsibility =================

    public synchronized Map<String, Object> clearFlight(String id, Role role) {
        Flight flight = getFlight(id);
        proxy.performProtectedOperation("Clear flight " + flight.getFlightNumber(), role);
        if (flight.getStatus() != FlightStatus.CREATED) {
            throw new IllegalStateException("Flight is not in a clearable state (current: " + flight.getStatus() + ")");
        }
        saveState(flight, "before clearance");
        ClearanceResult result = mediator.requestClearance(flight);   // Mediator -> Chain of Responsibility
        return Map.of("flight", flight, "approved", result.approved(), "steps", result.steps());
    }

    // Mediator: ATC lands -> Baggage Handling gets a belt (Strategy) + slot -> Control Center logs.
    public synchronized Flight landFlight(String id, Role role) {
        Flight flight = getFlight(id);
        proxy.performProtectedOperation("Land flight " + flight.getFlightNumber(), role);
        if (flight.getStatus() != FlightStatus.CLEARED) {
            throw new IllegalStateException("Flight must be CLEARED before it can land (current: " + flight.getStatus() + ")");
        }
        saveState(flight, "before landing");
        mediator.requestLanding(flight);
        return flight;
    }

    // ================= Strategy =================

    // strategy given -> automatic (NORMAL/PRIORITY); otherwise manual belt name.
    public synchronized Flight assignBelt(String id, String belt, String strategy, Role role) {
        Flight flight = getFlight(id);
        proxy.performProtectedOperation("Assign belt for " + flight.getFlightNumber(), role);
        if (flight.getStatus() != FlightStatus.LANDED && flight.getStatus() != FlightStatus.BAGGAGE_PROCESSING) {
            throw new IllegalStateException("Flight must have LANDED before a belt can be assigned (current: " + flight.getStatus() + ")");
        }
        saveState(flight, "before belt assignment");
        String chosen;
        if (notBlank(strategy)) {
            chosen = beltAssigner.assign(flight, strategy);
            log.add("Belt " + chosen + " assigned to " + flight.getFlightNumber() + " using " + strategy.toUpperCase() + " strategy.");
        } else {
            if (!notBlank(belt)) throw new IllegalArgumentException("Provide a belt or a strategy");
            if (!center.assignBelt(belt.trim(), flight.getFlightId())) throw new IllegalArgumentException("Unknown belt: " + belt);
            chosen = belt.trim();
            log.add("Belt " + chosen + " assigned manually to " + flight.getFlightNumber() + ".");
        }
        return flight;
    }

    // ================= Expected baggage slot =================

    public synchronized Flight setBaggageSlot(String id, String requestedSlot, Role role) {
        Flight flight = getFlight(id);
        proxy.performProtectedOperation("Set baggage slot for " + flight.getFlightNumber(), role);
        if (flight.getBaggageBelt() == null) throw new IllegalStateException("Assign a baggage belt first.");
        saveState(flight, "before slot change");
        flight.setBaggageArrivalSlot(notBlank(requestedSlot) ? requestedSlot.trim()
                : SlotCalculator.computeSlot(flight.getLandingTime()));
        if (flight.getStatus() == FlightStatus.LANDED) flight.setStatus(FlightStatus.BAGGAGE_PROCESSING);
        return flight;
    }

    // If the landing time changes, the expected slot follows (when a belt is already assigned).
    public synchronized Flight updateLandingTime(String id, String landingTime, Role role) {
        Flight flight = getFlight(id);
        proxy.performProtectedOperation("Update landing time for " + flight.getFlightNumber(), role);
        if (!SlotCalculator.isValidTime(landingTime)) throw new IllegalArgumentException("Landing time must look like '10:00 AM'");
        saveState(flight, "before landing-time change");
        flight.setLandingTime(landingTime.trim());
        if (flight.getBaggageBelt() != null) flight.setBaggageArrivalSlot(SlotCalculator.computeSlot(flight.getLandingTime()));
        return flight;
    }

    // ================= Bridge + Template Method + Observer =================

    public synchronized Map<String, Object> processBaggage(String id, String baggageType, String handlingMethod, Role role) {
        Flight flight = getFlight(id);
        proxy.performProtectedOperation("Process baggage for " + flight.getFlightNumber(), role);
        if (flight.getStatus() != FlightStatus.BAGGAGE_PROCESSING) {
            throw new IllegalStateException("Baggage processing has not started for this flight (current: " + flight.getStatus() + ")");
        }
        saveState(flight, "before baggage processing");

        // Bridge: combine WHAT (Regular/Priority) with HOW (Manual/Automated)...
        BaggageProcessor processor = BaggageProcessor.forType(baggageType, BaggageProcessor.implementorFor(handlingMethod));
        Baggage baggage = new Baggage("BG" + baggageCounter.getAndIncrement(), flight.getFlightId(),
                notBlank(baggageType) ? baggageType : "Regular", processor.getClass().getSimpleName());
        center.addBaggage(baggage);

        // ...then Template Method runs the fixed workflow (Observer fires at the end).
        List<String> steps = processor.process(baggage, flight);
        log.add("Baggage " + baggage.getBaggageId() + " processed (" + baggage.getBaggageType() + ") for " + flight.getFlightNumber() + ".");
        return Map.of("flight", flight, "baggage", baggage, "steps", steps);
    }

    // ================= Memento =================

    public synchronized Flight restoreFlight(String id, Role role) {
        Flight flight = getFlight(id);
        proxy.performProtectedOperation("Restore previous state of " + flight.getFlightNumber(), role);
        SavedState saved = history.pop(id);
        if (saved == null) throw new IllegalStateException("No saved state to restore for " + flight.getFlightNumber());

        flight.restore(saved.getSnapshot());                       // Observers notified inside restore()
        boolean beltOk = center.syncBeltWithFlight(flight);        // keep belt occupancy consistent
        center.removeBaggageForFlightExcept(id, saved.getBaggageIds());
        log.add("Flight " + flight.getFlightNumber() + " restored to state saved " + saved.getLabel()
                + (beltOk ? "." : " (its old belt was taken meanwhile, so no belt is assigned)."));
        return flight;
    }

    // ================= helpers =================

    private void saveState(Flight flight, String label) {
        history.save(flight.getFlightId(), new SavedState(flight.createSnapshot(), label,
                center.getBaggageIdsForFlight(flight.getFlightId())));
    }

    private void register(Flight flight) {
        flight.registerObserver(new PassengerObserver(log));
        flight.registerObserver(new DisplayBoardObserver(log));
        flight.registerObserver(new StaffObserver(log));
        center.addFlight(flight);
    }

    private void ensureUniqueNumber(String flightNumber) {
        if (!notBlank(flightNumber)) throw new IllegalArgumentException("Missing required flight property: flight number");
        for (Flight f : center.getAllFlights().values()) {
            if (f.getFlightNumber().equalsIgnoreCase(flightNumber.trim())) {
                throw new IllegalArgumentException("Flight number " + flightNumber.trim() + " already exists");
            }
        }
    }

    private static boolean notBlank(String s) { return s != null && !s.isBlank(); }

    private void seedSampleFlights() {
        seed("DOMESTIC", "AI-202", "Air India", "Mumbai", "Nagpur", "10:00 AM");
        seed("INTERNATIONAL", "6E-451", "IndiGo", "Dubai", "Delhi", "11:30 AM");
        seed("CARGO", "UK-803", "Vistara Cargo", "Chennai", "Nagpur", "1:00 PM");
    }

    private void seed(String type, String number, String airline, String from, String to, String time) {
        FlightRequest r = new FlightRequest();
        r.flightType = type; r.flightNumber = number; r.airline = airline;
        r.source = from; r.destination = to; r.landingTime = time;
        createFlight(r);
    }
}
