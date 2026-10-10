# Demonstration Script

Step-by-step walkthrough used for the second presentation.

1. Log in as `staff1`. **Create flight** `AI-305`, DOMESTIC, any route, `9:00 AM` (Builder; result shows Abstract Factory resources). Try leaving a field empty to show Builder validation.
2. Select `AI-202` -> **Clone flight** as `AI-203` with a new landing time (Prototype). Show both rows exist; clone is `CREATED` with no belt.
3. Select `AI-203` -> **Clear for landing**: Last-result panel shows the chain steps. Notifications panel shows Control Center (Mediator) + Observer lines.
4. **Mark landed**: belt + expected slot appear automatically (Mediator -> Baggage Handling -> Strategy NORMAL).
5. Choose *Auto belt: Priority* -> **Assign belt**: moves to a priority belt (Strategy). Try *Manual belt* on an occupied belt to see the rejection.
6. **Process baggage** (Regular/Manual, then another flight Priority/Automated): compare steps (Template Method) and the `[Manual]`/`[Automated]` line (Bridge).
7. **Undo last update**: status returns to `BAGGAGE_PROCESSING`, baggage record removed (Memento); history list shrinks.
8. Log out (passenger): action buttons are disabled; forcing a call returns 403 (Proxy).
