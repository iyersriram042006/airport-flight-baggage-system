# Airport Flight & Baggage Management System

**Design Patterns Lab mini-project (second presentation): 13 design patterns in one application.**
Java 17 + Spring Boot backend (in-memory data, no database) and a plain HTML/CSS/JS frontend
served by the same Spring Boot app.

## 1. Overview

Airport staff create flights, clear them for landing, land them, assign baggage belts, set the
expected baggage slot and process baggage. Passengers can view flights and baggage status.
Every status change produces notifications (passenger, display board, staff, control centre).

Example: `AI-202`, Domestic, landing 10:00 AM -> belt `B3` (auto) -> expected baggage `10:15 AM - 10:30 AM`.

## 2. Run it

Requires **JDK 17+** and **Maven 3.8+**.

```bash
mvn spring-boot:run          # starts backend AND frontend
# open http://localhost:8080
mvn test                     # runs the pattern tests
```

There is no separate frontend server: `src/main/resources/static/` is served by Spring Boot.

Demo logins (hardcoded, lab only): `staff1 / staff123`, `admin1 / admin123`, `passenger1 / pass123`.
Without logging in you are a read-only PASSENGER.

## 3. Pattern map (all 13)

| # | Pattern | Problem it solves here | Main classes | Where it runs |
|---|---|---|---|---|
| 1 | Singleton | One shared airport state | `singleton.AirportControlCenter` (`getInstance()`) | `AirportService` field; every flight/belt/baggage read and write |
| 2 | Factory Method | Create the right flight subtype | `factory.FlightFactory`, `Domestic/International/CargoFlightFactory` | called by `FlightBuilder.build()` |
| 3 | Proxy | Role-based access | `proxy.AirportSystemProxy` -> `AirportSystemImpl` | first line of every protected `AirportService` method |
| 4 | Abstract Factory | Matching Gate + BaggageService family | `abstractfactory.AirportResourceFactory`, `DomesticAirportFactory`, `InternationalAirportFactory` | `AirportService.createFlight` |
| 5 | Bridge | Baggage *type* vs handling *method* vary independently | `bridge.BaggageProcessor` (abstraction), `BaggageHandlingImplementor`, `Manual/AutomatedBaggageHandler` | `AirportService.processBaggage` |
| 6 | Chain of Responsibility | Landing clearance checks in sequence | `chain.ClearanceHandler`, `Operations/ATC/Security/ManagerHandler` | run by `ClearanceDepartment` |
| 7 | Observer | Notify passenger/display/staff on status change | `Flight` (subject), `observer.FlightObserver`, `Passenger/DisplayBoard/StaffObserver` | `Flight.setStatus()` / `Flight.restore()` |
| 8 | **Builder** | Flight has many required + optional fields | `builder.FlightBuilder` | `AirportService.createFlight` (also used for seed data) |
| 9 | **Prototype** | Create a new flight from an existing configuration | `prototype.FlightPrototype`, `Flight.cloneWith()` + `newBlank()` in each subtype | `AirportService.cloneFlight` |
| 10 | **Mediator** | Departments coordinate without knowing each other | `mediator.AirportControlMediator`, `ClearanceDepartment`, `ATCDepartment`, `BaggageHandlingDepartment`, `ControlCenterDepartment` | `clearFlight` and `landFlight` |
| 11 | **Template Method** | One fixed baggage workflow, varying steps | `BaggageProcessor.process()` (final), overridden `inspect()` / `determineHandling()` in `Regular/PriorityBaggageProcessor` | `AirportService.processBaggage` |
| 12 | **Strategy** | Interchangeable belt-assignment rules | `strategy.BeltAssignmentStrategy`, `NormalBeltAssignmentStrategy`, `PriorityBeltAssignmentStrategy`, context `BeltAssigner` | `assignBelt`, and the Baggage Handling department |
| 13 | **Memento** | Undo a wrong update from a real snapshot | `Flight.createSnapshot()/restore()` (originator), `memento.FlightSnapshot`, `SavedState`, `FlightHistory` (caretaker) | every state-changing action saves; `restoreFlight` undoes |

### How the patterns cooperate (and where they do NOT overlap)

* **Builder** assembles and validates a flight; **Factory Method** decides the subtype; **Prototype** copies configuration instead.
* **Mediator** coordinates departments; **Chain of Responsibility** still does the actual clearance checks (the mediator does not replace it).
* **Strategy** chooses *which belt*; **Template Method** fixes the *order of baggage steps*; **Bridge** supplies *how* the handling is physically done. `BaggageProcessor` is the one class that is both the Bridge abstraction and the Template Method base: Bridge = the `implementor` field, Template = the `final process()` method.
* **Observer** only sends notifications; **Memento** only saves/restores state. Restoring triggers Observer notifications like any other status change.

## 4. Workflow

