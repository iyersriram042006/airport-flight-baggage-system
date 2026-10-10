package com.airport;

import com.airport.builder.FlightBuilder;
import com.airport.dto.BeltRequest;
import com.airport.dto.CloneRequest;
import com.airport.dto.FlightRequest;
import com.airport.mediator.AirportControlMediator;
import com.airport.memento.FlightHistory;
import com.airport.model.Flight;
import com.airport.model.FlightStatus;
import com.airport.observer.NotificationLog;
import com.airport.proxy.AccessDeniedException;
import com.airport.proxy.AirportSystemImpl;
import com.airport.proxy.AirportSystemProxy;
import com.airport.proxy.Role;
import com.airport.service.AirportService;
import com.airport.singleton.AirportControlCenter;
import com.airport.strategy.BeltAssigner;
import com.airport.strategy.NormalBeltAssignmentStrategy;
import com.airport.strategy.PriorityBeltAssignmentStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

// Plain JUnit 5 tests (no Spring context needed). Seed data: FL1=AI-202, FL2=6E-451, FL3=UK-803.
class PatternsTest {

    private AirportService service;
    private NotificationLog log;

    @BeforeEach
    void setUp() {
        AirportControlCenter.getInstance().reset();
        log = new NotificationLog();
        BeltAssigner assigner = new BeltAssigner(List.of(new NormalBeltAssignmentStrategy(), new PriorityBeltAssignmentStrategy()));
        service = new AirportService(new AirportSystemProxy(new AirportSystemImpl()), log, assigner,
                new AirportControlMediator(log, assigner), new FlightHistory());
    }

    private void clearAndLand(String id) {
        service.clearFlight(id, Role.STAFF);
        service.landFlight(id, Role.STAFF);
    }

    private CloneRequest cloneReq(String number) {
        CloneRequest r = new CloneRequest();
        r.flightNumber = number;
        return r;
    }

    private boolean logHas(String text) {
        return log.getRecent().stream().anyMatch(s -> s.contains(text));
    }

    // ---------- original seven ----------
    @Test
    void singletonReturnsSameInstance() {
        assertSame(AirportControlCenter.getInstance(), AirportControlCenter.getInstance());
    }

    @Test
    void factoryMethodCreatesCorrectSubtypes() {
        assertEquals("Domestic", service.getFlight("FL1").getCategory());
        assertEquals("International", service.getFlight("FL2").getCategory());
        assertEquals("Cargo", service.getFlight("FL3").getCategory());
    }

    @Test
    void proxyBlocksPassengerButAllowsStaff() {
        assertThrows(AccessDeniedException.class, () -> service.clearFlight("FL1", Role.PASSENGER));
        assertEquals(FlightStatus.CREATED, service.getFlight("FL1").getStatus());
        service.clearFlight("FL1", Role.STAFF);
        assertEquals(FlightStatus.CLEARED, service.getFlight("FL1").getStatus());
    }

    @Test
    void chainStopsAtFirstFailingHandler() {
        Flight f = service.getFlight("FL1");
        List<String> ok = com.airport.chain.ClearanceHandler.newReasonList();
        assertTrue(com.airport.chain.ClearanceHandler.buildStandardChain().handle(f, ok));
        assertEquals(4, ok.size());                                   // Operations, ATC, Security, Manager

        f.setStatus(FlightStatus.CLEARED);                            // ATC rule now fails
        List<String> bad = com.airport.chain.ClearanceHandler.newReasonList();
        assertFalse(com.airport.chain.ClearanceHandler.buildStandardChain().handle(f, bad));
        assertEquals(2, bad.size());                                  // stopped at ATC; Security/Manager never ran
        assertTrue(bad.get(1).contains("ATCHandler: REJECTED"));
    }

    @Test
    void abstractFactoryMatchesResourceFamilyToFlight() {
        FlightRequest r = new FlightRequest();
        r.flightType = "INTERNATIONAL"; r.flightNumber = "EK-500"; r.source = "Dubai";
        r.destination = "Nagpur"; r.landingTime = "9:00 AM";
        Map<String, Object> res = service.createFlight(r);
        assertTrue(res.get("gate").toString().contains("International"));
        assertTrue(res.get("baggageService").toString().contains("International"));
    }

    @Test
    void observersReceiveStatusChanges() {
        service.clearFlight("FL1", Role.STAFF);
        assertTrue(logHas("Passenger notified: Flight AI-202"));
        assertTrue(logHas("Display Board updated: Flight AI-202"));
        assertTrue(logHas("Staff alert: Flight AI-202"));
    }

