# REST API

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