```
Create (Builder -> Factory Method -> Abstract Factory resources)
  -> Clear  [Proxy -> Mediator -> Chain: Operations -> ATC -> Security -> Manager] -> CLEARED
  -> Land   [Proxy -> Mediator: ATC lands -> Baggage Handling assigns belt (Strategy NORMAL)
             + expected slot -> Control Center logs]            -> BAGGAGE_PROCESSING
  -> (optional) re-assign belt with PRIORITY strategy or manually; change slot
  -> Process baggage [Template Method + Bridge] -> BAGGAGE_AVAILABLE
  -> Undo last update [Memento] -> back to BAGGAGE_PROCESSING
  Observer notifications fire on every status change.
```

## 5. REST API

Protected endpoints read the role from the `X-Role` header (the frontend sets it after login).
STAFF/ADMIN required where marked. Errors return `{"error": "..."}` with 400 / 403 / 404.

| Method & path | Role | Purpose |
|---|---|---|
| `POST /api/auth/login` | - | `{username,password}` -> `{username,role}` |
| `GET /api/flights`, `GET /api/flights/{id}` | any | list / one flight |
| `POST /api/flights` | any* | **Builder**: `{flightType,flightNumber,airline,source,destination,landingTime,gate?,baggageBelt?,expectedBaggageSlot?}` |
| `POST /api/flights/{id}/clone` | STAFF/ADMIN | **Prototype**: `{flightNumber, destination?, source?, landingTime?, gate?}` |
| `POST /api/flights/{id}/clear` | STAFF/ADMIN | **Mediator + Chain**: returns `approved` and `steps` |
| `POST /api/flights/{id}/land` | STAFF/ADMIN | **Mediator**: lands, assigns belt + slot |
| `PUT /api/flights/{id}/baggage/belt` | STAFF/ADMIN | **Strategy**: `{strategy:"NORMAL"\|"PRIORITY"}` or manual `{belt:"B2"}` |
| `PUT /api/flights/{id}/baggage/slot` | STAFF/ADMIN | `{slot?}` (blank = landing time + 15..30 min) |
| `PUT /api/flights/{id}/baggage/status` | STAFF/ADMIN | **Template Method + Bridge**: `{baggageType,handlingMethod}` -> returns `steps` |
| `PUT /api/flights/{id}/landing-time` | STAFF/ADMIN | `{landingTime}`; slot recalculated if a belt is assigned |
| `POST /api/flights/{id}/restore` | STAFF/ADMIN | **Memento**: restore last saved state |
| `GET /api/flights/{id}/history` | any | saved-state labels (max 5, newest first) |
| `GET /api/belts`, `/api/baggage`, `/api/notifications` | any | belts + occupant, baggage records, observer log |

\* Flight creation was unrestricted in the original version; that behaviour is kept.

## 6. Tests

`src/test/java/com/airport/PatternsTest.java` (JUnit 5, no Spring context): 21 tests covering all
13 patterns, including: builder validation, clone independence (original untouched, status/belt reset),
mediator coordination and no duplicate notifications, normal vs priority belt rules and occupancy,
identical Template Method step order with differing steps, memento restore (flight state, belt release,
baggage removal, unrelated flights untouched, role check, 5-snapshot limit).

## 7. Demonstration script (second presentation)

1. Log in as `staff1`. **Create flight** `AI-305`, DOMESTIC, any route, `9:00 AM` (Builder; result shows Abstract Factory resources). Try leaving a field empty to show Builder validation.
2. Select `AI-202` -> **Clone flight** as `AI-203` with a new landing time (Prototype). Show both rows exist; clone is `CREATED` with no belt.
3. Select `AI-203` -> **Clear for landing**: Last-result panel shows the chain steps. Notifications panel shows Control Center (Mediator) + Observer lines.
4. **Mark landed**: belt + expected slot appear automatically (Mediator -> Baggage Handling -> Strategy NORMAL).
5. Choose *Auto belt: Priority* -> **Assign belt**: moves to a priority belt (Strategy). Try *Manual belt* on an occupied belt to see the rejection.
6. **Process baggage** (Regular/Manual, then another flight Priority/Automated): compare steps (Template Method) and the `[Manual]`/`[Automated]` line (Bridge).
7. **Undo last update**: status returns to `BAGGAGE_PROCESSING`, baggage record removed (Memento); history list shrinks.
8. Log out (passenger): action buttons are disabled; forcing a call returns 403 (Proxy).

## 8. Assumptions and limitations

* **In-memory only**: data and Memento history reset on restart; no database.
* Memento covers **flight state** (status, landing time, gate, belt, slot) and removes baggage records created after the snapshot. Baggage has no separate status history; its status is derived inside the processing workflow.
* Belts `B1, B2` are the "priority belts" (assumption); `B3, B4` are regular. NORMAL prefers regular belts, PRIORITY prefers priority belts; both fall back to any free belt. A belt can never be held by two flights.
* Auto-assignment on landing always uses NORMAL; use the Assign belt action to switch to PRIORITY.
* Expected slot is just landing time + 15 to 30 minutes (no prediction algorithm).
* Authentication is hardcoded demo users and a client-supplied `X-Role` header: fine for a lab, **not** secure.
* If a restored belt was taken by another flight in the meantime, the flight is restored without a belt (the log says so).
* The earlier console version (`src/` with `main.Main`) is a separate project; this web version replaces it as the primary app.
