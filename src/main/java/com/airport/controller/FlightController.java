package com.airport.controller;

import com.airport.dto.BaggageStatusRequest;
import com.airport.dto.BeltRequest;
import com.airport.dto.CloneRequest;
import com.airport.dto.FlightRequest;
import com.airport.dto.LandingTimeRequest;
import com.airport.dto.SlotRequest;
import com.airport.model.Flight;
import com.airport.proxy.Role;
import com.airport.service.AirportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/flights")
@CrossOrigin(origins = "*")
public class FlightController {

    private final AirportService service;

    public FlightController(AirportService service) { this.service = service; }

    // The frontend sends the logged-in role in the X-Role header (default PASSENGER = read-only).
    private Role role(String header) {
        if (header == null || header.isBlank()) return Role.PASSENGER;
        try { return Role.valueOf(header.trim().toUpperCase()); }
        catch (IllegalArgumentException e) { return Role.PASSENGER; }
    }

    @GetMapping
    public Collection<Flight> list() { return service.listFlights(); }

    @GetMapping("/{id}")
    public Flight get(@PathVariable String id) { return service.getFlight(id); }

    @GetMapping("/{id}/history")
    public List<String> history(@PathVariable String id) { return service.getHistory(id); }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody FlightRequest req) {
        return ResponseEntity.ok(service.createFlight(req));
    }

    @PostMapping("/{id}/clone")
    public ResponseEntity<Flight> clone(@PathVariable String id, @RequestBody CloneRequest req,
                                        @RequestHeader(value = "X-Role", required = false) String r) {
        return ResponseEntity.ok(service.cloneFlight(id, req, role(r)));
    }

    @PostMapping("/{id}/clear")
    public ResponseEntity<Map<String, Object>> clear(@PathVariable String id,
                                                     @RequestHeader(value = "X-Role", required = false) String r) {
        return ResponseEntity.ok(service.clearFlight(id, role(r)));
    }

    @PostMapping("/{id}/land")
    public ResponseEntity<Flight> land(@PathVariable String id,
                                       @RequestHeader(value = "X-Role", required = false) String r) {
        return ResponseEntity.ok(service.landFlight(id, role(r)));
    }

    @PostMapping("/{id}/restore")
    public ResponseEntity<Flight> restore(@PathVariable String id,
                                          @RequestHeader(value = "X-Role", required = false) String r) {
        return ResponseEntity.ok(service.restoreFlight(id, role(r)));
    }

    @PutMapping("/{id}/baggage/belt")
    public ResponseEntity<Flight> assignBelt(@PathVariable String id, @RequestBody BeltRequest req,
                                             @RequestHeader(value = "X-Role", required = false) String r) {
        return ResponseEntity.ok(service.assignBelt(id, req.belt, req.strategy, role(r)));
    }

    @PutMapping("/{id}/baggage/slot")
    public ResponseEntity<Flight> setSlot(@PathVariable String id, @RequestBody SlotRequest req,
                                          @RequestHeader(value = "X-Role", required = false) String r) {
        return ResponseEntity.ok(service.setBaggageSlot(id, req.slot, role(r)));
    }

    @PutMapping("/{id}/baggage/status")
    public ResponseEntity<Map<String, Object>> processBaggage(@PathVariable String id, @RequestBody BaggageStatusRequest req,
                                                              @RequestHeader(value = "X-Role", required = false) String r) {
        return ResponseEntity.ok(service.processBaggage(id, req.baggageType, req.handlingMethod, role(r)));
    }

    @PutMapping("/{id}/landing-time")
    public ResponseEntity<Flight> landingTime(@PathVariable String id, @RequestBody LandingTimeRequest req,
                                              @RequestHeader(value = "X-Role", required = false) String r) {
        return ResponseEntity.ok(service.updateLandingTime(id, req.landingTime, role(r)));
    }
}
