# Airport Flight & Baggage Management System

A small Java console application built for a Design Patterns laboratory.
It simulates a simplified airport workflow — creating flights, clearing
them for landing, assigning baggage belts, and notifying passengers —
purely as a vehicle to clearly demonstrate **seven classic design
patterns**. It is intentionally not a full, production-style airport
system.

## 1. Project Overview

The app manages a handful of flights through this basic flow:

```
Flight Created
      |
Flight Clearance
      |
Flight Lands
      |
Baggage Processing
      |
Baggage Belt Assigned
      |
Expected Baggage Arrival Slot
      |
Passenger receives status updates
```

Everything runs from a single console menu, with hardcoded sample data
(no database, no external services).

## 2. Features

- Create flights (Domestic / International / Cargo)
- View all flights and their current status
- Basic staff/admin login (hardcoded users)
- Multi-step flight clearance/approval before landing
- Mark flights as landed
- Assign a baggage belt to a landed flight
- Set an expected baggage arrival time slot
- Process and update baggage status (regular/priority, manual/automated)
- Automatic status notifications to passengers, the display board, and staff
- View full flight and baggage details

## 3. Design Patterns Used

1. Singleton
2. Factory Method
3. Proxy
4. Chain of Responsibility
5. Abstract Factory
6. Bridge
7. Observer

## 4. Where Each Pattern Is Implemented

| Pattern | Package | Key Classes |
|---|---|---|
| Singleton | `singleton` | `AirportControlCenter` |
| Factory Method | `factory` | `FlightFactory`, `DomesticFlightFactory`, `InternationalFlightFactory`, `CargoFlightFactory` (products in `model`) |
| Proxy | `proxy` | `AirportSystem`, `AirportSystemImpl`, `AirportSystemProxy`, `AuthService`, `User`, `Role` |
| Chain of Responsibility | `chain` | `ClearanceHandler`, `OperationsHandler`, `ATCHandler`, `SecurityHandler`, `ManagerHandler` |
| Abstract Factory | `abstractfactory` | `AirportResourceFactory`, `DomesticAirportFactory`, `InternationalAirportFactory`, `Gate`, `BaggageService` |
| Bridge | `bridge` | `BaggageProcessor` (+ `RegularBaggageProcessor`, `PriorityBaggageProcessor`), `BaggageHandlingImplementor` (+ `ManualBaggageHandler`, `AutomatedBaggageHandler`) |
| Observer | `observer`, `model.Flight` | `FlightObserver`, `PassengerObserver`, `DisplayBoardObserver`, `StaffObserver` |

`model` holds shared domain classes (`Flight`, `DomesticFlight`,
`InternationalFlight`, `CargoFlight`, `Baggage`, and status enums).
`main.Main` wires everything together behind the console menu.

## 5. Why Each Pattern Was Chosen

- **Singleton** — The airport needs exactly one shared source of truth
  for flights, belts, and baggage. `AirportControlCenter` guarantees
  every part of the app reads and writes the same state instead of
  juggling separate, inconsistent copies.

- **Factory Method** — Flight creation logic differs per type, but the
  console menu shouldn't need to know about `DomesticFlight`,
  `InternationalFlight`, or `CargoFlight` directly. `FlightFactory`
  hides that decision behind `createFlight(...)`, so adding a new
  flight type later only means adding a new factory subclass.

- **Proxy** — Several operations (clearing a flight, assigning belts,
  updating baggage) should only be performed by authorized staff.
  `AirportSystemProxy` sits in front of the real system and checks the
  logged-in user's role before forwarding any request, without the
  real system needing to know anything about authentication.

- **Chain of Responsibility** — Flight clearance is a sequence of
  independent checks (schedule, ATC, security, manager approval).
  Modeling this as a chain of handlers avoids one large conditional
  block and makes it trivial to add, remove, or reorder checks.

- **Abstract Factory** — Domestic and international flights need
  *matching families* of resources (a gate and a baggage service that
  belong together). The Abstract Factory guarantees a domestic flight
  can never end up with an international gate paired with a domestic
  baggage service by mistake.

- **Bridge** — Baggage has two independent dimensions: *what kind* of
  baggage it is (regular/priority) and *how* it's handled
  (manual/automated). Bridge lets both dimensions vary and grow
  independently instead of needing a new class for every combination
  (e.g. `PriorityAutomatedBaggage`, `RegularManualBaggage`, ...).

- **Observer** — Passengers, the display board, and staff all need to
  react whenever a flight's status changes, without the `Flight`
  class needing to know who they are. Observer decouples "something
  changed" from "who cares about it."

## 6. How to Run the Project

Requires a Java JDK (8+).

```bash
# From the project root (folder containing "src")
javac -d bin $(find src -name "*.java")
java -cp bin main.Main
```

Sample hardcoded logins (used at menu option 3):

| Username | Password | Role |
|---|---|---|
| passenger1 | pass123 | PASSENGER |
| staff1 | staff123 | STAFF |
| admin1 | admin123 | ADMIN |

