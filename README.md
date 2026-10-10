# Airport Flight & Baggage Management System

A Spring Boot web application that simulates an airport workflow (creating flights, clearing them for landing, assigning baggage belts, processing baggage, notifying passengers) and uses it to demonstrate **13 classic design patterns** working together.

Built for the Design Patterns Laboratory course. Data is in-memory and the frontend is plain HTML/CSS/JS, so there is nothing to install besides Java and Maven.

## Features

- Create flights (Domestic / International / Cargo) with validation
- Clone an existing flight as a template for a new one
- Multi-step landing clearance (Operations, ATC, Security, Manager)
- Mark flights as landed; baggage belt and expected baggage slot are assigned automatically
- Re-assign belts using Normal or Priority rules, or pick one manually
- Process baggage (Regular / Priority, Manual / Automated handling)
- Live notifications to passengers, display board, staff and control center
- Undo the last update (restores a saved snapshot)
- Role-based access: Passenger (read-only), Staff, Admin

## Tech Stack

- Java 17, Spring Boot 3.3
- Maven
- JUnit 5
- Plain HTML, CSS and JavaScript frontend, served by Spring Boot

## Getting Started

Requires JDK 17+ and Maven 3.8+.

```bash
git clone https://github.com/iyersriram042006/airport-flight-baggage-system.git
cd airport-flight-baggage-system
mvn spring-boot:run
```

Open http://localhost:8080. Backend and frontend run together, no separate frontend server.

Run the tests:

```bash
mvn test
```

Demo logins (hardcoded, for lab use only):

| Username | Password | Role |
|---|---|---|
| staff1 | staff123 | Staff |
| admin1 | admin123 | Admin |
| passenger1 | pass123 | Passenger |

Without logging in you are a read-only Passenger.

## Design Patterns (13)

| # | Pattern | Used for |
|---|---|---|
| 1 | Singleton | One shared airport state |
| 2 | Factory Method | Creating the right flight subtype |
| 3 | Proxy | Role-based access control |
| 4 | Abstract Factory | Matching gate + baggage service per flight type |
| 5 | Bridge | Baggage type vs handling method |
| 6 | Chain of Responsibility | Landing clearance checks in sequence |
| 7 | Observer | Notifications on every status change |
| 8 | Builder | Building and validating a flight |
| 9 | Prototype | Cloning a flight |
| 10 | Mediator | Coordinating airport departments |
| 11 | Template Method | Fixed baggage processing workflow |
| 12 | Strategy | Normal vs Priority belt assignment |
| 13 | Memento | Undo last update |

Classes, locations and how the patterns cooperate: [docs/PATTERNS.md](docs/PATTERNS.md)

## Workflow

```
Create (Builder -> Factory Method -> Abstract Factory resources)
  -> Clear  [Proxy -> Mediator -> Chain: Operations -> ATC -> Security -> Manager]
  -> Land   [Proxy -> Mediator: ATC lands -> belt (Strategy) + expected slot -> Control Center logs]
  -> Process baggage [Template Method + Bridge] -> BAGGAGE_AVAILABLE
  -> Undo last update [Memento]
  Observer notifications fire on every status change.
```

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
    │   │   ├── service/           AirportService (ties patterns together)
    │   │   ├── dto/               request objects
    │   │   ├── model/             Flight, Baggage, enums
    │   │   ├── singleton/         AirportControlCenter
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
        └── PatternsTest.java      21 tests covering all 13 patterns
```

## Documentation

- [docs/PATTERNS.md](docs/PATTERNS.md): pattern map and how they cooperate
- [docs/API.md](docs/API.md): REST API reference
- [docs/DEMO.md](docs/DEMO.md): demonstration script

## Limitations

- In-memory only: data and undo history reset on restart.
- Authentication is hardcoded demo users with a client-supplied `X-Role` header. Fine for a lab, not secure.
- Expected baggage slot is simply landing time + 15 to 30 minutes.
- Belts B1, B2 are treated as priority belts; B3, B4 as regular.

## Project History

This project was presented in two stages:

1. **Presentation 1:** console application demonstrating 7 patterns (Singleton, Factory Method, Proxy, Chain of Responsibility, Abstract Factory, Bridge, Observer). Preserved at git tag `v1-terminal-7-patterns`:
   ```bash
   git checkout v1-terminal-7-patterns
   ```
2. **Presentation 2:** this Spring Boot web application with 13 patterns (adds Builder, Prototype, Mediator, Template Method, Strategy, Memento).

## Author

Sriram, Ramdeobaba University (RBU), Nagpur