    // ---------- Builder ----------
    @Test
    void builderBuildsFlightWithOptionalFields() {
        Flight f = new FlightBuilder().setFlightId("X1").setFlightType("domestic").setFlightNumber("IX-1")
                .setOrigin("A").setDestination("B").setLandingTime("2:30 PM").setGate("G7").build();
        assertEquals("Domestic", f.getCategory());
        assertEquals("G7", f.getGate());
        assertEquals(FlightStatus.CREATED, f.getStatus());
    }

    @Test
    void builderRejectsInvalidConfigurations() {
        assertThrows(IllegalArgumentException.class, () -> new FlightBuilder().setFlightId("X").setFlightType("DOMESTIC").build());
        assertThrows(IllegalArgumentException.class, () -> new FlightBuilder().setFlightId("X").setFlightType("DOMESTIC")
                .setFlightNumber("N").setOrigin("A").setDestination("B").setLandingTime("noon-ish").build());
    }

    @Test
    void creatingDuplicateFlightNumberIsRejected() {
        FlightRequest r = new FlightRequest();
        r.flightType = "DOMESTIC"; r.flightNumber = "ai-202"; r.source = "A"; r.destination = "B"; r.landingTime = "9:00 AM";
        assertThrows(IllegalArgumentException.class, () -> service.createFlight(r));
    }

    // ---------- Prototype ----------
    @Test
    void cloneIsADistinctIndependentFlight() {
        clearAndLand("FL1");                                          // original has belt/slot/status
        CloneRequest req = cloneReq("AI-203");
        req.landingTime = "12:00 PM";
        req.destination = "Pune";
        Flight original = service.getFlight("FL1");
        Flight copy = service.cloneFlight("FL1", req, Role.STAFF);

        assertNotSame(original, copy);
        assertNotEquals(original.getFlightId(), copy.getFlightId());
        assertEquals("Domestic", copy.getCategory());                 // subtype preserved
        assertEquals("Air India", copy.getAirline());                 // configuration copied
        assertEquals("Pune", copy.getDestination());                  // override applied
        assertEquals(FlightStatus.CREATED, copy.getStatus());         // operational state reset
        assertNull(copy.getBaggageBelt());
        assertNull(copy.getBaggageArrivalSlot());
        // original untouched
        assertEquals("Nagpur", original.getDestination());
        assertEquals("10:00 AM", original.getLandingTime());
        assertEquals(FlightStatus.BAGGAGE_PROCESSING, original.getStatus());
        // both stay independently editable
        service.updateLandingTime(copy.getFlightId(), "1:15 PM", Role.STAFF);
        assertEquals("10:00 AM", original.getLandingTime());
    }

    @Test
    void cloneNeedsUniqueNumberAndStaffRole() {
        assertThrows(IllegalArgumentException.class, () -> service.cloneFlight("FL1", cloneReq("AI-202"), Role.STAFF));
        assertThrows(AccessDeniedException.class, () -> service.cloneFlight("FL1", cloneReq("AI-999"), Role.PASSENGER));
    }

    // ---------- Mediator ----------
    @Test
    void landingIsCoordinatedThroughMediator() {
        clearAndLand("FL1");
        Flight f = service.getFlight("FL1");
        assertEquals(FlightStatus.BAGGAGE_PROCESSING, f.getStatus());
        assertEquals("B3", f.getBaggageBelt());                       // normal strategy: regular belt first
        assertEquals("10:15 AM - 10:30 AM", f.getBaggageArrivalSlot());
        assertTrue(logHas("Control Center: Clearance Department approved AI-202"));
        assertTrue(logHas("Control Center: ATC confirmed landing of AI-202"));
        assertTrue(logHas("Control Center: Baggage Handling assigned belt B3"));
    }

    @Test
    void mediatorDoesNotDuplicateNotifications() {
        clearAndLand("FL1");
        long landedMsgs = log.getRecent().stream().filter(s -> s.contains("Passenger notified: Flight AI-202 status is now LANDED")).count();
        assertEquals(1L, landedMsgs);
    }