Only `STAFF` and `ADMIN` can perform protected operations (clearance,
landing, belt assignment, baggage slot/status updates).

## 7. Example Console Output

A typical run: create a domestic flight, log in as staff, clear it,
land it, assign a belt, set a baggage slot, and process baggage.

```
========================================
   Welcome to the Airport Flight &
      Baggage Management System
========================================
Sample logins -> staff1/staff123 (STAFF), admin1/admin123 (ADMIN), passenger1/pass123 (PASSENGER)

========================================
     AIRPORT FLIGHT & BAGGAGE SYSTEM
========================================
1. Create Flight
2. View Flights
3. Authenticate User
4. Clear Flight for Landing
5. Mark Flight as Landed
6. Assign Baggage Belt
7. Set Baggage Arrival Slot
8. Update Baggage Status
9. View Flight & Baggage Details
10. Exit
Enter choice: 1
Enter flight type (Domestic/International/Cargo): Domestic
Enter flight number (e.g. AI-202): AI-202
Created: [FL1] AI-202 (Domestic) - Status: CREATED | Belt: - | Baggage Slot: -
Provisioning matching airport resources:
  Domestic Gate assigned (local terminal, no customs).
  Domestic Baggage Service ready (standard handling).

Enter choice: 3
Username: staff1
Password: staff123
Login successful. Welcome staff1 (STAFF)

Enter choice: 4
Enter flight ID (e.g. FL1): FL1
  [AirportSystem] Executing protected operation: Clear Flight AI-202
Running clearance chain for AI-202:
  -> OperationsHandler approved.
  -> ATCHandler approved.
  -> SecurityHandler approved.
  -> ManagerHandler approved.
  [Notify Passenger Guest] Flight AI-202 status is now: CLEARED
  [Airport Display Board] Updated: Flight AI-202 -> CLEARED
  [Airport Staff Alert] Flight AI-202 changed to CLEARED
Flight AI-202 is CLEARED for landing.

Enter choice: 5
Enter flight ID (e.g. FL1): FL1
  [AirportSystem] Executing protected operation: Mark Flight AI-202 as Landed
  [Notify Passenger Guest] Flight AI-202 status is now: LANDED
  [Airport Display Board] Updated: Flight AI-202 -> LANDED
  [Airport Staff Alert] Flight AI-202 changed to LANDED
Flight AI-202 has LANDED.

Enter choice: 6
Enter flight ID (e.g. FL1): FL1
  [AirportSystem] Executing protected operation: Assign Baggage Belt for AI-202
Available belts: [Belt-1, Belt-2, Belt-3]
Enter belt name to assign: Belt-1
Belt Belt-1 assigned to flight AI-202.

Enter choice: 7
Enter flight ID (e.g. FL1): FL1
  [AirportSystem] Executing protected operation: Set Baggage Arrival Slot for AI-202
Enter expected baggage arrival slot (e.g. 10:15 AM - 10:30 AM): 10:15 AM - 10:30 AM
  [Notify Passenger Guest] Flight AI-202 status is now: BAGGAGE_PROCESSING
  [Airport Display Board] Updated: Flight AI-202 -> BAGGAGE_PROCESSING
  [Airport Staff Alert] Flight AI-202 changed to BAGGAGE_PROCESSING
Baggage arrival slot set. Baggage processing has started.

Enter choice: 8
Enter flight ID (e.g. FL1): FL1
  [AirportSystem] Executing protected operation: Update Baggage Status for AI-202
Baggage type (Regular/Priority): Priority
Handling method (Manual/Automated): Automated
  Processing PRIORITY baggage BG1 (expedited):
  [Automated] Conveyor system is auto-routing baggage BG1
Baggage BG1 is now ARRIVING.
  [Notify Passenger Guest] Flight AI-202 status is now: BAGGAGE_AVAILABLE
  [Airport Display Board] Updated: Flight AI-202 -> BAGGAGE_AVAILABLE
  [Airport Staff Alert] Flight AI-202 changed to BAGGAGE_AVAILABLE
Baggage BG1 is now AVAILABLE.

Enter choice: 9
---- Flights ----
  [FL1] AI-202 (Domestic) - Status: BAGGAGE_AVAILABLE | Belt: Belt-1 | Baggage Slot: 10:15 AM - 10:30 AM
---- Baggage ----
  Baggage[BG1] for Flight[FL1] - Status: AVAILABLE
---- Belts ----
  Belt-1 -> FL1
  Belt-2 -> free
  Belt-3 -> free

Enter choice: 10
Thank you for using the Airport Flight & Baggage Management System.
```

## Project Structure

```
src/
 ├── singleton/        (Singleton)
 ├── factory/           (Factory Method)
 ├── proxy/              (Proxy)
 ├── chain/               (Chain of Responsibility)
 ├── abstractfactory/      (Abstract Factory)
 ├── bridge/                 (Bridge)
 ├── observer/                 (Observer)
 ├── model/                       (shared domain classes)
 └── main/                            (console entry point)
```
