# Airport Flight & Baggage Management System

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3-brightgreen)
![Build](https://img.shields.io/badge/build-Maven-blue)
![Tests](https://img.shields.io/badge/tests-21%20passing-success)
![Patterns](https://img.shields.io/badge/design%20patterns-13-purple)

A Spring Boot web application that simulates how an airport handles a flight from creation to baggage delivery, built to demonstrate **13 classic (GoF) design patterns working together in one realistic workflow**.

Staff create flights, clear them for landing, land them, assign baggage belts, set the expected baggage slot and process baggage. Passengers see live flight and baggage status. Every status change produces notifications for the passenger, the display board, staff and the control center.

Example: flight `AI-202` (Domestic) lands at 10:00 AM, gets belt `B3` automatically, and the expected baggage slot becomes `10:15 AM - 10:30 AM`.

Built for the Design Patterns Laboratory course.

## Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Getting Started](#getting-started)
- [How It Works](#how-it-works)
- [Design Patterns](#design-patterns)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [Testing](#testing)
- [Documentation](#documentation)
- [Limitations](#limitations)
- [Project History](#project-history)
- [Author](#author)

## Features

- Create Domestic, International or Cargo flights with input validation
- Clone an existing flight as a template for a new one
- Multi-step landing clearance: Operations, ATC, Security, Manager approval
- Landing automatically assigns a baggage belt and calculates the expected baggage slot
- Re-assign belts using Normal or Priority rules, or pick a belt manually
- Process baggage as Regular or Priority, with Manual or Automated handling
- Live notification log for passengers, display board, staff and control center
- Undo the last update using saved snapshots (up to 5 per flight)
- Role-based access: Passenger (read-only), Staff, Admin
- Single-page frontend served by the same Spring Boot app, no separate frontend server

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Backend | Spring Boot 3.3 (REST API) |
| Frontend | Plain HTML, CSS, JavaScript |
| Build | Maven |
| Testing | JUnit 5 |
| Storage | In-memory (no database) |

## Getting Started

Requires JDK 17+ and Maven 3.8+.

```bash
git clone https://github.com/iyersriram042006/airport-flight-baggage-system.git
cd airport-flight-baggage-system
mvn spring-boot:run
```

Open http://localhost:8080. Backend and frontend start together.

Run the tests:

```bash
mvn test
```

### Demo logins

Hardcoded, for lab use only.

| Username | Password | Role |
|---|---|---|
| staff1 | staff123 | Staff |
| admin1 | admin123 | Admin |
| passenger1 | pass123 | Passenger |

Without logging in you are a read-only Passenger. The app starts with three sample flights: `AI-202` (Domestic), `6E-451` (International) and `UK-803` (Cargo).

## How It Works

### Flight lifecycle

```mermaid
flowchart LR
    A[CREATED] -->|Clear for landing| B[CLEARED]
    A -->|Check fails| R[REJECTED]
    B -->|Land| C[LANDED]
    C --> D[BAGGAGE_PROCESSING]
    D -->|Process baggage| E[BAGGAGE_AVAILABLE]
    E -.->|Undo last update| D
```

### Request flow

```
Create  : Builder -> Factory Method -> Abstract Factory (gate + baggage service)
Clear   : Proxy -> Mediator -> Chain (Operations -> ATC -> Security -> Manager)
Land    : Proxy -> Mediator -> ATC lands -> Baggage Handling assigns belt (Strategy) + slot
Baggage : Proxy -> Template Method + Bridge (type x handling method)
Undo    : Proxy -> Memento restores the last snapshot
Always  : Observer notifies passenger, display board and staff on every status change
```

### Baggage belts

Four belts `B1` to `B4`. `B1` and `B2` are priority belts, `B3` and `B4` are regular. The Normal strategy prefers regular belts, the Priority strategy prefers priority belts, and both fall back to any free belt. A belt can never be held by two flights.

## Design Patterns

| # | Pattern | Problem it solves here | Main classes |
|---|---|---|---|
| 1 | Singleton | One shared airport state | `AirportControlCenter` |
| 2 | Factory Method | Create the right flight subtype | `FlightFactory` and its Domestic/International/Cargo subclasses |
| 3 | Proxy | Role-based access control | `AirportSystemProxy`, `AirportSystemImpl` |
| 4 | Abstract Factory | Matching gate + baggage service family | `AirportResourceFactory`, `DomesticAirportFactory`, `InternationalAirportFactory` |
| 5 | Bridge | Baggage type and handling method vary independently | `BaggageProcessor`, `BaggageHandlingImplementor` |
| 6 | Chain of Responsibility | Clearance checks in sequence | `ClearanceHandler` and its Operations/ATC/Security/Manager handlers |
| 7 | Observer | Notify on every status change | `Flight` (subject), `FlightObserver` and its Passenger/DisplayBoard/Staff observers |
| 8 | Builder | Many required and optional flight fields, validated | `FlightBuilder` |
| 9 | Prototype | Create a flight from an existing one | `FlightPrototype`, `Flight.cloneWith()` |
| 10 | Mediator | Departments coordinate without knowing each other | `AirportControlMediator` and the four departments |
| 11 | Template Method | Fixed baggage workflow with varying steps | `BaggageProcessor.process()` |
| 12 | Strategy | Interchangeable belt-assignment rules | `BeltAssignmentStrategy`, `BeltAssigner` |
| 13 | Memento | Undo a wrong update from a real snapshot | `FlightSnapshot`, `FlightHistory` |

Full details, including where each pattern runs and how they cooperate without overlapping, are in [docs/PATTERNS.md](docs/PATTERNS.md).

## Architecture

```mermaid
flowchart TD
    UI[Frontend: index.html + app.js] -->|REST + X-Role header| C[Controllers]
    C --> S[AirportService]
    S --> P[AirportSystemProxy]
    P --> I[AirportSystemImpl]
    S --> M[Mediator]
    M --> D1[Clearance Dept] --> CH[Chain of handlers]
    M --> D2[ATC Dept]
    M --> D3[Baggage Handling Dept] --> ST[Strategy]
    M --> D4[Control Center Dept]
    S --> B[Bridge + Template Method]
    S --> MM[Memento history]
    S --> CC[(AirportControlCenter singleton)]
    CC --> O[Observers]
```

Controllers stay thin and delegate to `AirportService`, which is where the patterns are wired together. All state lives in the `AirportControlCenter` singleton.

## Project Structure

```
.
├── pom.xml
├── README.md
├── docs/
│   ├── PATTERNS.md        pattern-by-pattern map
│   ├── API.md             REST endpoints
│   └── DEMO.md            presentation walkthrough
└── src/
    ├── main/
    │   ├── java/com/airport/
    │   │   ├── AirportApplication.java
    │   │   ├── controller/        REST controllers
    │   │   ├── service/           AirportService
    │   │   ├── dto/               request objects
    │   │   ├── model/             Flight, Baggage, status enums
    │   │   ├── singleton/         Singleton
    │   │   ├── factory/           Factory Method
    │   │   ├── abstractfactory/   Abstract Factory
    │   │   ├── builder/           Builder
    │   │   ├── prototype/         Prototype
    │   │   ├── proxy/             Proxy + auth
    │   │   ├── chain/             Chain of Responsibility
    │   │   ├── mediator/          Mediator
    │   │   ├── observer/          Observer
    │   │   ├── bridge/            Bridge + Template Method
    │   │   ├── strategy/          Strategy
    │   │   └── memento/           Memento
    │   └── resources/
    │       ├── application.properties
    │       └── static/            index.html, app.js, style.css
    └── test/java/com/airport/
        └── PatternsTest.java
```

## Testing

`PatternsTest` contains 21 JUnit 5 tests (no Spring context) covering all 13 patterns, including:

- Builder validation of required fields
- Clone independence: original untouched, clone starts as `CREATED` with no belt
- Mediator coordination without duplicate notifications
- Normal vs Priority belt rules and belt occupancy
- Template Method: identical step order, differing steps
- Memento: flight state restored, belt released, baggage record removed, unrelated flights untouched, role check, 5-snapshot limit

## Documentation

- [docs/PATTERNS.md](docs/PATTERNS.md): pattern map and how the patterns cooperate
- [docs/API.md](docs/API.md): REST API reference
- [docs/DEMO.md](docs/DEMO.md): step-by-step demonstration script

## Limitations

- In-memory only: flights, baggage and undo history reset on restart.
- Authentication uses hardcoded demo users and a client-supplied `X-Role` header. Fine for a lab, not secure for real use.
- Expected baggage slot is simply landing time plus 15 to 30 minutes, with no prediction logic.
- Auto-assignment on landing always uses the Normal strategy; use the Assign belt action to switch to Priority.
- If a restored belt was taken by another flight in the meantime, the flight is restored without a belt.

## Project History

This project was presented in two stages:

1. **Presentation 1:** console application demonstrating 7 patterns (Singleton, Factory Method, Proxy, Chain of Responsibility, Abstract Factory, Bridge, Observer). Preserved at git tag `v1-terminal-7-patterns`:
   ```bash
   git checkout v1-terminal-7-patterns
   ```
2. **Presentation 2:** this Spring Boot web application with 13 patterns (adds Builder, Prototype, Mediator, Template Method, Strategy, Memento).

## Author

Sriram, Ramdeobaba University (RBU), Nagpur
