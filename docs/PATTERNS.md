# Design Patterns Map

All 13 design patterns used in this project: what problem each solves, which classes implement it, and where it runs.

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