    // ---------- Strategy ----------
    @Test
    void priorityStrategyUsesPriorityBeltsAndRespectsOccupancy() {
        clearAndLand("FL1");                                          // mediator gave B3 (normal)
        service.assignBelt("FL1", null, "PRIORITY", Role.STAFF);
        assertEquals("B1", service.getFlight("FL1").getBaggageBelt());
        assertNull(service.getBelts().get("B3"));                     // old belt released

        clearAndLand("FL2");
        service.assignBelt("FL2", null, "PRIORITY", Role.STAFF);
        assertEquals("B2", service.getFlight("FL2").getBaggageBelt());

        clearAndLand("FL3");                                          // priority belts full -> falls back
        service.assignBelt("FL3", null, "PRIORITY", Role.STAFF);
        assertEquals("B3", service.getFlight("FL3").getBaggageBelt());
    }

    @Test
    void normalStrategyAvoidsPriorityBelts() {
        clearAndLand("FL1");
        service.assignBelt("FL1", null, "NORMAL", Role.STAFF);
        assertEquals("B3", service.getFlight("FL1").getBaggageBelt());
    }

    @Test
    void occupiedBeltCannotBeAssignedManually() {
        clearAndLand("FL1");                                          // FL1 holds B3
        clearAndLand("FL2");
        assertThrows(IllegalStateException.class, () -> service.assignBelt("FL2", "B3", null, Role.STAFF));
        assertThrows(IllegalArgumentException.class, () -> service.assignBelt("FL2", null, "FASTEST", Role.STAFF));
    }

    // ---------- Template Method (+ Bridge) ----------
    @Test
    @SuppressWarnings("unchecked")
    void regularAndPriorityBaggageShareOneWorkflow() {
        clearAndLand("FL1");
        clearAndLand("FL2");
        List<String> regular = (List<String>) service.processBaggage("FL1", "Regular", "Manual", Role.STAFF).get("steps");
        List<String> priority = (List<String>) service.processBaggage("FL2", "Priority", "Automated", Role.STAFF).get("steps");

        assertEquals(8, regular.size());
        assertEquals(regular.size(), priority.size());
        for (int i = 0; i < regular.size(); i++) {                    // same numbered order
            assertEquals(regular.get(i).substring(0, 2), priority.get(i).substring(0, 2));
        }
        assertNotEquals(regular.get(2), priority.get(2));             // inspect differs
        assertNotEquals(regular.get(3), priority.get(3));             // handling requirement differs
        assertTrue(regular.get(4).contains("Manual"));                // Bridge: HOW is independent of WHAT
        assertTrue(priority.get(4).contains("Automated"));
        assertEquals(FlightStatus.BAGGAGE_AVAILABLE, service.getFlight("FL1").getStatus());
    }

    // ---------- Memento ----------
    @Test
    void restoreBringsBackSavedStateAndOnlyThatFlight() {
        clearAndLand("FL1");
        clearAndLand("FL2");
        service.processBaggage("FL1", "Regular", "Manual", Role.STAFF);
        assertEquals(FlightStatus.BAGGAGE_AVAILABLE, service.getFlight("FL1").getStatus());
        assertEquals(1, service.getAllBaggage().size());

        Flight restored = service.restoreFlight("FL1", Role.STAFF);

        assertEquals(FlightStatus.BAGGAGE_PROCESSING, restored.getStatus());
        assertEquals("B3", restored.getBaggageBelt());
        assertEquals(0, service.getAllBaggage().size());              // baggage record created after snapshot removed
        assertEquals(FlightStatus.BAGGAGE_PROCESSING, service.getFlight("FL2").getStatus()); // unrelated flight untouched
        assertTrue(logHas("Passenger notified: Flight AI-202 status is now BAGGAGE_PROCESSING")); // observers told
    }

    @Test
    void restoreAfterLandingReleasesBelt() {
        clearAndLand("FL1");
        service.restoreFlight("FL1", Role.STAFF);                     // undo landing
        Flight f = service.getFlight("FL1");
        assertEquals(FlightStatus.CLEARED, f.getStatus());
        assertNull(f.getBaggageBelt());
        assertNull(service.getBelts().get("B3"));
    }

    @Test
    void restoreNeedsStaffRoleAndAHistory() {
        assertThrows(AccessDeniedException.class, () -> service.restoreFlight("FL1", Role.PASSENGER));
        assertThrows(IllegalStateException.class, () -> service.restoreFlight("FL1", Role.STAFF));
        service.clearFlight("FL1", Role.STAFF);
        assertEquals(1, service.getHistory("FL1").size());
    }

    @Test
    void historyKeepsOnlyRecentSnapshots() {
        clearAndLand("FL1");
        for (int i = 0; i < 8; i++) service.setBaggageSlot("FL1", "10:20 AM - 10:40 AM", Role.STAFF);
        assertEquals(5, service.getHistory("FL1").size());
    }
}
